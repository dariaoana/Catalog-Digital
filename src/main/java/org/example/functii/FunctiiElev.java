package org.example.functii;

import org.example.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FunctiiElev {
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
        String sql = "SELECT m.nume AS materie_nume, p.nume AS prof_nume, p.prenume AS prof_prenume " +
                "FROM plan_invatamant pi " +
                "JOIN materie m ON pi.id_materie = m.id_materie " +
                "JOIN profesor p ON m.profesor_materie = p.id_profesor " +
                "WHERE pi.clasa_an = ?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clasa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String materie = rs.getString("materie_nume");
                    String profesor = "Prof. " + rs.getString("prof_prenume") + " " + rs.getString("prof_nume");

                    PlanInvatamant pi = new PlanInvatamant(clasa, materie, profesor);
                    plan.add(pi);
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la determinarea planului de invatamant: " + e.getMessage());
        }

        return plan;
    }
}
