
package conexao;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author pedro
 */
public class Entrada extends Thread {

    private final DataInputStream fluxoEntrada;
    private Saida saida;
    private final String login;
    
    public Entrada(DataInputStream fluxoEntrada, String login) throws IOException {
        this.fluxoEntrada = fluxoEntrada;
        this.login = login;
    }

    public void setSaida(Saida saida) throws IOException{
        this.saida = saida;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                System.out.println("Mensagem> " + msg);
                if(msg.equals("Desconectar")){
                    break;
                }
                saida.enviar(msg, login);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.println("Morri");
    }
}
