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

    public Conexao(Socket socket) throws IOException {
        this.socket = socket;
    }

    public String getLogin() {
        return login;
    }

    public void tStart() {
        saida.start();
        entrada.start();
    }
    
    public void apagar() throws IOException {
        socket.close();
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void atualiza(ArrayList<Conexao> conexoes) {
        this.conexoes = conexoes;
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
            if (c.getLogin() != null) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
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
                    break;
                } else {
                    System.out.println("Login Invalido ou ja existente, tente novamente");
                    fluxoSaida.writeUTF("Login Invalido ou ja existente, tente novamente");
                    printUsuarios(fluxoSaida);
                }
            }
            this.saida = new Saida(fluxoSaida);
            this.entrada = new Entrada(fluxoEntrada);
            tStart();
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
