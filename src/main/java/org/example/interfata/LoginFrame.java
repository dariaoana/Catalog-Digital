package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.example.functii.FunctiiLogin;

public class LoginFrame extends JFrame {

    private JTextField txtUtilizator;
    private JPasswordField txtParola;
    private JButton btnLogare;

    //Se creaza fereastra de login - cu bara de titlu, buton de minimizare etc.
    public LoginFrame() {
        setTitle("Catalog Digital - Logare");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
    }
    private void initComponente() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //indica ca urmatoarele componente adaugate in mainPanel
                                                                          // vor fi adaugate in concordanta cu axa y
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));


        JPanel rowUser = new JPanel(); //container pentru username
        JLabel lblUser = new JLabel("Utilizator: "); //label ce indica utilizatorului ca in acest container trebuie
                                                          //introduca username-ul
        txtUtilizator = new JTextField(15);
        rowUser.add(lblUser);
        rowUser.add(txtUtilizator);

        JPanel rowParola = new JPanel();
        JLabel lblParola = new JLabel("Parolă:     ");
        txtParola = new JPasswordField(15);
        rowParola.add(lblParola);
        rowParola.add(txtParola);

        JPanel rowButon = new JPanel();
        btnLogare = new JButton("Intră în catalog");
        rowButon.add(btnLogare);


        //se adauga componentele in fereastra
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(rowUser);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(rowParola);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(rowButon);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);

        btnLogare.addActionListener(e -> apasareButonLogare());
    }

    private void apasareButonLogare() {
        String username = txtUtilizator.getText().trim();
        String password = new String(txtParola.getText().trim());
        if(username.isEmpty()|| password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Numele de utilizator sau parola incomplete!","Eroare de logare!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(FunctiiLogin.verificaDateLogin(username,password)=="OK")
        {
            this.dispose();
        }
    }

}