package pck1;

import conexao.*;
import java.io.IOException;

public class Main extends Thread {

    public static void main(String[] args) throws IOException, InterruptedException {
        Servidor server = new Servidor();
        server.start();
        while (true) {
            System.out.println("Usuarios ONLINE: ");
            int i = 1;
            for (Conexao c : server.getConexoes()) {
                if (c.isOnline()) {
                    System.out.println(i + "--------" + c.getLogin() + "---" + c.getSocket());
                    i++;
                }
            }
            Thread.sleep(3000);
        }
    }
}

