
package conexao;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author pedro
 */
public class Entrada extends Thread {

    private final DataInputStream fluxoEntrada;
    
    public Entrada(DataInputStream fluxoEntrada) throws IOException {
        this.fluxoEntrada = fluxoEntrada;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                System.out.println("Mensagem> " + msg);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.println("Morri");
    }
}
