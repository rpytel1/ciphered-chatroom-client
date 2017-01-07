package network;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Marcin Jamroz on 15.11.2016.
 * odpowiada za komunikacje z serwerem
 */
public class Protocol {

    /**
     * server IP address
     */
    String serverIP;

    /**
     * server port
     */
    int serverPort;

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;


    public Protocol(String serverIP, int serverPort) throws IOException {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        socket = new Socket(serverIP, serverPort);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    public String register(String username, String password) throws IOException {
       /* Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
*/
        dos.writeUTF("REGISTER");
        dos.writeUTF(username);
        dos.writeUTF(password);
        String answer = dis.readUTF();
    /*    dis.close();
        dos.close();
        socket.close();*/
        return answer;
    }

    public String login(String username, String password) throws IOException {
     /*   Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
*/
        dos.writeUTF("LOGIN");
        dos.writeUTF(username);
        dos.writeUTF(password);
        String answer = dis.readUTF();
     /*   dis.close();
        dos.close();
        socket.close();*/
        return answer;
    }

    public String receiveMessages() throws IOException {
        //socket = new Socket(serverIP,serverPort);
        // dis = new DataInputStream(socket.getInputStream());
        String message = "";
        //   if (dis.available() > 0)
            message = dis.readUTF();
        // else message = null;

        System.out.println(message);
        return message;
    }

    public void sendMessage(String message) throws IOException {
        dos.writeUTF("MESSAGE");
        dos.writeUTF(message);
    }
}
