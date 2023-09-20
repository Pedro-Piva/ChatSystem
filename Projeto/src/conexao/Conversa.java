/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class Conversa extends Thread {

    private ArrayList<Conexao> conexoes;
    private Saida saida;
    private DataOutputStream fluxoSaida;
    private Entrada entrada;
    private DataInputStream fluxoEntrada;
    private String login;

    public Conversa(Saida saida, DataOutputStream fluxoSaida, DataInputStream fluxoEntrada, String login, ArrayList<Conexao> conexoes) {
        this.saida = saida;
        this.fluxoSaida = fluxoSaida;
        this.fluxoEntrada = fluxoEntrada;
        this.login = login;
        this.conexoes = conexoes;
    }

    public Entrada getEntrada() {
        return entrada;
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

    public void atualiza(ArrayList<Conexao> conexoes) {
        this.conexoes = conexoes;
    }

    public void printUsuarios(DataOutputStream fluxoSaida) throws IOException {
        fluxoSaida.writeUTF("Usuarios Online: ");
        for (Conexao c : conexoes) {
            if (c.isOnline() && !c.getLogin().equals(login)) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                fluxoSaida.writeUTF("Escolha com quem Falar: ");
                printUsuarios(fluxoSaida);
                fluxoSaida.writeUTF("Pressione Enter para ver os Usuarios novamente.");
                String nome = fluxoEntrada.readUTF();
                if (nomeValido(nome) != null) {
                    fluxoSaida.writeUTF("Falando com " + nome);
                    this.entrada = new Entrada(fluxoEntrada, login);
                    this.entrada.setSaida(nomeValido(nome).getSaida());
                    this.entrada.start();
                    while (entrada.isAlive()) {
                    }
                } else if (nome.equals("desconectar")) {
                    break;
                }
            } catch (IOException ex) {
                System.out.println("IO " + ex);
                break;
            }
        }
    }
}
