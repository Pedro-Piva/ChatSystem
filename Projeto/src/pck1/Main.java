package pck1;

import conexao.*;
import java.io.IOException;

public class Main extends Thread {

    public static void main(String[] args) throws IOException, InterruptedException {
        Servidor server = new Servidor();
        server.start();
        int i = 0;
        while (true) {
            if (i == 20) {
                System.out.println("Usuarios ONLINE: ");
                int j = 1;
                for (Conexao c : server.getConexoes()) {
                    if (c.isOnline()) {
                        System.out.println(j + "--------" + c.getLogin());
                    }
                    j++;
                }
                i = 0;
            }
            i++;
            server.atualizarConexoes();
            Thread.sleep(500);
        }
    }
}
