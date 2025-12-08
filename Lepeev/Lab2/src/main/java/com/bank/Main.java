package com.bank;

import com.bank.gui.BankFrame;
import com.bank.service.BankService;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankFrame frame = new BankFrame(new BankService());
            frame.setVisible(true);
        });
    }
}