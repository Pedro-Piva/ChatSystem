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
    private Conversa entrada;
    private final DataInputStream fluxoEntrada;
    private final String login;
    private Conexao conexao;

    public BatePapo(DataOutputStream fluxoSaida, DataInputStream fluxoEntrada, String login, Servidor server, Conexao conexao) {
        this.fluxoSaida = fluxoSaida;
        this.fluxoEntrada = fluxoEntrada;
        this.login = login;
        this.server = server;
        this.conexao = conexao;
    }

    public Conversa getEntrada() {
        return entrada;
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
                    fluxoSaida.writeUTF("Falando com " + nome);
                    this.entrada = new Conversa(login, c.getLogin(), fluxoEntrada, new DataOutputStream(c.getSocket().getOutputStream()));
                    this.entrada.start();
                    while (entrada.isAlive()) {
                    }
                    System.out.println(login + " Saiu da Conversa");
                    fluxoSaida.writeUTF("Saiu da Conversa");
                } else if (nome.equals("grupo")) {
                    Grupo g = new Grupo(conexao.getSocket(), server, this.login);
                    
                } else if (nome.equals("desconectar")) {
                    fluxoSaida.writeUTF("desconectado");
                    break;
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
