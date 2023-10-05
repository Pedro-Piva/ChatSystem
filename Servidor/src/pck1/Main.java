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
                if (c instanceof User) {
                    if (c.getLogin() != null) {
                        System.out.println(i + "--------" + c.getLogin() + "--------" + c.getStatus() + "--------" + c.getSocket());
                        i++;
                    }
                }
            }
            System.out.println("Grupos: ");
            i = 1;
            for (Conexao c : server.getConexoes()) {
                if (c instanceof Grupo) {
                    if (c.getLogin() != null) {
                        System.out.println(i + "--------" + c.getLogin());
                        i++;
                    }
                }
            }
            Thread.sleep(5000);
        }
    }
}
