package com.gabriel.slcas.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.UserAccount;

// Login screen for the application.
public class LoginPanel extends JPanel {

    public LoginPanel(LibraryDatabase db, Consumer<UserAccount> onLogin) {
        setLayout(new GridBagLayout());
        setBackground(UITheme.NAVY_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));

        JLabel title = new JLabel("Gabriel's SLCAS");
        title.setFont(UITheme.titleFont());
        title.setForeground(UITheme.NAVY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Smart Library Circulation & Automation System");
        subtitle.setFont(UITheme.subtitleFont());
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel prompt = new JLabel("Select your account to sign in");
        prompt.setFont(UITheme.boldLabelFont());
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        prompt.setBorder(BorderFactory.createEmptyBorder(28, 0, 8, 0));

        JComboBox<UserAccount> userCombo = new JComboBox<>(db.getUsers().toArray(new UserAccount[0]));
        userCombo.setMaximumSize(new Dimension(320, 32));
        userCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        userCombo.setToolTipText("Members can browse, search, borrow and return items. Admins get full catalogue control.");

        JButton loginBtn = UITheme.primaryButton("Sign In", 'S');
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        loginBtn.addActionListener(e -> {
            UserAccount selected = (UserAccount) userCombo.getSelectedItem();
            if (selected != null) onLogin.accept(selected);
        });

        JLabel footer = new JLabel("designed & built by Gabriel Chukwuebuka Oboko");
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setForeground(UITheme.TEXT_MUTED);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setBorder(BorderFactory.createEmptyBorder(26, 0, 0, 0));

        card.add(title);
        card.add(subtitle);
        card.add(prompt);
        card.add(userCombo);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(footer);

        add(card);
    }
}
