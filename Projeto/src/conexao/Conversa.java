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

    public Conversa(String login, String destino, DataInputStream fluxoEntrada, DataOutputStream fluxoSaida) throws IOException {
        this.login = login;
        this.destino = destino;
        this.fluxoEntrada = fluxoEntrada;
        this.fluxoSaida = fluxoSaida;
    }

    public void enviar(String msg, String login) throws IOException {
        fluxoSaida.writeUTF(login + "> " + msg);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                System.out.println(this.login  + "-" + this.destino + "> " + msg);
                if (msg.equals("Desconectar")) {
                    break;
                }
                enviar(msg, login);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.println("Morri");
    }
}
