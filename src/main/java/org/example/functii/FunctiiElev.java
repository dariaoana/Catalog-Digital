package org.example.functii;

import org.example.JDBC;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.example.functii.Nota;
public class FunctiiElev {
    // 1. Metodă nouă pentru a prelua numele complet al elevului
    public static String determinaNumeComplet(int idElev) {
        String sql = "SELECT nume, prenume FROM elev WHERE id_elev = ?";
        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idElev);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("prenume") + " " + rs.getString("nume");
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare determinare nume: " + e.getMessage());
        }
        return "Elev Anonim";
    }

    // 2. Metodă nouă pentru a aduce notele împreună cu data notării
    public static List<Nota> determinaNoteCuData(int idElev, int idMaterie) {
        List<Nota> note = new ArrayList<>();
        String sql = "SELECT valoare, data_notarii FROM nota WHERE elev = ? AND materie = ? ORDER BY data_notarii";
        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idElev);
            ps.setInt(2, idMaterie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int valoare = rs.getInt("valoare");
                    String data = rs.getString("data_notarii"); // SQL DATE se preia frumos ca String
                    note.add(new Nota(valoare, data));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare preluare note cu dată: " + e.getMessage());
        }
        return note;
    }
public static String determinaClasa(int id_elev){
    String sql="select clasa from elev where id_elev=?";

    try(Connection conn= JDBC.conecteaza();
    PreparedStatement ps = conn.prepareStatement(sql)){
        ps.setInt(1,id_elev);
        try (ResultSet rs=ps.executeQuery()){
            if(rs.next()){
                return rs.getString("clasa");
            }
        }
    }catch(Exception e){
        System.out.print("Eroare la baza de date"+e.getMessage());
    }
return null;
}
    public static List<PlanInvatamant> determinaMaterii(int clasa) {
        List<PlanInvatamant> plan = new ArrayList<>();
        String sql = "SELECT m.id_materie, m.nume AS materie_nume, p.nume AS prof_nume, p.prenume AS prof_prenume " +
                "FROM plan_invatamant pi " +
                "JOIN materie m ON pi.id_materie = m.id_materie " +
                "JOIN profesor p ON m.profesor_materie = p.id_profesor " +
                "WHERE pi.clasa_an = ?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clasa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idMaterie = rs.getInt("id_materie");
                    String materie = rs.getString("materie_nume");
                    String profesor = "Prof. " + rs.getString("prof_prenume") + " " + rs.getString("prof_nume");

                    PlanInvatamant pi = new PlanInvatamant(clasa, materie, profesor, idMaterie);
                    plan.add(pi);
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la determinarea planului de invatamant: " + e.getMessage());
        }

        return plan;
    }
    public static List<Integer> determinaNote(int idElev, int idMaterie) {
        List<Integer> note = new ArrayList<Integer>();
        String sql = "select valoare from nota where elev = ? and materie = ?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idElev);
            ps.setInt(2, idMaterie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    note.add(rs.getInt("valoare"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }

        return note;
    }
}
