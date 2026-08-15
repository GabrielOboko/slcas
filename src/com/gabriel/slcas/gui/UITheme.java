package com.gabriel.slcas.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

// Shared colors, fonts, and styles for the GUI.
public final class UITheme {

    private UITheme() { }

    // Palette -----------------------------------------------------------
    public static final Color NAVY_DARK   = new Color(0x0B1E3A);
    public static final Color NAVY        = new Color(0x11294D);
    public static final Color NAVY_LIGHT  = new Color(0x1B3A66);
    public static final Color ACCENT_RED  = new Color(0xC0362C);
    public static final Color ACCENT_GOLD = new Color(0xC9A24B);
    public static final Color OFF_WHITE   = new Color(0xF5F6F8);
    public static final Color TEXT_DARK   = new Color(0x1C232E);
    public static final Color TEXT_MUTED  = new Color(0x5B6472);
    public static final Color BOOK_TINT      = new Color(0xE3ECFB);
    public static final Color MAGAZINE_TINT  = new Color(0xFCEBD9);
    public static final Color JOURNAL_TINT   = new Color(0xE2F5E7);
    public static final Color BORROWED_TINT  = new Color(0xFDE2E1);
    public static final Color AVAILABLE_TINT = new Color(0xDFF3E5);

    // Fonts ---------------------------------------------------------------
    public static Font titleFont()    { return new Font("Georgia", Font.BOLD, 26); }
    public static Font subtitleFont() { return new Font("Segoe UI", Font.PLAIN, 13); }
    public static Font headingFont()  { return new Font("Segoe UI Semibold", Font.BOLD, 16); }
    public static Font labelFont()    { return new Font("Segoe UI", Font.PLAIN, 13); }
    public static Font boldLabelFont(){ return new Font("Segoe UI", Font.BOLD, 13); }
    public static Font buttonFont()   { return new Font("Segoe UI Semibold", Font.BOLD, 13); }
    public static Font tableFont()    { return new Font("Consolas", Font.PLAIN, 13); }
    public static Font monoFont()     { return new Font("Consolas", Font.PLAIN, 12); }

    public static JButton primaryButton(String text, char mnemonic) {
        JButton b = new JButton(text);
        b.setFont(buttonFont());
        b.setMnemonic(mnemonic);
        b.setBackground(NAVY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton accentButton(String text, char mnemonic) {
        JButton b = primaryButton(text, mnemonic);
        b.setBackground(ACCENT_RED);
        return b;
    }

    public static JButton secondaryButton(String text, char mnemonic) {
        JButton b = new JButton(text);
        b.setFont(buttonFont());
        b.setMnemonic(mnemonic);
        b.setBackground(OFF_WHITE);
        b.setForeground(NAVY_DARK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(NAVY_LIGHT, 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(headingFont());
        l.setForeground(NAVY_DARK);
        return l;
    }

    public static void stylePanel(JComponent c) {
        c.setBackground(OFF_WHITE);
        c.setBorder(new EmptyBorder(16, 18, 16, 18));
    }
}
