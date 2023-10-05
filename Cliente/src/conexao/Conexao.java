package conexao;

import java.net.*;
import java.io.*;

public final class Conexao {

    private final Socket socket;
    private final Saida saida;
    private final Entrada entrada;

    public Conexao(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new Entrada(socket);
        this.saida = new Saida(socket, entrada);
        tStart();
    }

    public void tStart() {
        saida.start();
        entrada.start();
    }

    public Socket getSocket() {
        return socket;
    }
}
