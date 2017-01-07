package network;

import GUI.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Created by Marcin Jamroz on 15.11.2016.
 * główna klasa programu, uruchamia metody umożliwiające działanie backupera
 */
public class ChatClient {


    /**
     * username of the current user
     */
    String username;
    boolean outFlag = false;
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
    private User user;

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        try {
            chatClient.init();
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
        protocol = new Protocol(serverIP, serverPort);

        while (!registrationLoginAnswer.equals("LOG_IN")) {
            LogOrRegister logOrRegister = new LogOrRegister();
            String answerLogOrRegister = logOrRegister.getUserAction();


            if (answerLogOrRegister.equals("REGISTER")) {
                Registration registration = new Registration();
                String[] data = registration.getUserRegistrationInformation();
                Alert alert = new Alert(protocol.register(data[0], data[1]));

            } else {
                Login login = new Login();
                String[] data = login.getUserLoginInformation();
                registrationLoginAnswer = protocol.login(data[0], data[1]);
                Alert alert = new Alert(registrationLoginAnswer);
                username = data[0];
            }
        }

        if(protocol.receiveMessages().equals("startKeyDistribution"));
        ChatWindow chatWindow = new ChatWindow(protocol, username);
        generateAndSendKeys();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!outFlag) {
                    String message = "";

                    try {
                        message = protocol.receiveMessages();
                        chatWindow.updateTextArea(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        outFlag = true;
                        System.exit(0);

                    }

                }
            }

        }).start();
        generateAndSendKeys();


    }

    private void generateAndSendKeys() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
            user=new User();
            user.setPrivateKey(keyPair.getPrivate());
            RSAPublicKey pK=(RSAPublicKey)keyPair.getPublic();
            String message=user.getUserID()+":"+pK.getPublicExponent()+":"+ pK.getModulus();
            protocol.sendMessage(message);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

