package conexao;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Saida extends Thread {

    private final DataOutputStream fluxoSaida;
    private final Entrada entrada;
    Scanner sc;

    public Saida(Socket socket, Entrada entrada) throws IOException {
        this.fluxoSaida = new DataOutputStream(socket.getOutputStream());
        sc = new Scanner(System.in);
        this.entrada = entrada;
    }

    //Recebe a Mensagem do Cliente, evnia pro servidor e exibe no front
    @Override
    public void run() {
        try {
            while (true) {
                String msg = sc.nextLine();
                fluxoSaida.writeUTF(msg);
                if (msg.equals("desconectar")) {
                    if (!entrada.isAlive()) {
                        Thread.sleep(500);
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("IOSaidaCliente: " + ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(Saida.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
