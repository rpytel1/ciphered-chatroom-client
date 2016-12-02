package network;

import GUI.*;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Marcin Jamroz on 15.11.2016.
 * główna klasa programu, uruchamia metody umożliwiające działanie backupera
 */
public class BackuperClient {


    /**
     * username of the current user
     */
    String username;

    /**
     * IP serwera, z którym łączy się klient
     */
    private String serverIP;
    /**
     * port serwera, z którym łączy się klient
     */
    private int serverPort;

    /**
     * instancja protokołu komunikacyjnego
     */
    private Protocol protocol;

    public static void main(String[] args) {
        BackuperClient backuperClient = new BackuperClient();
        try {
            backuperClient.init();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void init() throws IOException {
        ConnectWindow connectWindow = new ConnectWindow();
        String[] serverConfiguration = connectWindow.getServerConfiguration();

        setServerIP(serverConfiguration[0]);
        setServerPort(Integer.parseInt(serverConfiguration[1]));


        String registrationLoginAnswer = "kappa";

        while (!registrationLoginAnswer.equals("LOG_IN")) {
            LogOrRegister logOrRegister = new LogOrRegister();
            String answerLogOrRegister = logOrRegister.getUserAction();

            protocol = new Protocol(serverIP, serverPort);

            if (answerLogOrRegister.equals("REGISTER")) {
                Registration registration = new Registration();
                String[] data = registration.getUserRegistrationInformation();
                Alert alert = new Alert(protocol.register(data[0], data[1]));                     // todo trzeba dorobic okienko informujace o istniejacym uzytkowniu o tym loginie

            } else {
                Login login = new Login();
                String[] data = login.getUserLoginInformation();
                registrationLoginAnswer = protocol.login(data[0], data[1]);//todo okienko informujace o bledzie logowania
                Alert alert = new Alert(registrationLoginAnswer);
                username = data[0];
            }
        }

        System.out.println("Koniec while");

        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("FileName");
        columnNames.add("FilePath");
        columnNames.add("Size");
        columnNames.add("Checksum");

        String directrory = System.getProperty("user.dir");
        ClientBrowser clientBrowser = new ClientBrowser(protocol.getServerFiles(username), columnNames, username, serverIP, serverPort, protocol, directrory);
        //    clientBrowser.updateFileRecords(serverIP,serverPort,username,protocol.getFileRecords(username));
        System.out.print("");
    }
}

