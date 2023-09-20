package conexao;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Conexao extends Thread {

    private final Socket socket;
    private Saida saida;
    private Entrada entrada;
    private String login;
    private ArrayList<Conexao> conexoes;
    private String nome;
    private boolean online;

    public Conexao(Socket socket) throws IOException {
        this.socket = socket;
        this.online = false;
    }

    public String getLogin() {
        return login;
    }

    public void tStart() {
        saida.start();
        entrada.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void apagar() throws IOException {
        socket.close();
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public Saida getSaida() {
        return saida;
    }

    public void atualiza(ArrayList<Conexao> conexoes) {
        this.conexoes = conexoes;
    }
    
    public boolean isOnline(){
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
            if (c.getLogin() != null && !c.getLogin().equals(login)) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public Conexao nomeValido(String nome) {
        for (Conexao c : conexoes) {
            if (c.getLogin() != null) {
                if (c.getLogin().equals(nome)) {
                    return c;
                }
            }
        }
        return null;
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
                } else {
                    System.out.println("Login Invalido ou ja existente, tente novamente");
                    fluxoSaida.writeUTF("Login Invalido ou ja existente, tente novamente");
                    printUsuarios(fluxoSaida);
                }
            }
            this.saida = new Saida(fluxoSaida);
            while (true) {
                fluxoSaida.writeUTF("Escolha com quem Falar: ");
                printUsuarios(fluxoSaida);
                fluxoSaida.writeUTF("Pressione Enter para ver os Usuarios novamente.");
                this.nome = fluxoEntrada.readUTF();
                if (nomeValido(nome) != null) {
                    fluxoSaida.writeUTF("Falando com " + nome);
                    this.entrada = new Entrada(fluxoEntrada, login);
                    this.entrada.setSaida(nomeValido(nome).getSaida());
                    this.entrada.start();
                    while (entrada.isAlive()) {
                    }
                } else if (nome.equals("desconectar")) {
                    apagar();
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("IO " + ex);
            try {
                this.online = false;
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}
