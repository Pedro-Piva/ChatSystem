package conexao;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public final class Conexao extends Thread {

    private Socket socket;
    private DataOutputStream saida;
    private String login;
    private ArrayList<Conexao> conexoes;
    private boolean online;
    private BatePapo conversa;

    public Conexao(Socket socket) throws IOException {
        conexoes = new ArrayList();
        this.socket = socket;
        this.online = false;
    }

    public String getLogin() {
        return login;
    }

    public Socket getSocket() {
        return socket;
    }

    public Conversa getEntrada() {
        return conversa.getEntrada();
    }

    public void atualiza(ArrayList<Conexao> conexoes) {
        this.conexoes = conexoes;
        if (conversa != null) {
            conversa.atualiza(conexoes);
        }
    }

    public void desconectar() {
        for (Conexao c : conexoes) {
            c.atualiza(conexoes);
        }
    }

    public boolean isOnline() {
        return online;
    }

    public boolean repetido(String nome) {
        for (Conexao c : conexoes) {
            if (c.getLogin() != null) {
                if (c.getLogin().equals(nome)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printUsuarios(DataOutputStream fluxoSaida) throws IOException {
        fluxoSaida.writeUTF("Usuarios do Servidor: ");
        for (Conexao c : conexoes) {
            if (c.getLogin() != null) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public void atualizaConexao(Socket socket) throws IOException {
        this.socket = socket;
        this.saida = new DataOutputStream(socket.getOutputStream());
        this.online = true;
        this.conversa = new BatePapo(saida, new DataInputStream(socket.getInputStream()), this.login, this.conexoes);
        this.conversa.start();
    }

    public boolean verificaConexao(String nome) {
        for (Conexao c : conexoes) {
            if (!c.isOnline()) {
                if (c.getSocket().getInetAddress().equals(socket.getInetAddress())
                        && c.getLogin() != null) {
                    if (c.getLogin().equals(nome)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        DataInputStream fluxoEntrada;
        DataOutputStream fluxoSaida;
        try {
            fluxoEntrada = new DataInputStream(socket.getInputStream());
            fluxoSaida = new DataOutputStream(socket.getOutputStream());
            boolean repetido = false;
            while (true) {
                fluxoSaida.writeUTF("Informe o Login: ");
                //Recebe da tela de login o login
                String lixo = fluxoEntrada.readUTF();
                System.out.println("Login Recebido: " + lixo);
                if (repetido(lixo) && !lixo.equals("")) {
                    System.out.println(lixo + " Logou");
                    //Mensagem de sucesso
                    fluxoSaida.writeUTF(lixo + " Logou");
                    this.login = lixo;
                    this.online = true;
                    break;
                } else if (verificaConexao(lixo)) {
                    this.login = lixo;
                    System.out.println(lixo + " Logou Novamente");
                    //Mensagem de sucesso de alguém relogando
                    fluxoSaida.writeUTF(lixo + " Logou Novamente");
                    apagar();
                    repetido = true;
                    break;
                } else {
                    System.out.println("Login Invalido ou ja existente, tente novamente: " + lixo);
                    //Mensagem de erro
                    fluxoSaida.writeUTF("Login Invalido ou ja existente, tente novamente");
                    printUsuarios(fluxoSaida);
                }
            }
            if (!repetido) {
                this.saida = fluxoSaida;
                this.conversa = new BatePapo(fluxoSaida, fluxoEntrada, this.login, this.conexoes);
                this.conversa.start();
            }
            if (conversa != null) {
                while (conversa.isAlive()) {
                }
            }
            online = false;
            conversa = null;
            System.out.println("User " + login + " disconnected");
            desconectar();
        } catch (IOException ex) {
            System.out.println("IO " + ex);
            this.online = false;
        }
    }

    public void apagar() throws IOException {
        for (Conexao c : conexoes) {
            if (c.equals(this)) {
                conexoes.remove(c);
                break;
            }
        }
        for (Conexao c : conexoes) {
            if (c.getSocket().getInetAddress().equals(this.socket.getInetAddress())
                    && c.getLogin().equals(this.getLogin())) {
                c.atualizaConexao(this.socket);
            }
        }
        desconectar();
    }
}
