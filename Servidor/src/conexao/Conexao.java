package conexao;

import java.net.*;
import java.io.*;

public class Conexao extends Thread {

    private final Servidor server;
    private Socket socket;
    private DataOutputStream saida;
    private String login;
    private boolean online;
    private BatePapo conversa;

    public Conexao(Socket socket, Servidor server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.online = false;
    }

    public String getLogin() {
        return login;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getSaida() {
        return saida;
    }

    public Conversa getEntrada() {
        return conversa.getEntrada();
    }

    public boolean isOnline() {
        return online;
    }

    public boolean repetido(String nome) {
        for (Conexao c : server.getConexoes()) {
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
        for (Conexao c : server.getConexoes()) {
            if (c.getLogin() != null) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public boolean verificaConexaoIgual(String nome) {
        for (Conexao c : server.getConexoes()) {
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
            if (!online) {
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
                        System.out.println("Grupo teste criado");
                        Grupo g = new Grupo(socket, server);
                        this.login = lixo;
                        this.online = true;
                        break;
                    } else if (verificaConexaoIgual(lixo)) {
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
                    this.conversa = new BatePapo(fluxoSaida, fluxoEntrada, this.login, server);
                    this.conversa.start();
                }
            }
            if (conversa != null) {
                while (conversa.isAlive()) {
                }
            }
            online = false;
            System.out.println("User " + login + " disconnected");
        } catch (IOException ex) {
            System.out.println("IO " + ex);
            this.online = false;
        }
    }

    public void apagar() throws IOException {
        for (Conexao c : server.getConexoes()) {
            if (c.equals(this)) {
                server.removeConexao(c);
                break;
            }
        }
        for (Conexao c : server.getConexoes()) {
            if (c.getSocket().getInetAddress().equals(this.socket.getInetAddress())
                    && c.getLogin().equals(this.getLogin())) {
                c.reconectar(this.socket);
                break;
            }
        }
    }

    public void reconectar(Socket socket) throws IOException {
        this.socket = socket;
        this.saida = new DataOutputStream(socket.getOutputStream());
        this.online = true;
        this.conversa = new BatePapo(saida, new DataInputStream(socket.getInputStream()), this.login, this.server);
        this.conversa.start();
        while (conversa.isAlive()) {
        }
        this.online = false;
    }
}
