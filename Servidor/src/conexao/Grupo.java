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
    private int port;

    public Grupo(Socket socket, Servidor server, String login, int port) throws IOException {
        super(socket, server);
        this.mcastaddr = InetAddress.getByName("228.5.6.7");
        this.group = new InetSocketAddress(mcastaddr, port);
        this.netIf = NetworkInterface.getByName("wlp0s20f3");
        this.multSocket = new MulticastSocket(port);
        multSocket.joinGroup(group, netIf);
        this.membros = new ArrayList();
        this.port = port;
        DataOutputStream fluxoSaida = new DataOutputStream(getSocket().getOutputStream());
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
        for (Conexao c : getServer().getConexoes()) {
            if (c.getSocket().equals(getSocket()) && c.getLogin().equals(login)) {
                DataOutputStream criarEntradas = new DataOutputStream(c.getSocket().getOutputStream());
                criarEntradas.writeUTF("address " + port + " " + c.getLogin() + " " + getLogin());
                membros.add(c);
                break;
            }
        }
        addMembros(fluxoSaida, fluxoEntrada);
        fluxoSaida.writeUTF("Grupo Criado\n\tMembros:");
        for (Conexao c : membros) {
            fluxoSaida.writeUTF(c.getLogin());
        }
    }

    public void removeMembro(Conexao conexao) {
        membros.remove(conexao);
        if (membros.isEmpty()) {
            getServer().removeConexao(this);
            System.out.println("Grupo removido");
        }
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

    public void addMembros(DataOutputStream fluxoSaida, DataInputStream fluxoEntrada) throws IOException {
        while (true) {
            printUsuariosGrupo(fluxoSaida);
            fluxoSaida.writeUTF("Selecione um membro para fazer parte do grupo");
            String membro = fluxoEntrada.readUTF();
            if (membro.equals("sair")) {
                fluxoSaida.writeUTF("Membros adicionados!");
                break;
            }
            Conexao c = verificaMembro(membro);
            if (c != null) {
                membros.add(c);
                fluxoSaida.writeUTF(c.getLogin() + " Adicionado com sucesso");
                fluxoSaida.writeUTF("Escreva outro membro ou escreva sair para sair");
                DataOutputStream criarEntradas = new DataOutputStream(c.getSocket().getOutputStream());
                criarEntradas.writeUTF("address " + port + " " + c.getLogin() + " " + getLogin());
            } else {
                fluxoSaida.writeUTF("Escreva um membro valido ou escreva sair para sair");
            }
        }
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
            if (!membros.contains(c) && c instanceof User) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }

    public void enviarMSG(String msg) throws IOException {
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket mensagem = new DatagramPacket(msgBytes, msgBytes.length, group);
        multSocket.send(mensagem);
    }
}
