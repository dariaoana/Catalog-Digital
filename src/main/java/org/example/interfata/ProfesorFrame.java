package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.JDBC;
import org.example.functii.MesagerieParinte;
import org.example.functii.FunctiiMesaj;

public class ProfesorFrame extends JFrame {

    private int idProfesorCurent;
    private JButton btnMesaje;

    private JComboBox<String> comboMaterie;
    private JComboBox<String> comboClasa;
    private JTextField campCautare;
    private JTable tabelElevi;
    private DefaultTableModel model;
    private JButton btnAdaugaNota;

    private List<Integer> idElevRanduri = new ArrayList<Integer>();

    public ProfesorFrame(int idProfesor) {
        this.idProfesorCurent = idProfesor;

        setTitle("Catalog Digital - Profesor");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
        incarcaMaterii();
    }

    private void initComponente() {

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // ---------- PANOU FILTRE ----------
        JPanel panelFiltre = new JPanel();
        panelFiltre.setLayout(new BoxLayout(panelFiltre, BoxLayout.Y_AXIS));
        panelFiltre.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel lblTitluFiltre = new JLabel("<html><b><font size='5'>Filtrare elevi</font></b></html>");
        panelFiltre.add(lblTitluFiltre);
        panelFiltre.add(Box.createVerticalStrut(10));

        JPanel randMaterie = new JPanel();
        randMaterie.setLayout(new BoxLayout(randMaterie, BoxLayout.X_AXIS));
        randMaterie.add(new JLabel("Materie: "));
        comboMaterie = new JComboBox<String>();
        randMaterie.add(comboMaterie);
        randMaterie.add(Box.createHorizontalGlue());
        panelFiltre.add(randMaterie);
        panelFiltre.add(Box.createVerticalStrut(8));

        JPanel randClasa = new JPanel();
        randClasa.setLayout(new BoxLayout(randClasa, BoxLayout.X_AXIS));
        randClasa.add(new JLabel("Clasa: "));
        comboClasa = new JComboBox<String>();
        comboClasa.addItem("Toate clasele");
        randClasa.add(comboClasa);
        randClasa.add(Box.createHorizontalGlue());
        panelFiltre.add(randClasa);
        panelFiltre.add(Box.createVerticalStrut(8));

        JPanel randCautare = new JPanel();
        randCautare.setLayout(new BoxLayout(randCautare, BoxLayout.X_AXIS));
        randCautare.add(new JLabel("Caută elev: "));
        campCautare = new JTextField(20);
        randCautare.add(campCautare);
        randCautare.add(Box.createHorizontalGlue());
        panelFiltre.add(randCautare);

        panelPrincipal.add(panelFiltre);
        panelPrincipal.add(new JSeparator());

        // ---------- TABEL ELEVI ----------
        String[] coloane = {"Nume", "Prenume", "Clasă"};
        model = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelElevi = new JTable(model);
        tabelElevi.setRowHeight(28);
        tabelElevi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelElevi);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        panelPrincipal.add(scrollPane);

        // ---------- FOOTER ----------
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBorder(new EmptyBorder(10, 20, 15, 20));

        btnAdaugaNota = new JButton("Adaugă notă elevului selectat");
        footer.add(btnAdaugaNota);

        btnMesaje = new JButton("Mesaje cu părinții");
        footer.add(Box.createHorizontalStrut(10));
        footer.add(btnMesaje);

        footer.add(Box.createHorizontalGlue());

        panelPrincipal.add(new JSeparator());
        panelPrincipal.add(footer);

        add(panelPrincipal);

        // ---------- LISTENERE (clase anonime, fără lambda) ----------

