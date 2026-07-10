package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

    public class JDBC {
        String url = "jdbc:mysql://localhost:3306/catalogdigital";
        String user = "root";
        String password = "Bordeadaria2005";

        public static Connection conecteaza() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/catalogdigital";
            String user = "root";
            String password = "Bordeadaria2005";
            return DriverManager.getConnection(url, user, password);
        }
            private static int testInsert(Connection conn, String username, String parola, String rol) throws SQLException {
                System.out.println("Executing INSERT...");
                String sql = "INSERT INTO utilizator (nume_utilizator, parola_utilizator, rol) VALUES (?, ?, ?)";

                // Folosim RETURN_GENERATED_KEYS pentru a afla ce ID i-a dat AUTO_INCREMENT-ul din MySQL
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, parola);
                    pstmt.setString(3, rol);

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("INSERT completat. Rânduri afectate: " + rowsAffected);

                    // Extragem ID-ul generat automat
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idGenerat = generatedKeys.getInt(1);
                            System.out.println("Utilizatorul a primit ID-ul: " + idGenerat + "\n");
                            return idGenerat;
                        }
                    }
                }
                return -1;
            }

            private static void testSelect(Connection conn) throws SQLException {
                System.out.println("Executing SELECT...");
                String sql = "SELECT id_utilizator, nume_utilizator, rol FROM utilizator LIMIT 5"; // punem LIMIT 5 să nu umplem consola

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    while (rs.next()) {
                        int id = rs.getInt("id_utilizator");
                        String user = rs.getString("nume_utilizator");
                        String rol = rs.getString("rol");
                        System.out.printf(" -> ID: %d | Username: %s | Rol: %s %n", id, user, rol);
                    }
                    System.out.println();
                }
            }

            private static void testUpdate(Connection conn, int idUtilizator, String nouaParola) throws SQLException {
                System.out.println("Executing UPDATE pentru ID: " + idUtilizator);
                String sql = "UPDATE utilizator SET parola_utilizator = ? WHERE id_utilizator = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nouaParola);
                    pstmt.setInt(2, idUtilizator);

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("UPDATE completat. Rânduri modificate: " + rowsAffected + "\n");
                }
            }

            private static void testDelete(Connection conn, int idUtilizator) throws SQLException {
                System.out.println("Executing DELETE pentru ID: " + idUtilizator);
                String sql = "DELETE FROM utilizator WHERE id_utilizator = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, idUtilizator);

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("DELETE completat. Rânduri șterse: " + rowsAffected + "\n");
                }
            }
        }