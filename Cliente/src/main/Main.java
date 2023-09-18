package main;

import conexao.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        int porta = 1234;
        InetAddress ip = InetAddress.getByName("26.40.235.21");
        Socket socket = new Socket(ip, porta);
        Conexao conexao = new Conexao(socket);
    }
}
