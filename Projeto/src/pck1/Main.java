package pck1;

import conexao.*;
import java.io.IOException;

public class Main extends Thread {

    public static void main(String[] args) throws IOException, InterruptedException {
        Servidor server = new Servidor();
        server.start();
        int i = 0;
        while (true) {
            if (i == 10) {
                System.out.println("Usuarios ONLINE: ");
                for (Conexao c : server.getConexoes()) {
                    if (c.getLogin() != null) {
                        System.out.println(c.getLogin());
                    }
                }
                i = 0;
            }
            i++;
            server.atualizarConexoes();
            Thread.sleep(500);
        }
    }
}
