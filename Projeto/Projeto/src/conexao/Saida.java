package conexao;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Saida extends Thread {

    private final DataOutputStream fluxoSaida;
    Scanner sc;

    public Saida(DataOutputStream fluxoSaida) throws IOException {
        this.fluxoSaida = fluxoSaida;
        sc = new Scanner(System.in);
    }

    public void enviar(String msg, String login) throws IOException {
        fluxoSaida.writeUTF(login + "> " + msg);
    }

    public DataOutputStream getFluxo() {
        return fluxoSaida;
    }

    @Override
    public void run() {
        /*
        while (true) {
            try {
                fluxoSaida.writeUTF(msg);
            } catch (IOException ex) {
                System.out.println("Saida: " + ex.getMessage());
                break;
            }
        }*/
    }
}
