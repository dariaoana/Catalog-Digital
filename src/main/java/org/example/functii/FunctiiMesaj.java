package org.example.functii;

import org.example.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FunctiiMesaj {

    // toți părinții copiilor pe care îi predă profesorul (după clasele lui din profesor_clasa)
    public static List<MesagerieParinte> listaParintiPentruProfesor(int idProfesor) {
        List<MesagerieParinte> lista = new ArrayList<MesagerieParinte>();

        String sql = "select distinct pa.id_parinte, pa.nume as nume_parinte, pa.prenume as prenume_parinte, " +
                "e.nume as nume_elev, e.prenume as prenume_elev " +
                "from profesor_clasa pc " +
                "join elev e on e.clasa = pc.clasa " +
                "join parinte pa on pa.parinte_pentru = e.id_elev " +
                "where pc.id_profesor = ? " +
                "order by nume_elev, prenume_elev";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfesor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idParinte = rs.getInt("id_parinte");
                    String numeParinte = rs.getString("prenume_parinte") + " " + rs.getString("nume_parinte");
                    String numeElev = rs.getString("prenume_elev") + " " + rs.getString("nume_elev");

                    lista.add(new MesagerieParinte(idParinte, numeParinte, numeElev));
                }
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }

        return lista;
    }

    // toți profesorii care predau la clasa copilului unui părinte
    public static List<MesagerieParinte> listaProfesoriPentruParinte(int idParinte) {
        List<MesagerieParinte> lista = new ArrayList<MesagerieParinte>();

        String sql = "select distinct p.id_profesor, p.nume as nume_prof, p.prenume as prenume_prof " +
                "from parinte pa " +
                "join elev e on pa.parinte_pentru = e.id_elev " +
                "join profesor_clasa pc on pc.clasa = e.clasa " +
                "join profesor p on p.id_profesor = pc.id_profesor " +
                "where pa.id_parinte = ? " +
                "order by nume_prof, prenume_prof";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idParinte);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idProfesor = rs.getInt("id_profesor");
                    String numeProfesor = rs.getString("prenume_prof") + " " + rs.getString("nume_prof");

                    lista.add(new MesagerieParinte(idProfesor, numeProfesor, null));
                }
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }

        return lista;
    }

    public static List<Mesaj> obtineConversatie(int idProfesor, int idParinte) {
        List<Mesaj> mesaje = new ArrayList<Mesaj>();

        String sql = "select id_mesaj, expeditor, continut, data_trimitere " +
                "from mesaj where id_profesor = ? and id_parinte = ? " +
                "order by data_trimitere asc";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfesor);
            ps.setInt(2, idParinte);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idMesaj = rs.getInt("id_mesaj");
                    String expeditor = rs.getString("expeditor");
                    String continut = rs.getString("continut");
                    Timestamp data = rs.getTimestamp("data_trimitere");

                    mesaje.add(new Mesaj(idMesaj, expeditor, continut, data));
                }
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }

        return mesaje;
    }

    public static boolean trimiteMesaj(int idProfesor, int idParinte, String expeditor, String continut) {
        String sql = "insert into mesaj (id_profesor, id_parinte, expeditor, continut) values (?, ?, ?, ?)";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfesor);
            ps.setInt(2, idParinte);
            ps.setString(3, expeditor);
            ps.setString(4, continut);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
            return false;
        }
    }
}