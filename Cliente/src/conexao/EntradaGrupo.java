package conexao;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author pedro
 */
public class EntradaGrupo extends Thread {

    private final InetAddress mcastaddr;
    private final InetSocketAddress group;
    private final NetworkInterface netIf;
    private final MulticastSocket multSocket;
    private final String login;
    private final String nomeGrupo;

    public EntradaGrupo(int port, String login, String nomeGrupo) throws UnknownHostException, SocketException, IOException {
        this.login = login;
        this.nomeGrupo = nomeGrupo;
        this.mcastaddr = InetAddress.getByName("228.5.6.7");
        this.group = new InetSocketAddress(mcastaddr, port);
        this.netIf = NetworkInterface.getByName("wlp0s20f3");
        this.multSocket = new MulticastSocket(port);
        multSocket.joinGroup(group, netIf);
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buf = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buf, buf.length);
                multSocket.receive(messageIn);
                String msg = new String(messageIn.getData(), 0, messageIn.getLength());
                System.out.println("Mensagem> " + msg);
                if(msg.equals(nomeGrupo + "> " + login + " Saiu do Grupo")){
                    System.out.println("Entrada Grupo excluida");
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException " + ex.getMessage());
        }
    }
}
