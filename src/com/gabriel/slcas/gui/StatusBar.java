package com.gabriel.slcas.gui;

import java.awt.BorderLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class StatusBar extends JPanel {

    private final JLabel messageLabel = new JLabel("Ready.");
    private final JLabel clockLabel = new JLabel();

    public StatusBar() {
        setLayout(new BorderLayout());
        setBackground(UITheme.NAVY_DARK);
        setBorder(new EmptyBorder(6, 14, 6, 14));
        messageLabel.setForeground(UITheme.OFF_WHITE);
        messageLabel.setFont(UITheme.labelFont());
        clockLabel.setForeground(UITheme.ACCENT_GOLD);
        clockLabel.setFont(UITheme.labelFont());
        add(messageLabel, BorderLayout.WEST);
        add(clockLabel, BorderLayout.EAST);

        Timer clock = new Timer(1000, e -> clockLabel.setText(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        clock.setRepeats(true);
        clock.start();
    }

    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }
}
