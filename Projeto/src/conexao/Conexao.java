package conexao;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public final class Conexao extends Thread {

    private Socket socket;
    private Saida saida;
    private String login;
    private ArrayList<Conexao> conexoes;
    private boolean online;
    private boolean repetido;
    private Conversa conversa;

    public Conexao(Socket socket) throws IOException {
        repetido = false;
        this.socket = socket;
        this.online = false;
    }

    public String getLogin() {
        return login;
    }

    public Socket getSocket() {
        return socket;
    }

    public Entrada getEntrada() {
        return conversa.getEntrada();
    }

    public Saida getSaida() {
        return saida;
    }

    public void atualiza(ArrayList<Conexao> conexoes) {
        this.conexoes = conexoes;
        if (conversa != null) {
            conversa.atualiza(conexoes);
            if (!conversa.isAlive()){
                online = false;
            }
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
        fluxoSaida.writeUTF("Usuarios Online: ");
        for (Conexao c : conexoes) {
            if (c.isOnline() && !c.getLogin().equals(login)) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public boolean getRepetido() {
        return repetido;
    }

    public void atualizaConexao(Socket socket) throws IOException {
        this.socket = socket;
        this.saida = new Saida(new DataOutputStream(socket.getOutputStream()));
        this.online = true;
        this.conversa = new Conversa(this.saida, new DataOutputStream(socket.getOutputStream()), new DataInputStream(socket.getInputStream()), this.login, this.conexoes);
        this.conversa.start();
    }

    public boolean verificaConexao(String nome) {
        for (Conexao c : conexoes) {
            if (!c.isOnline()) {
                if (c.getSocket().getInetAddress().equals(socket.getInetAddress())) {
                    System.out.println(c.getLogin());
                    if (c.getLogin() != null) {
                        System.out.println(c.getLogin() + " == " + nome);
                        if (c.getLogin().equals(nome)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        DataInputStream fluxoEntrada = null;
        DataOutputStream fluxoSaida = null;
        try {
            fluxoEntrada = new DataInputStream(socket.getInputStream());
            fluxoSaida = new DataOutputStream(socket.getOutputStream());
            while (true) {
                fluxoSaida.writeUTF("Informe o Login: ");
                String lixo = fluxoEntrada.readUTF();
                System.out.println("Login Recebido");
                if (lixo != null && !lixo.equals("") && repetido(lixo)) {
                    System.out.println(lixo + " Logou");
                    fluxoSaida.writeUTF(lixo + " Logou");
                    this.login = lixo;
                    this.online = true;
                    break;
                } else if (verificaConexao(lixo)) {
                    this.repetido = true;
                    this.login = lixo;
                    System.out.println(lixo + " Logou Novamente");
                    fluxoSaida.writeUTF(lixo + " Logou Novamente");
                    Thread.sleep(600);
                    break;
                } else {
                    System.out.println("Login Invalido ou ja existente, tente novamente");
                    fluxoSaida.writeUTF("Login Invalido ou ja existente, tente novamente");
                    printUsuarios(fluxoSaida);
                }
            }
            if (!repetido) {
                this.saida = new Saida(fluxoSaida);
                this.conversa = new Conversa(this.saida, fluxoSaida, fluxoEntrada, this.login, this.conexoes);
                this.conversa.start();
            }
            if (conversa != null) {
                while (conversa.isAlive()) {
                }
            }
            online = false;
            conversa = null;
        } catch (IOException ex) {
            System.out.println("IO " + ex);
            this.online = false;
        } catch (InterruptedException ex) {
            System.out.println("Sleep " + ex);
        }
    }
}
