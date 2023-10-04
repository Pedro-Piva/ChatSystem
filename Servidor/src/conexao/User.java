package conexao;

import java.net.*;
import java.io.*;

public class User extends Conexao implements Runnable {

    public User(Socket socket, Servidor server) throws IOException {
        super(socket, server);
        setTipo("User");
    }

    public boolean repetido(String nome) {
        for (Conexao c : getServer().getConexoes()) {
            if (c.getLogin() != null) {
                if (c.getLogin().equals(nome)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean verificaConexaoIgual(String nome) {
        for (Conexao c : getServer().getConexoes()) {
            if (!c.isOnline()) {
                if (c.getSocket().getInetAddress().equals(getSocket().getInetAddress())
                        && c.getLogin() != null) {
                    if (c.getLogin().equals(nome)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void grupo() {
        setOnline(true);
        setLogin("GRUPO");
    }

    @Override
    public void run() {
        DataInputStream fluxoEntrada;
        DataOutputStream fluxoSaida;
        try {
            if (!isOnline()) {
                fluxoEntrada = new DataInputStream(getSocket().getInputStream());
                fluxoSaida = new DataOutputStream(getSocket().getOutputStream());
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
                        setLogin(lixo);
                        setOnline(true);
                        break;
                    } else if (verificaConexaoIgual(lixo)) {
                        setLogin(lixo);
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
                        printUsuarios();
                    }
                }
                if (!repetido) {
                    setB(new BatePapo(fluxoSaida, fluxoEntrada, getLogin(), getServer(), this));
                    getB().start();
                }
            }
            while (getB().isAlive()) {
            }
            setOnline(false);
            System.out.println("User " + getLogin() + " disconnected");
        } catch (IOException ex) {
            System.out.println("IO " + ex);
            setOnline(false);
        }
    }

    public void apagar() throws IOException {
        for (Conexao c : getServer().getConexoes()) {
            if (c.equals(this)) {
                getServer().removeConexao(c);
                break;
            }
        }
        for (Conexao c : getServer().getConexoes()) {
            if (c.getSocket().getInetAddress().equals(getSocket().getInetAddress())
                    && c.getLogin().equals(this.getLogin())) {
                c.reconectar(getSocket());
                break;
            }
        }
    }
}
