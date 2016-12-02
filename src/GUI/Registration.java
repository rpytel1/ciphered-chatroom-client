package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by Marcin Jamroz on 14.11.2016.
 * handles registration of the user
 */
public class Registration extends JFrame {

    /**
     * monitor used to wait until user write inputs
     */
    public static final Object monitor = new Object();
    /**
     * flag used together with monitor to ensure input data is present
     */
    public static boolean monitorState = false;
    /**
     * panel which contain components
     */
    JPanel panel;
    /**
     * text fields which contains login and password of the user
     */
    JTextField loginField, passwordField;
    /**
     * labels with instructions for user
     */
    JLabel loginLabel, passwordLabel, buttonLabel;
    /**
     * button which indicates user readiness
     */
    JButton registerButton;


    /**
     * initialize standard properties of the window
     */
    public Registration() {
        setTitle("Registration");
        setLocation(300, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public String[] getUserRegistrationInformation() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        String[] outInformation = new String[2];

        loginLabel = new JLabel("Login:");
        panel.add(loginLabel);

        loginField = new JTextField();
        loginField.setText("Login");
        loginField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                loginField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (loginField.getText().isEmpty()) {
                    loginField.setText("Login");
                }
            }
        });
        panel.add(loginField);

        passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JTextField();
        passwordField.setText("Password");
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getText().isEmpty()) {
                    passwordField.setText("Password");
                }
            }
        });
        panel.add(passwordField);

        buttonLabel = new JLabel("Click if you are done:");
        panel.add(buttonLabel);

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (monitor) {
                    outInformation[0] = loginField.getText();
                    outInformation[1] = passwordField.getText();
                    monitorState = false;
                    monitor.notifyAll();
                    dispose();
                }
            }
        });

        panel.add(registerButton);

        add(panel);
        pack();
        panel.setVisible(true);
        setVisible(true);

        monitorState = true;
        while (monitorState) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return outInformation;
    }
}


