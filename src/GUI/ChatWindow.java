package GUI;

import network.Protocol;
import network.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


/**
 * Created by Marcin Jamroz on 02.12.2016.
 */
public class ChatWindow extends JFrame {

    private final static String newLine = "\n";
    JPanel textFieldPanel;
    JTextField textField;
    JTextArea textArea;
    JScrollPane textAreaScrollPane;
    Protocol protocol;
    String username;
    User user;

    public ChatWindow(Protocol protocol, String username, User user) {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat room");
        this.protocol = protocol;
        this.username = username;
        this.user = user;

        createTextArea();
        createTextField();


        pack();
        textArea.setVisible(true);
        textAreaScrollPane.setVisible(true);
        textFieldPanel.setVisible(true);
        textField.setVisible(true);
        setVisible(true);
    }

    public void createTextArea() {

        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textAreaScrollPane = new JScrollPane(textArea);
        add(textAreaScrollPane, BorderLayout.CENTER);
    }

    public void updateTextArea(String text) {
        textArea.append(text + newLine);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void createTextField() {
        textFieldPanel = new JPanel();
        textField = new JTextField(20);
        textFieldPanel.add(textField);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                try {
                    updateTextArea(username + ":" + text);
                    String encryptedMessage = user.encryptMessage(username + ":" + text);
                    protocol.sendMessage(encryptedMessage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                textField.setText("");
            }
        });

        add(textFieldPanel, BorderLayout.SOUTH);
    }


}
