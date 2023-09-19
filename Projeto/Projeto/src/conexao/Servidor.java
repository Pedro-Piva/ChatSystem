package conexao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {

    private final ArrayList<Conexao> conexoes = new ArrayList();
    private final ServerSocket servidor;

    public Servidor() throws IOException {
        this.servidor = new ServerSocket(1234);
    }

    public ArrayList<Conexao> getConexoes() {
        return conexoes;
    }

    public void removeConexao(Conexao c) {
        conexoes.remove(c);
    }

    public void atualizarConexoes() throws IOException {
        ArrayList<Conexao> remover = new ArrayList();
        for (Conexao c : conexoes) {
            if (c.getSocket().isClosed()) {
                remover.add(c);
            }
        }
        if (!remover.isEmpty()) {
            for (Conexao c : remover) {
                System.out.println("Conexao " + c.getLogin() + " Removida");
                c.apagar();
                removeConexao(c);
            }
        }
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
                conexao.start();
                conexoes.add(conexao);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
