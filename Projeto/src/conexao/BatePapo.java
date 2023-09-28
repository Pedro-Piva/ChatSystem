package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author pedro
 */
public class BatePapo extends Thread {

    private ArrayList<Conexao> conexoes;
    private final DataOutputStream fluxoSaida;
    private Conversa entrada;
    private final DataInputStream fluxoEntrada;
    private final String login;

    public BatePapo(DataOutputStream fluxoSaida, DataInputStream fluxoEntrada, String login, ArrayList<Conexao> conexoes) {
        this.fluxoSaida = fluxoSaida;
        this.fluxoEntrada = fluxoEntrada;
        this.login = login;
        this.conexoes = conexoes;
    }

    public Conversa getEntrada() {
        return entrada;
    }

    public Conexao nomeValido(String nome) {
        for (Conexao c : conexoes) {
            if (c.getLogin() != null) {
                if (c.getLogin().equals(nome)) {
                    return c;
                }
            }
        }
        return null;
    }

    public void atualiza(ArrayList<Conexao> conexoes) {
        this.conexoes = conexoes;
    }

    public void printUsuarios(DataOutputStream fluxoSaida) throws IOException {
        fluxoSaida.writeUTF("Usuarios Online: ");
        for (Conexao c : conexoes) {
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
                if (nomeValido(nome) != null) {
                    fluxoSaida.writeUTF("Falando com " + nome);
                    this.entrada = new Conversa(login, fluxoEntrada, fluxoSaida);
                    this.entrada.start();
                    while (entrada.isAlive()) {
                    }
                    System.out.println(login + " Saiu da Conversa");
                    fluxoSaida.writeUTF("Saiu da Conversa");
                } else if (nome.equals("desconectar")) {
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
