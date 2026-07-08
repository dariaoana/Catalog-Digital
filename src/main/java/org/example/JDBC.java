package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class JDBC {
        public static Connection conecteaza() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/catalogdigital";
            String user = "root";
            String password = "Bordeadaria2005";
            return DriverManager.getConnection(url, user, password);
        }
    }