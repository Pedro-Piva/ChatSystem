package conexao;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Entrada extends Thread {

    private final DataInputStream fluxoEntrada;

    public Entrada(Socket socket) throws IOException {
        this.fluxoEntrada = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                if (msg.equals("Informe o Login: ")) {
                    System.out.print(msg);
                } else {
                    System.out.println("Mensagem> " + msg);
                }
            } catch (IOException ex) {
                System.out.println("Erro no Servidor: " + ex.getMessage());
                System.out.println("Pressione Enter para sair");
                break;
            }
        }
    }
}