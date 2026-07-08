package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = JDBC.conecteaza();
            System.out.println("Conectat :)");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }
}
