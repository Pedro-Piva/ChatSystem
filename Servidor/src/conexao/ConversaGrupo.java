/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author pedro
 */
public class ConversaGrupo extends Thread {

    private final Grupo grupo;
    private final DataInputStream fluxoEntrada;
    private final String login;
    private final Conexao conexao;
    private final DataOutputStream fluxoSaida;

    public ConversaGrupo(Grupo grupo, Conexao conexao, String login) throws IOException {
        this.grupo = grupo;
        this.fluxoEntrada = new DataInputStream(conexao.getSocket().getInputStream());
        this.conexao = conexao;
        this.login = login;
        this.fluxoSaida = new DataOutputStream(conexao.getSocket().getOutputStream());
    }

    @Override
    public void run() {
        OUTER:
        while (true) {
            try {
                fluxoSaida.writeUTF(this.login + " Mensagem:");
                String msg = fluxoEntrada.readUTF();
                System.out.println(this.login + "-" + grupo.getLogin() + "> " + msg);
                switch (msg) {
                    case "sair" -> {
                        break OUTER;
                    }
                    case "sair do grupo" -> {
                        msg = grupo.getLogin() + "> " + login + " Saiu do Grupo";
                        System.out.println(msg + "SAIR DO GRUPO");
                        grupo.enviarMSG(msg);
                        grupo.removeMembro(conexao);
                        break OUTER;
                    }
                    case "add" -> {
                        grupo.addMembros(fluxoSaida, fluxoEntrada);
                        continue;
                    }
                    default -> {
                        msg = login + "> " + grupo.getLogin() + "> " + msg;
                    }
                }
                grupo.enviarMSG(msg);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.println("Morri");
    }
}
