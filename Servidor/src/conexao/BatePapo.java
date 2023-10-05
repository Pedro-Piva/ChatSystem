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
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
        fluxoSaida.writeUTF("Grupos disponiveis: ");
        for (Conexao c : server.getConexoes()) {
            if (c instanceof Grupo grupo) {
                if (grupo.ehMembro(conexao)) {
                    fluxoSaida.writeUTF(grupo.getLogin());
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
                fluxoSaida.writeUTF("Escolha com quem Falar: ");
                printUsuarios(fluxoSaida);
                fluxoSaida.writeUTF("Pressione Enter para ver os Usuarios novamente.");
                String nome = fluxoEntrada.readUTF();
                Conexao c = nomeValido(nome);
                if (c != null) {
                    if (c instanceof User) {
                        fluxoSaida.writeUTF("Falando com " + nome);
                        Conversa cvs = new Conversa(login, c.getLogin(), fluxoEntrada, new DataOutputStream(c.getSocket().getOutputStream()));
                        cvs.start();
                        while (cvs.isAlive()) {
                        }
                        System.out.println(login + " Saiu da Conversa");
                        fluxoSaida.writeUTF("Saiu da Conversa");
                    } else if (c instanceof Grupo grupo) {
                        fluxoSaida.writeUTF("Falando no Grupo " + nome);
                        ConversaGrupo cg = new ConversaGrupo(grupo, conexao, this.login);
                        fluxoSaida.writeUTF("Se quiser sair do grupo escreva: sair do grupo");
                        fluxoSaida.writeUTF("Se quiser sair da conversa escreva: sair");
                        fluxoSaida.writeUTF("Se quiser adicionar novos membros escreva: add");
                        cg.start();
                        while (cg.isAlive()) {
                        }
                        System.out.println(login + " Saiu da Conversa");
                        fluxoSaida.writeUTF("Saiu da Conversa");
                    }
                } else if (nome.equals("grupo")) {
                    Grupo g = new Grupo(conexao.getSocket(), server, this.login, server.getPort());
                    server.addConexao(g);
                } else if (nome.equals("desconectar")) {
                    if (estaEmGrupo()) {
                        fluxoSaida.writeUTF("Saia dos Grupos antes de desconectar");
                    } else {
                        fluxoSaida.writeUTF("desconectado");
                        break;
                    }
                } else if (nome.equals("")) {
                    System.out.println("Oi");
                } else {
                    fluxoSaida.writeUTF("Escolha uma Opcao valida ou escreva 'desconectar' para sair");
                }
            } catch (IOException ex) {
                System.out.println("IO " + ex);
                break;
            }
        }
    }
}
