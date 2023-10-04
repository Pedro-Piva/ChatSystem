package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author pedro
 */
public class Conexao extends Thread{

    private final Servidor server;
    private Socket socket;
    private String login;
    private boolean online;
    private BatePapo b;
    private String tipo;

    public Conexao(Socket socket, Servidor server) {
        this.server = server;
        this.socket = socket;
        this.login = null;
        this.online = false;
    }

    public BatePapo getB() {
        return b;
    }

    public void setB(BatePapo b) {
        this.b = b;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Servidor getServer() {
        return server;
    }

    public String getLogin() {
        return login;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus(){
        if(online){
            return "Online";
        } 
        return "Offline";
    }
    public void reconectar(Socket socket) throws IOException {
        setSocket(socket);
        setOnline(true);
        setB(new BatePapo(new DataOutputStream(socket.getOutputStream()), new DataInputStream(socket.getInputStream()), getLogin(), getServer(), this));
        getB().start();
        while (getB().isAlive()) {
        }
        setOnline(false);
    }

    public void printUsuarios() throws IOException {
        DataOutputStream fluxoSaida = new DataOutputStream(socket.getOutputStream());
        fluxoSaida.writeUTF("Usuarios do Servidor: ");
        for (Conexao c : getServer().getConexoes()) {
            if (c.getLogin() != null) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }
}
