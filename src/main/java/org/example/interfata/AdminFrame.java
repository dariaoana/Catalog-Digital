package org.example.interfata;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.example.JDBC;
import org.example.functii.NotificareModificare;
public class AdminFrame extends JFrame {

    private JTable tabelUtilizatori;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField campCautare;
    private JComboBox<String> comboFiltruRol;

    private JButton btnAdauga;
    private JButton btnModifica;
    private JButton btnSterge;
    private JButton btnCereriModificare;

    // ID-ul contului de admin curent logat, folosit pentru a preveni auto-ștergerea
    private int idUtilizatorCurent = -1;

    public AdminFrame() {
        setTitle("Catalog Digital - Administrare Conturi");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
        incarcaUtilizatori();
        actualizeazaBadgeCereri(); // NOU
    }

    // Constructor nou, opțional: dacă vrei să pasezi ID-ul adminului logat din LoginFrame,
    // ca să poți bloca auto-ștergerea. Dacă nu îl folosești, idUtilizatorCurent rămâne -1
    // și protecția e pur și simplu ignorată.
    public AdminFrame(int idUtilizatorCurent) {
        this();
        this.idUtilizatorCurent = idUtilizatorCurent;
    }

    private void initComponente() {

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));


        JPanel panelFiltre = new JPanel();
        panelFiltre.setLayout(new BoxLayout(panelFiltre, BoxLayout.X_AXIS));
        panelFiltre.setBorder(new TitledBorder("Filtre și Căutare"));

        panelFiltre.add(new JLabel(" Caută utilizator (Username/Rol): "));
        campCautare = new JTextField(20);
        panelFiltre.add(campCautare);

        panelFiltre.add(Box.createHorizontalStrut(20));

        panelFiltre.add(new JLabel("Filtrează după Rol: "));
        comboFiltruRol = new JComboBox<>(new String[]{"Toate rolurile", "admin", "profesor", "elev", "parinte"});
        panelFiltre.add(comboFiltruRol);

        panelPrincipal.add(panelFiltre);
        panelPrincipal.add(Box.createVerticalStrut(10));


        String[] coloane = {"ID Utilizator", "Username", "Parolă", "Rol"};
        model = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelUtilizatori = new JTable(model);
        tabelUtilizatori.setRowHeight(25);
        tabelUtilizatori.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(model);
        tabelUtilizatori.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabelUtilizatori);
        panelPrincipal.add(scrollPane);
        panelPrincipal.add(Box.createVerticalStrut(10));


        JPanel panelActiuni = new JPanel();
        panelActiuni.setLayout(new BoxLayout(panelActiuni, BoxLayout.X_AXIS));
        panelActiuni.setBorder(new TitledBorder("Administrare"));

        btnAdauga = new JButton("Adaugă Cont");
        btnModifica = new JButton("Modifică Cont");
        btnSterge = new JButton("Șterge Cont");


        panelActiuni.add(Box.createHorizontalGlue());
        panelActiuni.add(btnAdauga);
        panelActiuni.add(Box.createHorizontalStrut(15));
        panelActiuni.add(btnModifica);
        panelActiuni.add(Box.createHorizontalStrut(15));
        panelActiuni.add(btnSterge);
        panelActiuni.add(Box.createHorizontalStrut(15));
        panelActiuni.add(Box.createHorizontalStrut(15));
        btnCereriModificare = new JButton("Cereri Modificare Note (0)");
        panelActiuni.add(btnCereriModificare);

        panelPrincipal.add(panelActiuni);
        add(panelPrincipal);

        // ================= LISTENERE (Cu expresii Lambda, fără import de clase din AWT) =================

        campCautare.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicaFiltre(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicaFiltre(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicaFiltre(); }
        });

        // Folosim direct lambdas din Swing, fără să importăm ActionListener din AWT
        comboFiltruRol.addActionListener(e -> aplicaFiltre());
        btnAdauga.addActionListener(e -> deschideDialogAdauga());
        btnModifica.addActionListener(e -> deschideDialogModifica());
        btnSterge.addActionListener(e -> stergeUtilizatorSelectat());
        btnCereriModificare.addActionListener(e -> deschideCereriModificare());
    }


    private String citesteCelulaText(Row row, int index) {
        Cell celula = row.getCell(index);
        if (celula == null) return null;

        if (celula.getCellType() == CellType.STRING) {
            return celula.getStringCellValue();
        } else if (celula.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) celula.getNumericCellValue());
        }
        return null;
    }
    private void incarcaUtilizatori() {
        model.setRowCount(0);
        String sql = "SELECT id_utilizator, nume_utilizator, parola_utilizator, rol FROM utilizator ORDER BY id_utilizator";

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_utilizator");
                String user = rs.getString("nume_utilizator");
                String pass = rs.getString("parola_utilizator");
                String rol = rs.getString("rol");

                model.addRow(new Object[]{id, user, pass, rol});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
        }
    }
    private void actualizeazaBadgeCereri() {
        String sql = "SELECT COUNT(*) FROM cereri_modificare_note WHERE status = 'IN_ASTEPTARE'";
        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int numar = 0;
            if (rs.next()) {
                numar = rs.getInt(1);
            }
            btnCereriModificare.setText("Cereri Modificare Note (" + numar + ")");
        } catch (SQLException e) {
            System.err.println("Eroare la numărarea cererilor: " + e.getMessage());
        }
    }

    private void deschideCereriModificare() {
        NotificareModificare fereastra = new NotificareModificare(this);
        fereastra.setVisible(true);
    }
    private void aplicaFiltre() {
        String text = campCautare.getText().trim();
        String rolSelectat = (String) comboFiltruRol.getSelectedItem();

        List<RowFilter<Object, Object>> filtre = new ArrayList<>();

        if (!text.isEmpty()) {
            filtre.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1));
        }

        if (rolSelectat != null && !rolSelectat.equals("Toate rolurile")) {
            filtre.add(RowFilter.regexFilter("^" + Pattern.quote(rolSelectat) + "$", 3));
        }

        if (filtre.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtre));
        }
    }

    private void deschideDialogAdauga() {
        JTextField idField = new JTextField();
        JTextField userField = new JTextField();
        JTextField passField = new JTextField();
        JComboBox<String> rolCombo = new JComboBox<>(new String[]{"admin", "profesor", "elev", "parinte"});

        Object[] formular = {
                "ID Utilizator:", idField,
                "Username:", userField,
                "Parolă:", passField,
                "Rol:", rolCombo
        };

        int optiune = JOptionPane.showConfirmDialog(this, formular, "Adaugă Utilizator Nou", JOptionPane.OK_CANCEL_OPTION);

        if (optiune == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String user = userField.getText().trim();
                String pass = passField.getText().trim();
                String rol = (String) rolCombo.getSelectedItem();

                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Toate câmpurile trebuie completate!");
                    return;
                }

                String sql = "INSERT INTO utilizator (id_utilizator, nume_utilizator, parola_utilizator, rol) VALUES (?, ?, ?, ?)";
                try (Connection conn = JDBC.conecteaza();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setString(2, user);
                    ps.setString(3, pass);
                    ps.setString(4, rol);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Utilizator salvat!");
                    incarcaUtilizatori();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID-ul trebuie să fie un număr!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
            }
        }
    }

    private void deschideDialogModifica() {
        int randSelectat = tabelUtilizatori.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this, "Selectează un utilizator.");
            return;
        }

        int randModel = tabelUtilizatori.convertRowIndexToModel(randSelectat);
        int id = (int) model.getValueAt(randModel, 0);
        String userCurent = (String) model.getValueAt(randModel, 1);
        String passCurent = (String) model.getValueAt(randModel, 2);
        String rolCurent = (String) model.getValueAt(randModel, 3);

        JTextField userField = new JTextField(userCurent);
        JTextField passField = new JTextField(passCurent);
        JComboBox<String> rolCombo = new JComboBox<>(new String[]{"admin", "profesor", "elev", "parinte"});
        rolCombo.setSelectedItem(rolCurent);

        Object[] formular = {
                "ID: " + id,
                "Username:", userField,
                "Parolă:", passField,
                "Rol:", rolCombo
        };

        int optiune = JOptionPane.showConfirmDialog(this, formular, "Modifică Utilizator", JOptionPane.OK_CANCEL_OPTION);

        if (optiune == JOptionPane.OK_OPTION) {
            String userNou = userField.getText().trim();
            String passNou = passField.getText().trim();
            String rolNou = (String) rolCombo.getSelectedItem();

            if (userNou.isEmpty() || passNou.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Câmpurile nu pot fi goale!");
                return;
            }

            String sql = "UPDATE utilizator SET nume_utilizator = ?, parola_utilizator = ?, rol = ? WHERE id_utilizator = ?";
            try (Connection conn = JDBC.conecteaza();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userNou);
                ps.setString(2, passNou);
                ps.setString(3, rolNou);
                ps.setInt(4, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Utilizator modificat!");
                incarcaUtilizatori();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
            }
        }
    }

    private void stergeUtilizatorSelectat() {
        int randSelectat = tabelUtilizatori.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this, "Selectează un utilizator.");
            return;
        }

        int randModel = tabelUtilizatori.convertRowIndexToModel(randSelectat);
        int idUtilizator = (int) model.getValueAt(randModel, 0);
        String username = (String) model.getValueAt(randModel, 1);
        String rol = (String) model.getValueAt(randModel, 3);

        // Protecție: nu permitem ștergerea contului cu care ești logat curent
        if (idUtilizatorCurent != -1 && idUtilizator == idUtilizatorCurent) {
            JOptionPane.showMessageDialog(this, "Nu poți șterge contul cu care ești logat curent!",
                    "Acțiune blocată", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(
                this,
                "Ești sigur că vrei să ștergi contul '" + username + "'? Toate datele asociate (note, mesaje, asocieri) vor fi șterse definitiv!",
                "Confirmă Ștergerea",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        // Folosim o tranzacție pentru a ne asigura că totul se șterge corect în lanț
        try (Connection conn = JDBC.conecteaza()) {
            conn.setAutoCommit(false); // Dezactivăm autocommit ca să putem da rollback în caz de eroare

            try {
                if (rol.equalsIgnoreCase("elev")) {
                    // 1. Identificăm ID-ul elevului
                    int idElev = -1;
                    String sqlGetIdElev = "SELECT id_elev FROM elev WHERE id_utilizator_e = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlGetIdElev)) {
                        ps.setInt(1, idUtilizator);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                idElev = rs.getInt("id_elev");
                            }
                        }
                    }

                    if (idElev != -1) {
                        // 2. Ștergem cererile de modificare/ștergere legate de notele acestui elev
                        //    (altfel FK-ul din cereri_modificare_note blochează ștergerea notelor)
                        String sqlDeleteCereri = "DELETE FROM cereri_modificare_note WHERE id_nota IN (SELECT id_nota FROM nota WHERE elev = ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteCereri)) {
                            ps.setInt(1, idElev);
                            ps.executeUpdate();
                        }

                        // 3. Ștergem notele elevului
                        String sqlDeleteNote = "DELETE FROM nota WHERE elev = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteNote)) {
                            ps.setInt(1, idElev);
                            ps.executeUpdate();
                        }

                        // 4. Ștergem mesajele părintelui acestui elev (pentru a evita eroarea de Foreign Key din tabelul 'mesaj')
                        String sqlDeleteMesajeParinte = "DELETE FROM mesaj WHERE id_parinte = (SELECT id_parinte FROM parinte WHERE parinte_pentru = ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteMesajeParinte)) {
                            ps.setInt(1, idElev);
                            ps.executeUpdate();
                        }

                        // 5. Ștergem contul de utilizator al părintelui asociat (dacă există), înainte de a șterge părintele
                        String sqlDeleteUtilizatorParinte = "DELETE FROM utilizator WHERE id_utilizator = (SELECT id_utilizator_p FROM parinte WHERE parinte_pentru = ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteUtilizatorParinte)) {
                            ps.setInt(1, idElev);
                            ps.executeUpdate();
                        }

                        // 6. Ștergem părintele asociat
                        String sqlDeleteParinte = "DELETE FROM parinte WHERE parinte_pentru = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteParinte)) {
                            ps.setInt(1, idElev);
                            ps.executeUpdate();
                        }}
                    // 7. Ștergem elevul
                    String sqlDeleteElev = "DELETE FROM elev WHERE id_utilizator_e = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDeleteElev)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }
                } else if (rol.equalsIgnoreCase("profesor")) {

                    // Pasul 1: Eliberăm materiile (le punem pe NULL ca să nu mai depindă de acest profesor)
                    String sqlUpdateMaterii = "UPDATE materie SET profesor_materie = NULL WHERE profesor_materie = (SELECT id_profesor FROM profesor WHERE id_utilizator_prof = ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdateMaterii)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }

                    // Pasul 2: Ștergem cererile de modificare/ștergere trimise de acest profesor
                    String sqlDeleteCereriProf = "DELETE FROM cereri_modificare_note WHERE id_profesor = (SELECT id_profesor FROM profesor WHERE id_utilizator_prof = ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDeleteCereriProf)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }

                    // Pasul 3: Ștergem asocierile cu clasele
                    String sqlClase = "DELETE FROM profesor_clasa WHERE id_profesor = (SELECT id_profesor FROM profesor WHERE id_utilizator_prof = ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlClase)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }

                    // Pasul 4: ȘTERGEM PROFESORUL PROPRIU-ZIS
                    String sqlProfesor = "DELETE FROM profesor WHERE id_utilizator_prof = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlProfesor)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }
                }
                else if (rol.equalsIgnoreCase("parinte")) {
                    // 1. Ștergem mesajele asociate acestui părinte
                    String sqlDeleteMesaje = "DELETE FROM mesaj WHERE id_parinte = (SELECT id_parinte FROM parinte WHERE id_utilizator_p = ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDeleteMesaje)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }

                    // 2. Ștergem părintele
                    String sqlParinte = "DELETE FROM parinte WHERE id_utilizator_p = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlParinte)) {
                        ps.setInt(1, idUtilizator);
                        ps.executeUpdate();
                    }
                }
                // Pentru rolul "admin" nu există date suplimentare asociate în alte tabele,
                // deci se sare direct la ștergerea din tabela utilizator, mai jos.

                // Pas final, comun tuturor rolurilor: ștergem rândul din tabela utilizator
                // (înainte, acest pas lipsea complet, motiv pentru care userul rămânea
                // vizibil în listă, deși datele lui asociate fuseseră deja șterse)
                String sqlDeleteUtilizator = "DELETE FROM utilizator WHERE id_utilizator = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteUtilizator)) {
                    ps.setInt(1, idUtilizator);
                    ps.executeUpdate();
                }

                conn.commit(); // Salvăm modificările în DB doar dacă toate query-urile de mai sus au reușit
                JOptionPane.showMessageDialog(this, "Utilizatorul și toate datele sale asociate au fost șterse cu succes!");
                incarcaUtilizatori();

            } catch (SQLException ex) {
                conn.rollback(); // Dacă un singur pas a eșuat, anulăm tot ca să nu stricăm baza de date
                JOptionPane.showMessageDialog(this, "Eroare în timpul ștergerii (tranzacție anulată): " + ex.getMessage());
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la conectarea la baza de date: " + ex.getMessage());
        }
    }
    public void actualizeazaBadgeCereriPublic() {
        actualizeazaBadgeCereri();
    }
}