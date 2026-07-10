package org.example.functii;

import org.example.JDBC;
import java.sql.*;

public class FunctiiLogin {

    public RezultatLogin verificaDateLogin(String username, String password) {

        String sqlLogin = "select id_utilizator, rol from utilizator where nume_utilizator=? and parola_utilizator=?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sqlLogin)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idUtilizator = rs.getInt("id_utilizator");
                    String rol = rs.getString("rol");
                    return new RezultatLogin(true, idUtilizator, rol);
                } else {
                    return new RezultatLogin(false,  -1, null);
                }
            }

        } catch (SQLException e) {
            return new RezultatLogin(false,  -1, null);
        }
    }
}