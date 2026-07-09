package org.example.interfata;

// Importăm exclusiv din pachetul Swing
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private JTextField txtUtilizator;
    private JPasswordField txtParola;
    private JButton btnLogare;

    public LoginFrame() {
        setTitle("Catalog Digital - Logare");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
    }

    private void initComponente() {
        // Panoul principal care ține totul aliniat pe verticală
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Grupăm eticheta și căsuța de text într-un sub-panou Swing standard
        // JPanel lasă din fabrică o dimensiune mică și elegantă componentelor din el
        JPanel rowUser = new JPanel();
        JLabel lblUser = new JLabel("Utilizator: ");
        txtUtilizator = new JTextField(15); // "15" înseamnă lățime de 15 caractere (Swing pur!)
        rowUser.add(lblUser);
        rowUser.add(txtUtilizator);

        JPanel rowParola = new JPanel();
        JLabel lblParola = new JLabel("Parolă:     ");
        txtParola = new JPasswordField(15); // Oglindește lățimea căsuței de sus
        rowParola.add(lblParola);
        rowParola.add(txtParola);

        JPanel rowButon = new JPanel();
        btnLogare = new JButton("Intră în catalog");
        rowButon.add(btnLogare);

        // Adăugăm rândurile în panoul vertical cu spații elegante între ele
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(rowUser);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(rowParola);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(rowButon);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);

        btnLogare.addActionListener(e -> apasareButonLogare());
    }

    private void apasareButonLogare() {
        String username = txtUtilizator.getText();
    }
}