/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author pedro
 */
public class ConversaGrupo extends Thread{

    private final Grupo grupo;
    private final DataInputStream fluxoEntrada;
    private final DataOutputStream fluxoSaida;
    private final String login;

    public ConversaGrupo(Grupo grupo, Socket socket, String login) throws IOException {
        this.grupo = grupo;
        this.fluxoEntrada = new DataInputStream(socket.getInputStream());
        this.fluxoSaida = new DataOutputStream(socket.getOutputStream());
        this.login = login;
    }
    
    @Override
    public void run(){
        while (true) {
            try {
                String msg = fluxoEntrada.readUTF();
                System.out.println(this.login + "-" + grupo.getLogin() + "> " + msg);
                if (msg.equals("Desconectar")) {
                    break;
                }
                msg = login + "> " + grupo.getLogin() + "> " + msg;
                grupo.enviarMSG(msg);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.println("Morri");
    }
}
