package conexao;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author pedro
 */
public class Grupo extends Conexao {

    public Grupo(Socket socket, Servidor server) throws IOException {
        super(socket, server);
    }
}
