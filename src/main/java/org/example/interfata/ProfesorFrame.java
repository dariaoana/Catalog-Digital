package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // IMPORT NOU
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.JDBC;

public class ProfesorFrame extends JFrame {

    private int idProfesorCurent;
    private JButton btnMesaje;
    private JButton btnStatistici;

    private JComboBox<String> comboMaterie;
    private JComboBox<String> comboClasa;
    private JTextField campCautare;
    private JTable tabelElevi;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter; // ELEMENT NOU pentru sortare
    private JButton btnAdaugaNota;

    // Panoul de jos pentru istoricul notelor
    private JPanel panelDetaliiNote;
    private JLabel lblNumeElevSelectat;
    private DefaultListModel<String> modelListaNote;
    private JList<String> listaNoteVizuale;

    // Folosim o clasă helper pentru a ține minte ID-ul elevului asociat rândului din model
    private List<Integer> idElevRanduri = new ArrayList<Integer>();

    public ProfesorFrame(int idProfesor) {
        this.idProfesorCurent = idProfesor;

        setTitle("Catalog Digital - Profesor");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
        incarcaMaterii();
    }

    private void initComponente() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // ---------- PANOU FILTRE (PE TOATĂ LĂȚIMEA) ----------
        JPanel panelFiltre = new JPanel();
        panelFiltre.setLayout(new BoxLayout(panelFiltre, BoxLayout.Y_AXIS));
        panelFiltre.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Filtre selecție clasă și elev"),
                new EmptyBorder(10, 15, 10, 15)
        ));

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
        panelPrincipal.add(Box.createVerticalStrut(5));

        // ---------- ZONĂ CENTRALĂ VERTICAL SPLIT ----------

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

        // INSTANȚIERE SORTER: Permite sortarea la click pe antetul tabelului
        sorter = new TableRowSorter<>(model);
        tabelElevi.setRowSorter(sorter);

        JScrollPane scrollTabel = new JScrollPane(tabelElevi);
        scrollTabel.setMinimumSize(new Dimension(400, 0));

        panelDetaliiNote = new JPanel(new BorderLayout());
        panelDetaliiNote.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Note"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        panelDetaliiNote.setPreferredSize(new Dimension(280, 0));
        panelDetaliiNote.setMinimumSize(new Dimension(220, 0));

        lblNumeElevSelectat = new JLabel("<html>Selectează un elev<br>pentru a-i vedea notele.</html>");
        lblNumeElevSelectat.setFont(new Font("Arial", Font.BOLD, 12));
        lblNumeElevSelectat.setBorder(new EmptyBorder(0, 0, 8, 0));
        panelDetaliiNote.add(lblNumeElevSelectat, BorderLayout.NORTH);

        modelListaNote = new DefaultListModel<>();
        listaNoteVizuale = new JList<>(modelListaNote);
        listaNoteVizuale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollListaNote = new JScrollPane(listaNoteVizuale);
        scrollListaNote.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        panelDetaliiNote.add(scrollListaNote, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabel, panelDetaliiNote);
        splitPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(500);
        panelPrincipal.add(splitPane);

        // ---------- FOOTER ----------
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBorder(new EmptyBorder(10, 20, 15, 20));

        btnAdaugaNota = new JButton("Adaugă notă");
        footer.add(btnAdaugaNota);

        btnStatistici = new JButton("Statistici elevi");
        footer.add(Box.createHorizontalStrut(10));
        footer.add(btnStatistici);

        footer.add(Box.createHorizontalGlue());

        panelPrincipal.add(new JSeparator());
        panelPrincipal.add(footer);

        add(panelPrincipal);

        // ---------- LISTENERE ----------

        tabelElevi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    incarcaDetaliiNoteSelectate();
                }
            }
        });

        comboMaterie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incarcaClase();
                incarcaElevi();
                reseteazaDetaliiNote();
            }
        });

        comboClasa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incarcaElevi();
                reseteazaDetaliiNote();
            }
        });

        campCautare.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                incarcaElevi();
                reseteazaDetaliiNote();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                incarcaElevi();
                reseteazaDetaliiNote();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                incarcaElevi();
                reseteazaDetaliiNote();
            }
        });

        btnAdaugaNota.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deschideDialogAdaugaNota();
            }
        });

        btnStatistici.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deschideStatistici();
            }
        });
    }

    private void deschideStatistici() {
        int idMaterie = getIdMaterieSelectata();
        if (idMaterie == -1) {
            JOptionPane.showMessageDialog(this, "Te rog selectează o materie validă.");
            return;
        }

        String clasaSelectata = (String) comboClasa.getSelectedItem();
        String numeMaterie = ((String) comboMaterie.getSelectedItem()).split(" - ")[1];

        StatisticiProfesorFrame statistici = new StatisticiProfesorFrame(clasaSelectata, idMaterie, numeMaterie);
        statistici.setVisible(true);
    }

    private void reseteazaDetaliiNote() {
        modelListaNote.clear();
    }

    private void incarcaDetaliiNoteSelectate() {
        modelListaNote.clear();
        int randSelectat = tabelElevi.getSelectedRow();

        if (randSelectat == -1) {
            reseteazaDetaliiNote();
            return;
        }

        // CORECȚIE IMPORTANTĂ: Convertim indexul rândului vizual selectat în indexul real din model,
        // deoarece sortarea modifică ordinea vizuală din tabel, dar nu și pe cea din array-ul nostru de ID-uri!
        int randModel = tabelElevi.convertRowIndexToModel(randSelectat);
        int idElev = idElevRanduri.get(randModel);

        String numeComplet = model.getValueAt(randModel, 0) + " " + model.getValueAt(randModel, 1);
        int idMaterie = getIdMaterieSelectata();

        lblNumeElevSelectat.setText("<html><b>Note pentru:</b> " + numeComplet + "</html>");

        String sql = "select valoare, data_notarii from nota where elev = ? and materie = ? order by data_notarii desc";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idElev);
            ps.setInt(2, idMaterie);

            try (ResultSet rs = ps.executeQuery()) {
                boolean areNote = false;
                while (rs.next()) {
                    areNote = true;
                    int valoare = rs.getInt("valoare");
                    Date data = rs.getDate("data_notarii");

                    String dataText = (data != null) ? data.toString() : "Fără dată";
                    modelListaNote.addElement(" Nota " + valoare + " , " + dataText);
                }

                if (!areNote) {
                    modelListaNote.addElement(" Elevul nu are note înregistrate la această materie.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare preluare note: " + e.getMessage());
        }
    }

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

    private void deschideDialogAdaugaNota() {
        int randSelectat = tabelElevi.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this, "Selectează mai întâi un elev din tabel.");
            return;
        }

        // CORECȚIE: Convertim și aici indexul vizual selectat în cel din model
        int randModel = tabelElevi.convertRowIndexToModel(randSelectat);
        int idElev = idElevRanduri.get(randModel);
        String numeElev = model.getValueAt(randModel, 0) + " " + model.getValueAt(randModel, 1);
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

            incarcaDetaliiNoteSelectate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la salvare: " + e.getMessage());
        }
    }
}