        comboMaterie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incarcaClase();
                incarcaElevi();
            }
        });

        comboClasa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incarcaElevi();
            }
        });

        campCautare.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                incarcaElevi();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                incarcaElevi();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                incarcaElevi();
            }
        });

        btnAdaugaNota.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deschideDialogAdaugaNota();
            }
        });

        btnMesaje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deschideListaParinti();
            }
        });
    }

    // ---------- ÎNCĂRCARE MATERII PREDATE DE PROFESOR ----------
    private void incarcaMaterii() {
        String sql = "select id_materie, nume from materie where profesor_materie = ?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfesorCurent);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idMaterie = rs.getInt("id_materie");
                    String nume = rs.getString("nume");
                    comboMaterie.addItem(idMaterie + " - " + nume);
                }
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }

        if (comboMaterie.getItemCount() > 0) {
            comboMaterie.setSelectedIndex(0);
        }
    }

    private int getIdMaterieSelectata() {
        String selectat = (String) comboMaterie.getSelectedItem();
        if (selectat == null) return -1;
        String idText = selectat.split(" - ")[0];
        return Integer.parseInt(idText);
    }

    // ---------- ÎNCĂRCARE CLASE CARE STUDIAZĂ MATERIA SELECTATĂ ----------
    private void incarcaClase() {
        comboClasa.removeAllItems();

        int idMaterie = getIdMaterieSelectata();
        if (idMaterie == -1) return;

        String sql = "select clasa from profesor_clasa where id_profesor = ?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfesorCurent);

            List<String> claseGasite = new ArrayList<String>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String clasa = rs.getString("clasa");
                    int anStudiu = extrageAnDinClasa(clasa);

                    if (materiaEsteInPlan(idMaterie, anStudiu) && !claseGasite.contains(clasa)) {
                        claseGasite.add(clasa);
                    }
                }
            }

            claseGasite.sort(null);
            for (String clasa : claseGasite) {
                comboClasa.addItem(clasa);
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }
    }

    private boolean materiaEsteInPlan(int idMaterie, int anStudiu) {
        String sql = "select 1 from plan_invatamant where id_materie=? and clasa_an=?";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMaterie);
            ps.setInt(2, anStudiu);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
            return false;
        }
    }

    private int extrageAnDinClasa(String clasa) {
        StringBuilder cifre = new StringBuilder();
        for (int i = 0; i < clasa.length(); i++) {
            char c = clasa.charAt(i);
            if (Character.isDigit(c)) {
                cifre.append(c);
            }
        }
        if (cifre.length() == 0) return -1;
        return Integer.parseInt(cifre.toString());
    }

    // ---------- ÎNCĂRCARE ELEVI (cu filtre aplicate) ----------
    private void incarcaElevi() {
        model.setRowCount(0);
        idElevRanduri.clear();

        int idMaterie = getIdMaterieSelectata();
        if (idMaterie == -1) return;

        String clasaSelectata = (String) comboClasa.getSelectedItem();
        String textCautare = campCautare.getText().trim();

        StringBuilder sql = new StringBuilder("select id_elev, nume, prenume, clasa from elev where 1=1 ");

        if (clasaSelectata != null) {
            sql.append("and clasa = ? ");
        }

        if (!textCautare.isEmpty()) {
            sql.append("and (nume like ? or prenume like ?) ");
        }

        sql.append("order by nume, prenume");

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (clasaSelectata != null) {
                ps.setString(index, clasaSelectata);
                index++;
            }

            if (!textCautare.isEmpty()) {
                ps.setString(index, "%" + textCautare + "%");
                index++;
                ps.setString(index, "%" + textCautare + "%");
                index++;
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idElev = rs.getInt("id_elev");
                    String nume = rs.getString("nume");
                    String prenume = rs.getString("prenume");
                    String clasa = rs.getString("clasa");

                    int anStudiu = extrageAnDinClasa(clasa);
                    if (!materiaEsteInPlan(idMaterie, anStudiu)) {
                        continue;
                    }

                    model.addRow(new Object[]{nume, prenume, clasa});
                    idElevRanduri.add(idElev);
                }
            }

        } catch (SQLException e) {
            System.err.println("Eroare SQL: " + e.getMessage());
        }
    }

    // ---------- ADĂUGARE NOTĂ ----------
    private void deschideDialogAdaugaNota() {
        int randSelectat = tabelElevi.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this, "Selectează mai întâi un elev din tabel.");
            return;
        }

        int idElev = idElevRanduri.get(randSelectat);
        String numeElev = model.getValueAt(randSelectat, 0) + " " + model.getValueAt(randSelectat, 1);
        int idMaterie = getIdMaterieSelectata();

        String valoareText = JOptionPane.showInputDialog(this, "Notă pentru " + numeElev + " (1-10):");

        if (valoareText == null || valoareText.trim().isEmpty()) {
            return;
        }

        int valoare;
        try {
            valoare = Integer.parseInt(valoareText.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Notă invalidă! Introdu un număr.");
            return;
        }

        if (valoare < 1 || valoare > 10) {
            JOptionPane.showMessageDialog(this, "Nota trebuie să fie între 1 și 10.");
            return;
        }

        salveazaNota(idElev, idMaterie, valoare);
    }

    private void salveazaNota(int idElev, int idMaterie, int valoare) {
        String sql = "insert into nota (valoare, materie, elev, data_notarii) values (?, ?, ?, curdate())";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, valoare);
            ps.setInt(2, idMaterie);
            ps.setInt(3, idElev);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Nota a fost adăugată cu succes!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la salvare: " + e.getMessage());
        }
    }

    // ---------- MESAGERIE ----------
    private void deschideListaParinti() {
        List<MesagerieParinte> parinti = FunctiiMesaj.listaParintiPentruProfesor(idProfesorCurent);

        if (parinti.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nu ai părinți disponibili pentru mesagerie.");
            return;
        }

        String[] optiuni = new String[parinti.size()];
        for (int i = 0; i < parinti.size(); i++) {
            optiuni[i] = parinti.get(i).getNumeParinte() + " (părinte al " + parinti.get(i).getNumeElev() + ")";
        }

        String ales = (String) JOptionPane.showInputDialog(this, "Alege un părinte:",
                "Mesaje", JOptionPane.PLAIN_MESSAGE, null, optiuni, optiuni[0]);

        if (ales == null) return;

        int indexAles = -1;
        for (int i = 0; i < optiuni.length; i++) {
            if (optiuni[i].equals(ales)) {
                indexAles = i;
                break;
            }
        }

        if (indexAles == -1) return;

        MesagerieParinte parinteAles = parinti.get(indexAles);

        ChatFrame chat = new ChatFrame(idProfesorCurent, parinteAles.getIdParinte(), "profesor", parinteAles.getNumeParinte());
        chat.setVisible(true);
    }
}