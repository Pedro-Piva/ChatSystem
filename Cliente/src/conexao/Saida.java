package conexao;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Saida extends Thread {

    private final DataOutputStream fluxoSaida;
    Scanner sc;

    public Saida(Socket socket) throws IOException {
        this.fluxoSaida = new DataOutputStream(socket.getOutputStream());
        sc = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            String msg = sc.nextLine();
            try {
                fluxoSaida.writeUTF(msg);
                System.out.println("Enviada");
            } catch (IOException ex) {
                System.out.println("IO: " + ex.getMessage());
                break;
            }
        }
    }
}
