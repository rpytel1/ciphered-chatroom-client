package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Marcin Jamroz on 14.11.2016.
 * window which allows user to choose between registration and login in
 */
public class LogOrRegister extends JFrame {

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
     * buttons which allows user to choose registration or login process
     */
    JButton registerButton, logInButton;
    /**
     * option selected by user
     */
    String choice;

    /**
     * initialize standard properties of the window
     */
    public LogOrRegister() {
        setLocation(300, 300);
        setTitle("Log in or register");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * gets user choice: register or logIn
     *
     * @return user choice
     */
    public String getUserAction() {
        panel = new JPanel();

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (monitor) {
                    choice = "REGISTER";
                    monitorState = false;
                    monitor.notifyAll();
                    dispose();
                }
            }
        });

        logInButton = new JButton("Log in");
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (monitor) {
                    choice = "LOGIN";
                    monitorState = false;
                    monitor.notifyAll();
                    dispose();
                }
            }
        });

        panel.add(logInButton);
        panel.add(registerButton);
        add(panel);
        pack();
        panel.setVisible(true);
        setSize(300, 100);
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
        return choice;
    }

}
