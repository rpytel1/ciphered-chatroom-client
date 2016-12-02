package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by Marcin Jamroz on 14.11.2016.
 * User can choose ip address and port of the server
 */
public class ConnectWindow extends JFrame {

    /**
     * monitor used to wait until user write inputs
     */
    public static final Object monitor = new Object();
    /**
     * flag used together with monitor to ensure input data is present
     */
    public static boolean monitorState = false;
    /**
     * panel containing text fields and button used to read configuration
     */
    JPanel panel;
    /**
     * text fields used to read user input configuration
     */
    JTextField ipTextField, portTextField;
    /**
     * button used to start of the data storing
     */
    JButton okButton;
    /**
     * labels which describes text fields
     */
    JLabel okButtonLabel, ipAddressLabel, portLabel;


    /**
     * initialize standard properties of the window
     */
    public ConnectWindow() {
        setLocation(300, 300);
        setTitle("Connect with server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * reads ip address and port number from created text fields
     *
     * @return string table with ip address and port number
     */
    public String[] getServerConfiguration() {

        String[] data = new String[2];

        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        ipAddressLabel = new JLabel("IP Address:");
        panel.add(ipAddressLabel);

        ipTextField = new JTextField();
        ipTextField.setText("IP address");
        ipTextField.setText("localhost");
        ipTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                ipTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (ipTextField.getText().isEmpty())
                    ipTextField.setText("IP address");
                ipTextField.setText("localhost");
            }
        });

        panel.add(ipTextField);

        portLabel = new JLabel("Port number:");
        panel.add(portLabel);

        portTextField = new JTextField();
        portTextField.setText("Port number");
        portTextField.setText("5555");
        portTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                portTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (portTextField.getText().isEmpty())
                    portTextField.setText("Port number");
                portTextField.setText("5555");
            }
        });

        panel.add(portTextField);


        okButtonLabel = new JLabel("Click button if above fields are filled:");
        panel.add(okButtonLabel);

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (monitor) {
                    data[0] = ipTextField.getText();
                    data[1] = portTextField.getText();
                    monitorState = false;
                    monitor.notifyAll();
                    dispose();
                }
            }
        });

        panel.add(okButton);
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
        return data;
    }
}