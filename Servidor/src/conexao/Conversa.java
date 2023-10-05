package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author pedro
 */
public class Conversa extends Thread {

    private final String login;
    private final String destino;
    private final DataInputStream fluxoEntrada;
    private final DataOutputStream fluxoSaida;
    private final Conexao cDestino;

    public Conversa(String login, String destino, DataInputStream fluxoEntrada, DataOutputStream fluxoSaida, Conexao conexao) throws IOException {
        this.login = login;
        this.destino = destino;
        this.fluxoEntrada = fluxoEntrada;
        this.fluxoSaida = fluxoSaida;
        this.cDestino = conexao;
    }

    public void enviar(String msg, String login) throws IOException {
        fluxoSaida.writeUTF(login + "> " + msg);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                System.out.println(this.login + "-" + this.destino + "> " + msg);
                if (msg.equals("sair")) {
                    break;
                }
                try {
                    enviar(msg, login);
                } catch (IOException ex) {
                    msg = login + "> " + msg;
                    cDestino.armazenarMensagem(msg);
                    System.out.println("MENSAGEM NÃO ENVIADA, SERA ENVIADA NA PROXIMA VEZ EM QUE O USER FICAR ONLINE");
                    break;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.println("Morri");
    }
}
