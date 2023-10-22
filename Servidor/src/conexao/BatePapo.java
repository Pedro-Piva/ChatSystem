package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author pedro
 */
public class BatePapo extends Thread {

    private final Servidor server;
    private final DataOutputStream fluxoSaida;
    private final DataInputStream fluxoEntrada;
    private final String login;
    private final Conexao conexao;

    public BatePapo(DataOutputStream fluxoSaida, DataInputStream fluxoEntrada, String login, Servidor server, Conexao conexao) {
        this.fluxoSaida = fluxoSaida;
        this.fluxoEntrada = fluxoEntrada;
        this.login = login;
        this.server = server;
        this.conexao = conexao;
    }

    public Conexao nomeValido(String nome) {
        for (Conexao c : server.getConexoes()) {
            if (c.getLogin() != null) {
                if (c.getLogin().equals(nome)) {
                    return c;
                }
            }
        }
        return null;
    }

    public void printUsuarios(DataOutputStream fluxoSaida) throws IOException {
        fluxoSaida.writeUTF("Usuarios Online: ");
        for (Conexao c : server.getConexoes()) {
            if (c.isOnline() && !c.getLogin().equals(login)) {
                fluxoSaida.writeUTF("\t\t" + c.getLogin());
            }
        }
        fluxoSaida.writeUTF("Grupos disponiveis: ");
        for (Conexao c : server.getConexoes()) {
            if (c instanceof Grupo grupo) {
                if (grupo.ehMembro(conexao)) {
                    fluxoSaida.writeUTF("\t\t" + grupo.getLogin());
                }
            }
        }
    }

    public boolean estaEmGrupo() {
        for (Conexao c : conexao.getServer().getConexoes()) {
            if (c instanceof Grupo g) {
                if (g.ehMembro(conexao)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                printUsuarios(fluxoSaida);
                fluxoSaida.writeUTF("SERVIDOR> Pressione Enter para ver os Usuarios novamente.");
                fluxoSaida.writeUTF("SERVIDOR> Escreva 'grupo' para criar um grupo");
                fluxoSaida.writeUTF("SERVIDOR> Escolha com quem Falar:");
                String nome = fluxoEntrada.readUTF();
                Conexao c = nomeValido(nome);
                if (c != null) {
                    if (c instanceof User) {
                        fluxoSaida.writeUTF("SERVIDOR> Falando com " + nome);
                        Conversa cvs = new Conversa(login, c.getLogin(), fluxoEntrada, c, conexao);
                        cvs.conversar();
                        System.out.println(login + " Saiu da Conversa");
                        fluxoSaida.writeUTF("SERVIDOR> Saiu da Conversa");
                    } else if (c instanceof Grupo grupo) {
                        fluxoSaida.writeUTF("SERVIDOR> Falando no Grupo " + nome);
                        ConversaGrupo cg = new ConversaGrupo(grupo, conexao, this.login);
                        fluxoSaida.writeUTF("SERVIDOR> Se quiser sair do grupo escreva: sair do grupo");
                        fluxoSaida.writeUTF("SERVIDOR> Se quiser sair da conversa escreva: sair");
                        fluxoSaida.writeUTF("SERVIDOR> Se quiser adicionar novos membros escreva: add");
                        cg.conversarGrupo();
                        System.out.println(login + " Saiu da Conversa");
                        fluxoSaida.writeUTF("SERVIDOR> Saiu da Conversa do Grupo");
                    }
                } else if (nome.equals("grupo")) {
                    Grupo g = new Grupo(conexao.getSocket(), server, this.login, server.getPort());
                    server.addConexao(g);
                } else if (nome.equals("desconectar")) {
                    if (estaEmGrupo()) {
                        fluxoSaida.writeUTF("SERVIDOR> Saia dos Grupos antes de desconectar");
                    } else {
                        fluxoSaida.writeUTF("desconectado");
                        break;
                    }
                } else {
                    fluxoSaida.writeUTF("SERVIDOR> Escolha uma Opcao valida ou escreva 'desconectar' para sair");
                }
            } catch (IOException ex) {
                System.out.println("IO " + ex);
                break;
            }
        }
    }
}
