package network;

import GUI.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by Marcin Jamroz on 15.11.2016.
 * główna klasa programu, uruchamia metody umożliwiające działanie backupera
 */
public class ChatClient {


    public static final Object monitor = new Object();
    public static boolean monitorState = false;
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
    private int userNumber = 2;

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

        if (protocol.receiveMessages().equals("startKeyDistribution")) ;


        generateAndSendKeys();

        for (int i = 0; i < userNumber - 1; i++) {
            String messsage = protocol.receiveMessages();
            setPublicKeys(messsage);

        }
        user.setR();
        String message = user.getUserID() + "0" + user.getR();
        protocol.sendMessage(message);
        user.addNonce(message);
        for (int i = 0; i < userNumber - 1; i++) {
            String msg = protocol.receiveMessages();
            user.addNonce(msg);
        }

        for (Nonce nonce : user.nonceList) {
            System.out.println("nonce:" + nonce.toString());
        }
        String messageSig = user.computeSignature();
        protocol.sendMessage(messageSig);

        for (int i = 0; i < userNumber - 1; i++) {
            String msg = protocol.receiveMessages();
            user.recieveSignature(msg);
        }

        String messageX = user.sendX();
        protocol.sendMessage(messageX);
        for (int i = 0; i < userNumber - 1; i++) {
            String msg = protocol.receiveMessages();
            user.reciveX(msg);
        }
        user.computeSessionKey();


        ChatWindow chatWindow = new ChatWindow(protocol, username);


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


    }

    private void generateAndSendKeys() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            user = new User();
            user.setPrivateKey(keyPair.getPrivate());
            RSAPublicKey pK = (RSAPublicKey) keyPair.getPublic();
            String message = user.getUserID() + ":" + pK.getPublicExponent() + ":" + pK.getModulus();
            protocol.sendMessage(message);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPublicKeys(String message) {
        String[] splittedMessage = message.split(":");
        user.getPossibleUsers().add(splittedMessage[0]);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(splittedMessage[2]), new BigInteger(splittedMessage[1]));
        KeyFactory factory = null;
        PublicKey pub = null;
        try {
            factory = KeyFactory.getInstance("RSA");
            pub = factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }


        user.getPublicKeyMap().put(splittedMessage[0], pub);
    }
}

