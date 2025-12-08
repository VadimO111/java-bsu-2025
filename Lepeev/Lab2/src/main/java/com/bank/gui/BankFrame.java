package com.bank.gui;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.BankService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BankFrame extends JFrame {
    private final BankService service;
    private UserTableModel userModel;
    private AccountTableModel accountModel;
    private TransactionTableModel txModel;

    public BankFrame(BankService service) {
        this.service = service;
        setTitle("Bank System Enterprise");
        setSize(1250, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        service.subscribe(this::onServiceUpdate);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        userModel = new UserTableModel();
        tabbedPane.addTab("👤 Клиенты", new JScrollPane(createSmartTable(userModel)));

        accountModel = new AccountTableModel();
        tabbedPane.addTab("💳 Счета", new JScrollPane(createSmartTable(accountModel)));

        txModel = new TransactionTableModel();
        tabbedPane.addTab("📊 История", new JScrollPane(createSmartTable(txModel)));

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setOpaque(false);

        btnPanel.add(createBtn("Добавить Клиента", new Color(70, 130, 180), e -> addUser()));
        btnPanel.add(createBtn("Открыть Счет", new Color(70, 130, 180), e -> addAccount()));
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(createBtn("Пополнить (+)", new Color(46, 139, 87), e -> doTx("DEPOSIT")));
        btnPanel.add(createBtn("Снять (-)", new Color(178, 34, 34), e -> doTx("WITHDRAW")));
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(createBtn("Заморозить ❄️", new Color(100, 149, 237), e -> changeAccountStatus("FREEZE")));
        btnPanel.add(createBtn("Разморозить 🔥", new Color(255, 140, 0), e -> changeAccountStatus("UNFREEZE")));
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(createBtn("📜 Отчет", new Color(105, 105, 105), e -> showReport()));

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JTable createSmartTable(AbstractTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(220, 230, 240));
        table.setFillsViewportHeight(true);

        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new CustomRenderer());

        JPopupMenu popup = new JPopupMenu();
        JMenuItem copy = new JMenuItem("Копировать значение");
        copy.addActionListener(e -> {
            int r = table.getSelectedRow();
            int c = table.getSelectedColumn();
            if (r >= 0 && c >= 0) {
                Object val = table.getValueAt(r, c);
                if (val != null) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(val.toString()), null);
                }
            }
        });
        popup.add(copy);
        table.setComponentPopupMenu(popup);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int r = table.rowAtPoint(e.getPoint());
                    int c = table.columnAtPoint(e.getPoint());
                    if (r >= 0 && c >= 0) {
                        table.changeSelection(r, c, false, false);
                    }
                }
            }
        });
        return table;
    }

    private JButton createBtn(String txt, Color bg, java.awt.event.ActionListener l) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(150, 45));
        b.addActionListener(l);
        return b;
    }

    static class CustomRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);

            boolean isRowSelected = table.isRowSelected(row);

            if (isSelected) {
                c.setBackground(new Color(30, 144, 255));
                c.setForeground(Color.WHITE);
            } else if (isRowSelected) {
                c.setBackground(new Color(225, 240, 255));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                c.setForeground(Color.BLACK);
            }

            if (!isSelected) {
                if (value instanceof String s) {
                    if (s.contains("Active")) c.setForeground(new Color(34, 139, 34));
                    else if (s.contains("Frozen")) c.setForeground(Color.RED);
                    else if (s.equals("DEPOSIT")) c.setForeground(new Color(34, 139, 34));
                    else if (s.equals("WITHDRAW")) c.setForeground(Color.RED);
                    else if (s.equals("FREEZE")) c.setForeground(Color.BLUE);
                    else if (s.equals("UNFREEZE")) c.setForeground(new Color(255, 140, 0));
                }
            }

            if (value instanceof String s) {
                if (s.contains("Active") || s.contains("Frozen")) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            return c;
        }
    }

    private void addUser() {
        String nick = JOptionPane.showInputDialog(this, "Никнейм:");
        if (nick != null && !nick.isBlank()) service.createUser(nick);
    }

    private void addAccount() {
        String uid = JOptionPane.showInputDialog(this, "UUID Пользователя (ПКМ по таблице -> Копировать):");
        if (uid != null && !uid.isBlank()) service.createAccount(uid);
    }

    private void doTx(String type) {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField idF = new JTextField();
        JTextField amF = new JTextField();
        p.add(new JLabel("UUID Счета:")); p.add(idF);
        p.add(new JLabel("Сумма:")); p.add(amF);

        if (JOptionPane.showConfirmDialog(this, p, type, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                service.processTransactionAsync(type, Double.parseDouble(amF.getText()), idF.getText().trim());
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Ошибка ввода"); }
        }
    }

    private void changeAccountStatus(String type) {
        String uid = JOptionPane.showInputDialog(this, "Введите UUID Счета для операции " + type + ":");
        if (uid != null && !uid.isBlank()) {
            service.processTransactionAsync(type, 0.0, uid.trim());
        }
    }

    private void showReport() {
        JTextArea area = new JTextArea(service.generateReport());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Финансовый Отчет", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onServiceUpdate(String msg) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, msg);
            refreshData();
        });
    }

    private void refreshData() {
        userModel.setData(service.getAllUsers());
        accountModel.setData(service.getAllAccounts());
        txModel.setData(service.getAllTransactions());
    }

    static class UserTableModel extends AbstractTableModel {
        private final String[] h = {"UUID", "Никнейм"};
        private List<User> d = new ArrayList<>();
        public void setData(List<User> l) { d = l; fireTableDataChanged(); }
        public int getRowCount() { return d.size(); }
        public int getColumnCount() { return h.length; }
        public String getColumnName(int c) { return h[c]; }
        public Object getValueAt(int r, int c) { return c==0 ? d.get(r).uuid() : d.get(r).nickname(); }
    }

    static class AccountTableModel extends AbstractTableModel {
        private final String[] h = {"ID Счета", "Владелец", "Баланс", "Статус"};
        private List<Account> d = new ArrayList<>();
        public void setData(List<Account> l) { d = l; fireTableDataChanged(); }
        public int getRowCount() { return d.size(); }
        public int getColumnCount() { return h.length; }
        public String getColumnName(int c) { return h[c]; }
        public Object getValueAt(int r, int c) {
            Account a = d.get(r);
            if (c==0) return a.getId();
            if (c==1) return a.getUserUuid();
            if (c==2) return String.format("%.2f", a.getBalance());
            return a.isFrozen() ? "❄️ Frozen" : "✅ Active";
        }
    }

    static class TransactionTableModel extends AbstractTableModel {
        private final String[] h = {"Время", "Тип", "Сумма", "Счет", "UUID Tx"};
        private List<Transaction> d = new ArrayList<>();
        public void setData(List<Transaction> l) { d = l; fireTableDataChanged(); }
        public int getRowCount() { return d.size(); }
        public int getColumnCount() { return h.length; }
        public String getColumnName(int c) { return h[c]; }
        public Object getValueAt(int r, int c) {
            Transaction t = d.get(r);
            if (c==0) return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(t.timestamp()));
            if (c==1) return t.type();
            if (c==2) return String.format("%.2f", t.amount());
            if (c==3) return t.accountId();
            return t.uuid();
        }
    }
}