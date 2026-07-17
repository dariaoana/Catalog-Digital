package org.example.functii;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import org.example.JDBC;
import org.example.interfata.AdminFrame;

public class NotificareModificare extends JFrame {

    private AdminFrame ferestraParinte;
    private JTable tabelCereri;
    private DefaultTableModel model;
    private List<Integer> idCereriRanduri = new ArrayList<Integer>();
    private List<Integer> idNoteRanduri = new ArrayList<Integer>();
    private List<Integer> valoriNoiRanduri = new ArrayList<Integer>();
    private List<String> tipuriCerereRanduri = new ArrayList<String>();

    private JButton btnAproba;
    private JButton btnRespinge;

    public NotificareModificare(AdminFrame parinte) {
        this.ferestraParinte = parinte;

        setTitle("Cereri Modificare Note");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
        incarcaCereri();
    }

    private void initComponente() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] coloane = {"Tip", "Elev", "Materie", "Notă veche", "Notă nouă", "Motiv", "Data cererii"};
        model = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelCereri = new JTable(model);
        tabelCereri.setRowHeight(25);
        tabelCereri.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelCereri);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel panelActiuni = new JPanel();
        panelActiuni.setLayout(new BoxLayout(panelActiuni, BoxLayout.X_AXIS));
        panelActiuni.setBorder(new TitledBorder("Acțiuni"));

        btnAproba = new JButton("Aprobă");
        btnRespinge = new JButton("Respinge");

        panelActiuni.add(Box.createHorizontalGlue());
        panelActiuni.add(btnAproba);
        panelActiuni.add(Box.createHorizontalStrut(15));
        panelActiuni.add(btnRespinge);

        panelPrincipal.add(panelActiuni, BorderLayout.SOUTH);
        add(panelPrincipal);

        btnAproba.addActionListener(e -> aprobaCerereSelectata());
        btnRespinge.addActionListener(e -> respingeCerereSelectata());
    }

    private void incarcaCereri() {
        model.setRowCount(0);
        idCereriRanduri.clear();
        idNoteRanduri.clear();
        valoriNoiRanduri.clear();
        tipuriCerereRanduri.clear();

        String sql = "SELECT c.id, c.id_nota, c.valoare_veche, c.valoare_noua, c.motiv, c.data_cerere, c.tip_cerere, " +
                "e.nume, e.prenume, m.nume as nume_materie " +
                "FROM cereri_modificare_note c " +
                "JOIN nota n ON c.id_nota = n.id_nota " +
                "JOIN elev e ON n.elev = e.id_elev " +
                "JOIN materie m ON n.materie = m.id_materie " +
                "WHERE c.status = 'IN_ASTEPTARE' " +
                "ORDER BY c.data_cerere DESC";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idCerere = rs.getInt("id");
                int idNota = rs.getInt("id_nota");
                int valoareVeche = rs.getInt("valoare_veche");
                int valoareNoua = rs.getInt("valoare_noua");
                boolean valoareNouaNula = rs.wasNull();
                String motiv = rs.getString("motiv");
                Timestamp data = rs.getTimestamp("data_cerere");
                String tipCerere = rs.getString("tip_cerere");
                String numeElev = rs.getString("nume") + " " + rs.getString("prenume");
                String numeMaterie = rs.getString("nume_materie");

                String valoareNouaText = valoareNouaNula ? "—" : String.valueOf(valoareNoua);
                String tipText = tipCerere.equals("STERGERE") ? "Ștergere" : "Modificare";

                model.addRow(new Object[]{tipText, numeElev, numeMaterie, valoareVeche, valoareNouaText, motiv, data});
                idCereriRanduri.add(idCerere);
                idNoteRanduri.add(idNota);
                valoriNoiRanduri.add(valoareNouaNula ? -1 : valoareNoua);
                tipuriCerereRanduri.add(tipCerere);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea cererilor: " + e.getMessage());
        }
    }
    private void aprobaCerereSelectata() {
        int randSelectat = tabelCereri.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this, "Selectează o cerere.");
            return;
        }

        int idCerere = idCereriRanduri.get(randSelectat);
        int idNota = idNoteRanduri.get(randSelectat);
        int valoareNoua = valoriNoiRanduri.get(randSelectat);
        String tipCerere = tipuriCerereRanduri.get(randSelectat);

        try (Connection conn = JDBC.conecteaza()) {
            conn.setAutoCommit(false);
            try {
                if (tipCerere.equals("STERGERE")) {
                    String sqlStergeNota = "DELETE FROM nota WHERE id_nota = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlStergeNota)) {
                        ps.setInt(1, idNota);
                        ps.executeUpdate();
                    }
                } else {
                    String sqlUpdateNota = "UPDATE nota SET valoare = ? WHERE id_nota = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdateNota)) {
                        ps.setInt(1, valoareNoua);
                        ps.setInt(2, idNota);
                        ps.executeUpdate();
                    }
                }

                String sqlUpdateCerere = "UPDATE cereri_modificare_note SET status = 'APROBAT', data_raspuns = NOW() WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateCerere)) {
                    ps.setInt(1, idCerere);
                    ps.executeUpdate();
                }

                conn.commit();
                String mesaj = tipCerere.equals("STERGERE") ? "Cererea a fost aprobată, nota a fost ștearsă." : "Cererea a fost aprobată, nota a fost actualizată.";
                JOptionPane.showMessageDialog(this, mesaj);
            } catch (SQLException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Eroare la aprobare: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la conectare: " + ex.getMessage());
        }

        incarcaCereri();
        ferestraParinte.actualizeazaBadgeCereriPublic();
    }

    private void respingeCerereSelectata() {
        int randSelectat = tabelCereri.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this, "Selectează o cerere.");
            return;
        }

        int idCerere = idCereriRanduri.get(randSelectat);

        String sql = "UPDATE cereri_modificare_note SET status = 'RESPINS', data_raspuns = NOW() WHERE id = ?";
        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCerere);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cererea a fost respinsă.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la respingere: " + ex.getMessage());
        }

        incarcaCereri();
        ferestraParinte.actualizeazaBadgeCereriPublic();
    }
}