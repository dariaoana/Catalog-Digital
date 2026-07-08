package org.example;

import org.example.interfata.LoginFrame;

import java.sql.Connection;
import java.sql.SQLException;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = JDBC.conecteaza();
            System.out.println("Conectat :)");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Eroare: " + e.getMessage());
        }

        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Nu s-a putut încărca tema vizuală.");
        }
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
    }
}
