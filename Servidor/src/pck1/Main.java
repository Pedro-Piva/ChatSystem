package pck1;

import conexao.*;
import java.io.IOException;

public class Main extends Thread {

    public static void main(String[] args) throws IOException, InterruptedException {
        Servidor server = new Servidor();
        server.start();
        while (true) {
            System.out.println("Usuarios: ");
            int i = 1;
            for (Conexao c : server.getConexoes()) {
                if (c.getLogin() != null) {
                    System.out.println(i + "--------" + c.getLogin() + "---" + c.getSocket());
                }
                i++;
            }
            Thread.sleep(8000);
        }
    }
}