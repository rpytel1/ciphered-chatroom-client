package network;

import GUI.LoadWindow;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

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


    public Protocol(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public String register(String username, String password) throws IOException {
        Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF("REGISTER");
        dos.writeUTF(username);
        dos.writeUTF(password);
        String answer = dis.readUTF();
        dis.close();
        dos.close();
        socket.close();
        return answer;
    }

    public String login(String username, String password) throws IOException {
        Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF("LOGIN");
        dos.writeUTF(username);
        dos.writeUTF(password);
        String answer = dis.readUTF();
        dis.close();
        dos.close();
        socket.close();
        return answer;
    }

    public String downloadFile(String username, String ID, String filename) throws IOException {
        Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        LoadWindow loadWindow = new LoadWindow(filename);
        dos.writeUTF("GET FILE");
        dos.writeUTF(username);
        dos.writeUTF(ID);
        dos.writeUTF(filename);

        String path = System.getProperty("user.dir");
        String absolutePath = path + File.separator + username;
        FileReceiver fileReceiver = new FileReceiver(socket.getInputStream());
        fileReceiver.receive("UserFiles/" + filename);
        loadWindow.dispose();
        System.out.println("Received");
        dis.close();
        dos.close();
        socket.close();
        return "cap";
    }

    public void removeFile(String username, int ID) throws IOException {
        Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF("DELETE");
        dos.writeUTF(username);
        dos.writeInt(ID);
    }

    public Vector<Vector<String>> getServerFiles(String username) throws IOException {
        Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        Vector<Vector<String>> data = new Vector<>();


        dos.writeUTF("GET_MY_FILES");
        dos.writeUTF(username);

        boolean stopLoop = true;

        while (stopLoop) {
            Vector<String> row = new Vector<>();
            for (int i = 0; i < 5; i++) {
                String field = dis.readUTF();
                if (field.equals("EOF")) {
                    stopLoop = false;
                    break;
                } else {
                    row.add(field);
                }
            }
            if (stopLoop == true)
                data.add(row);
        }
        dis.close();
        dos.close();
        socket.close();

        return data;
    }

    public ArrayList<FileRecord> getFileRecords(String username) throws IOException {
        Socket socket = new Socket(serverIP, serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF("GET_MY_FILES");
        dos.writeUTF(username);

        ArrayList<FileRecord> list = new ArrayList<>();
        boolean stopLoop = true;

        while (stopLoop) {
            String field = dis.readUTF();
            if (field.equals("EOF")) break;
            String id = field;
            String filename = dis.readUTF();
            String filepath = dis.readUTF();
            long size = Long.parseLong(dis.readUTF());
            String checksum = dis.readUTF();

            FileRecord fR = new FileRecord(filepath, filename, size, checksum);
            list.add(fR);
        }
        dis.close();
        dos.close();
        socket.close();

        return list;
    }


}
