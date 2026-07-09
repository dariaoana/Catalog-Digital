package org.example.functii;

import org.example.JDBC;

import javax.swing.*;
import java.sql.*;

public class FunctiiLogin {

    public static String verificaDateLogin(String username, String password) {

       /*String sqlUser = "select id_utilizator from utilizator where nume_utilizator=?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sqlUser)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Numele de utilizator nu exista!","Eroare",JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            return "Eroare SQL: " + e.getMessage();
            return "nimic";
        }*/

        String sqlLogin = "select id_utilizator from utilizator where nume_utilizator=? and parola_utilizator=?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sqlLogin)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "OK";
                } else {
                    JOptionPane.showMessageDialog(null,"Numele de utilizator sau parola sunt incorecte!","Eroare",JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            return "Eroare SQL: " + e.getMessage();
        }
        return "Alte erori!!!";
    }
}