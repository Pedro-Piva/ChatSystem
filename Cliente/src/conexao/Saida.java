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

    //Recebe a Mensagem do Cliente, evnia pro servidor e exibe no front
    @Override
    public void run() {
        try {
            while (true) {
                String msg = sc.nextLine();
                fluxoSaida.writeUTF(msg);
                if (msg.equals("desconectar")) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("IOSaidaCliente: " + ex.getMessage());
        }
    }

}
