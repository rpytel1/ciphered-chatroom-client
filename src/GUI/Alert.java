package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Marcin Jamroz on 16.11.2016.
 * helps showing alerts from the server
 */
public class Alert extends JFrame {

    /**
     * initializes alert window and show specific alert message
     *
     * @param alert alert message
     */
    public Alert(String alert) {
        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(2, 1));
        setTitle("Alert");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocation(300, 300);
        setAlwaysOnTop(true);

        JLabel label = new JLabel(alert);
        panel.add(label);

        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panel.add(button);
        add(panel);
        panel.setVisible(true);
        setVisible(true);


    }
}
