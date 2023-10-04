package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 *
 * @author pedro
 */
public final class Grupo extends Conexao {

    private ArrayList<Conexao> membros;
    private InetAddress mcastaddr;
    private InetSocketAddress group;
    private NetworkInterface netIf;
    private MulticastSocket multSocket;

    public Grupo(Socket socket, Servidor server, String login, int port) throws IOException {
        super(socket, server);
        this.mcastaddr = InetAddress.getByName("228.5.6.7");
        this.group = new InetSocketAddress(mcastaddr, port);
        this.netIf = NetworkInterface.getByName("wlp0s20f3");
        this.multSocket = new MulticastSocket(port);
        multSocket.joinGroup(group, netIf);
        this.membros = new ArrayList();
        DataOutputStream fluxoSaida = new DataOutputStream(getSocket().getOutputStream());
        for (Conexao c : getServer().getConexoes()) {
            if (c.getSocket().equals(getSocket()) && c.getLogin().equals(login)) {
                System.out.println("Adicionei");
                membros.add(c);
            }
        }
        DataInputStream fluxoEntrada = new DataInputStream(getSocket().getInputStream());
        while (true) {
            fluxoSaida.writeUTF("Informe um nome valido para o Grupo: ");
            String nome = fluxoEntrada.readUTF();
            if (nomeDiferente(nome)) {
                setLogin(nome);
                break;
            } else {
                fluxoSaida.writeUTF("Nome invalido ou ja existente: ");

            }
        }
        while (true) {
            printUsuariosGrupo(fluxoSaida);
            fluxoSaida.writeUTF("Selecione um membro para fazer parte do grupo");
            String membro = fluxoEntrada.readUTF();
            if (membro.equals("sair")) {
                fluxoSaida.writeUTF("Grupo Criado\n\tMembros:");
                for (Conexao c : membros) {
                    fluxoSaida.writeUTF(c.getLogin());
                    DataOutputStream criarEntradas = new DataOutputStream(c.getSocket().getOutputStream());
                    criarEntradas.writeUTF("address " + port);
                }        
                break;
            }
            Conexao c = verificaMembro(membro);
            if (c != null) {
                membros.add(c);
                fluxoSaida.writeUTF(c.getLogin() + " Adicionado com sucesso");
                fluxoSaida.writeUTF("Escreva outro membro ou escreva sair para sair");
            } else {
                fluxoSaida.writeUTF("Escreva um membro valido ou escreva sair para sair");
            }
        }
        setSocket(null);
    }

    public void printConexoes(DataOutputStream fluxoSaida) throws IOException {
        fluxoSaida.writeUTF("Nomes indisponiveis: ");
        for (Conexao c : getServer().getConexoes()) {
            if (c.getLogin() != null) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public Conexao verificaMembro(String membro) {
        for (Conexao c : getServer().getConexoes()) {
            if (!membros.contains(c) && c.getLogin().equals(membro)) {
                return c;
            }
        }
        return null;
    }

    public boolean nomeDiferente(String nome) {
        for (Conexao c : getServer().getConexoes()) {
            if (c.getLogin() != null) {
                if (c.getLogin().equals(nome)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean ehMembro(Conexao c) {
        return membros.contains(c);
    }

    public void printUsuariosGrupo(DataOutputStream fluxoSaida) throws IOException {
        fluxoSaida.writeUTF("Usuarios:");
        for (Conexao c : getServer().getConexoes()) {
            if (!membros.contains(c)) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public void enviarMSG(String msg) throws IOException {
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket mensagem = new DatagramPacket(msgBytes, msgBytes.length, group);
        multSocket.send(mensagem);
        System.out.println("Mensagem do grupo " + getLogin() + " Enviada" + " Mensagem: " + mensagem);
    }
}
