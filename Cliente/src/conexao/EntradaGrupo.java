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

    public EntradaGrupo(int port) throws UnknownHostException, SocketException, IOException {
        this.mcastaddr = InetAddress.getByName("228.5.6.7");
        this.group = new InetSocketAddress(mcastaddr, port);
        this.netIf = NetworkInterface.getByName("wlp0s20f3");
        this.multSocket = new MulticastSocket(port);
        multSocket.joinGroup(group, netIf);
        System.out.println("ENTRADAGRUPO CRIADA");
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buf = new byte[1000];
                System.out.println("Aguardando msg");
                DatagramPacket messageIn = new DatagramPacket(buf, buf.length);
                multSocket.receive(messageIn);
                String msg = new String(messageIn.getData(), 0, messageIn.getLength());
                System.out.println("Mensagem> " + msg);
            }
        } catch (IOException ex) {
            System.out.println("IOException " + ex.getMessage());
        }
    }
}
