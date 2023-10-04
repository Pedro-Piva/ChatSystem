package conexao;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Entrada extends Thread {

    private final DataInputStream fluxoEntrada;

    public Entrada(Socket socket) throws IOException {
        this.fluxoEntrada = new DataInputStream(socket.getInputStream());
    }

    //recebe a mensagem do Servidor e Exibe no Front
    @Override
    public void run() {
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                if (msg.equals("Informe o Login: ")) {
                    System.out.print(msg);
                } else if (msg.split(" ")[0].equals("address")) {
                    EntradaGrupo entradaGrupo = new EntradaGrupo(Integer.parseInt(msg.split(" ")[1]));
                    entradaGrupo.start();
                } else {
                    if (msg.equals("desconectado")) {
                        break;
                    }
                    System.out.println("Mensagem> " + msg);
                }

            } catch (IOException ex) {
                //System.out.println("Erro no Servidor: " + ex);
                System.out.println("DESCONECTADO");
                System.out.println("Pressione Enter para sair");
                break;
            }
        }
    }
}
