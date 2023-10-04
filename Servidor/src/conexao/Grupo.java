package conexao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author pedro
 */
public final class Grupo extends Conexao{

    private ArrayList<Conexao> membros;

    public Grupo(Socket socket, Servidor server, String login) throws IOException {
        super(socket, server);
        membros = new ArrayList();
        DataOutputStream fluxoSaida = new DataOutputStream(getSocket().getOutputStream());
        for (Conexao c : getServer().getConexoes()) {
            if (c.getSocket().equals(getSocket()) && c.getLogin().equals(login)) {
                System.out.println("Adicionei");
                membros.add(c);
            }
        }
        DataInputStream fluxoEntrada = new DataInputStream(getSocket().getInputStream());
        while (true) {
            printUsuariosGrupo(fluxoSaida);
            fluxoSaida.writeUTF("Selecione um membro para fazer parte do grupo");
            String membro = fluxoEntrada.readUTF();
            if(membro.equals("sair")){
                fluxoSaida.writeUTF("Grupo Criado\n\tMembros:");
                for(Conexao c: membros){
                    fluxoSaida.writeUTF(c.getLogin());
                }
                break;
            }
            Conexao c = verificaMembro(membro);
            if(c != null){
                membros.add(c);
                fluxoSaida.writeUTF(c.getLogin() + " Adicionado com sucesso");
                fluxoSaida.writeUTF("Escreva outro membro ou escreva sair para sair");
            } else {
                fluxoSaida.writeUTF("Escreva um membro valido ou escreva sair para sair");
            }
        }
        setSocket(null);
    }
    
    public Conexao verificaMembro(String membro){
        for (Conexao c : getServer().getConexoes()) {
            if (!membros.contains(c) && c.getLogin().equals(membro)) {
                return c;
            }
        }
        return null;
    }
    
    public boolean ehMembro(Conexao c){
        return membros.contains(c);
    }
    
    public void printUsuariosGrupo(DataOutputStream fluxoSaida) throws IOException{
        fluxoSaida.writeUTF("Usuarios:");
        for (Conexao c : getServer().getConexoes()) {
            if (!membros.contains(c)) {
                fluxoSaida.writeUTF(c.getLogin());
            }
        }
    }
}
