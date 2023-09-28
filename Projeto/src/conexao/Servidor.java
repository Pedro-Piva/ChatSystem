package conexao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor extends Thread {

    private final ArrayList<Conexao> conexoes = new ArrayList();
    private final ServerSocket servidor;

    public Servidor() throws IOException {
        this.servidor = new ServerSocket(1234);
    }

    public ArrayList<Conexao> getConexoes() {
        return conexoes;
    }

    public void atualizarConexoes() {
        for (Conexao c : conexoes) {
            c.atualiza(conexoes);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Conexao c : conexoes) {
                    if (c.getLogin() != null) {
                        System.out.println(c.getLogin());
                    }
                }
                System.out.println("Aguardando Conexao");
                Socket socket = servidor.accept();
                System.out.println("Cliente conectou " + socket);
                Conexao conexao = new Conexao(socket);
                conexoes.add(conexao);
                atualizarConexoes();
                conexao.start();
            } catch (IOException ex) {
                System.out.println("Erro no Servidor " + ex);
            }
        }
    }
}
