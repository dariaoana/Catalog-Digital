package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.example.functii.FunctiiElev;
import org.example.functii.PlanInvatamant;
import org.example.functii.Nota;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ElevFrame extends JFrame {

    public String clasaElevCurentString;
    public int clasaElevCurent;
    public int idElevCurent;
    public String numeElevCurent;
    public FunctiiElev functiiElev;

    // Elemente pentru tab-ul de Istoric / Semestre
    private JComboBox<String> comboAnScolar;
    private JTable tabelSituatie;
    private DefaultTableModel modelTabel;
    private JButton btnExport;

    public ElevFrame(int id_elev) {
        this.idElevCurent = id_elev;
        this.functiiElev = new FunctiiElev();

        this.clasaElevCurentString = FunctiiElev.determinaClasa(idElevCurent);
        this.numeElevCurent = FunctiiElev.determinaNumeComplet(idElevCurent);
        this.clasaElevCurent = Integer.parseInt(clasaElevCurentString.charAt(0) + "");

        setTitle("Catalog Digital - Notele mele");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponente();
    }

    public void initComponente() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel lblElev = new JLabel("<html><b>Elev:</b> " + numeElevCurent + "</html>");
        lblElev.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblClasa = new JLabel("<html><b>Clasa: </b> " + clasaElevCurentString + "</html>");
        lblClasa.setFont(new Font("Arial", Font.PLAIN, 14));

        panelHeader.add(lblElev);
        panelHeader.add(Box.createVerticalStrut(4));
        panelHeader.add(lblClasa);
        panelHeader.add(Box.createVerticalStrut(10));
        panelHeader.add(new JSeparator());

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Tab-uri
        tabbedPane.addTab("Note", creeazaPanelCarduri());
        tabbedPane.addTab("Situație Scolara", creeazaPanelIstoricTabel());

        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        this.add(panelPrincipal);
    }

    private JPanel creeazaPanelCarduri() {
        JPanel panelCarduri = new JPanel(new BorderLayout());

        JPanel gridCarduri = new JPanel(new GridLayout(0, 3, 15, 15));
        gridCarduri.setBorder(new EmptyBorder(15, 20, 15, 20));

        List<PlanInvatamant> planInvatamant = FunctiiElev.determinaMaterii(clasaElevCurent);
        for (PlanInvatamant pi : planInvatamant) {
            List<Nota> note = FunctiiElev.determinaNoteCuData(idElevCurent, pi.getIdMaterie());
            gridCarduri.add(cardMaterieSimplu(pi.getNumeMaterie(), pi.getNumeProfesor(), note));
        }

        JScrollPane scrollPane = new JScrollPane(gridCarduri);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelCarduri.add(scrollPane, BorderLayout.CENTER);

        return panelCarduri;
    }

    public JPanel cardMaterieSimplu(String numeMaterie, String numeProfesor, List<Nota> note) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setPreferredSize(new Dimension(180, 140));
        card.setMaximumSize(new Dimension(180, 140));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblMaterie = new JLabel("<html><b>" + numeMaterie + "</b></html>");
        lblMaterie.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblProfesor = new JLabel("Prof: " + numeProfesor);
        lblProfesor.setForeground(Color.GRAY);
        lblProfesor.setFont(new Font("Arial", Font.ITALIC, 11));

        card.add(lblMaterie);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblProfesor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        if (note.isEmpty()) {
            JLabel lblFaraNote = new JLabel("Fără note");
            card.add(lblFaraNote);
        } else {
            // Reconstituim lista simplă de note separate prin virgulă
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < note.size(); i++) {
                sb.append(note.get(i).getValoare());
                if (i < note.size() - 1) {
                    sb.append(", ");
                }
            }

            JLabel lblNote = new JLabel("<html><b>Note:</b> " + sb.toString() + "</html>");
            lblNote.setFont(new Font("Arial", Font.PLAIN, 12));
            card.add(lblNote);
        }

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel creeazaPanelIstoricTabel() {
        JPanel panelIstoric = new JPanel(new BorderLayout());

        // Selector Clasă
        JPanel randFiltru = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        randFiltru.add(new JLabel("Alege anul școlar de vizualizat: "));

        comboAnScolar = new JComboBox<>();
        for (int i = 5; i <= clasaElevCurent; i++) {
            comboAnScolar.addItem("Clasa a " + i + "-a");
        }
        comboAnScolar.setSelectedIndex(comboAnScolar.getItemCount() - 1);
        randFiltru.add(comboAnScolar);

        panelIstoric.add(randFiltru, BorderLayout.NORTH);


        String[] coloane = {"Materie", "Note Semestrul I", "Medie S1", "Note Semestrul II", "Medie S2"};
        modelTabel = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelSituatie = new JTable(modelTabel);
        tabelSituatie.setRowHeight(32);
        tabelSituatie.setFont(new Font("Arial", Font.PLAIN, 12));

        tabelSituatie.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabelSituatie.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabelSituatie.getColumnModel().getColumn(2).setPreferredWidth(70);
        tabelSituatie.getColumnModel().getColumn(3).setPreferredWidth(250);
        tabelSituatie.getColumnModel().getColumn(4).setPreferredWidth(70);

        JScrollPane scrollTabel = new JScrollPane(tabelSituatie);
        scrollTabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panelIstoric.add(scrollTabel, BorderLayout.CENTER);


        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        btnExport = new JButton("Exporta PDF");
        btnExport.setFont(new Font("Arial", Font.BOLD, 12));
        footer.add(btnExport);

        JPanel panelSud = new JPanel();
        panelSud.setLayout(new BoxLayout(panelSud, BoxLayout.Y_AXIS));
        panelSud.add(new JSeparator());
        panelSud.add(footer);
        panelIstoric.add(panelSud, BorderLayout.SOUTH);

        comboAnScolar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incarcaSituatieScolara();
            }
        });

        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportaSituatiaInPDF();
            }
        });

        incarcaSituatieScolara();

        return panelIstoric;
    }

    private void incarcaSituatieScolara() {
        if (modelTabel == null || comboAnScolar == null) return;
        modelTabel.setRowCount(0);

        String selectat = (String) comboAnScolar.getSelectedItem();
        if (selectat == null) return;
        int clasaSelectata = Integer.parseInt(selectat.replaceAll("[^0-9]", ""));

        int diferentaAni = clasaElevCurent - clasaSelectata;
        int anulCalendaristicTinta = 2026 - diferentaAni;

        List<PlanInvatamant> plan = FunctiiElev.determinaMaterii(clasaSelectata);

        for (PlanInvatamant pi : plan) {
            List<Nota> toateNotele = FunctiiElev.determinaNoteIstoric(idElevCurent, pi.getIdMaterie(), anulCalendaristicTinta);

            List<Nota> noteSem1 = new ArrayList<>();
            List<Nota> noteSem2 = new ArrayList<>();
            double suma1 = 0, suma2 = 0;

            for (Nota n : toateNotele) {
                String data = n.getData();
                if (data != null && data.length() >= 10) {
                    String lunaZi = data.contains(" ") ? data.split(" ")[0].substring(5, 10) : data.substring(5, 10);

                    if (lunaZi.compareTo("09-01") >= 0 || lunaZi.compareTo("01-31") <= 0) {
                        noteSem1.add(n);
                        suma1 += n.getValoare();
                    } else {
                        noteSem2.add(n);
                        suma2 += n.getValoare();
                    }
                }
            }

            StringBuilder sb1 = new StringBuilder();
            for (int j = 0; j < noteSem1.size(); j++) {
                sb1.append(noteSem1.get(j).getValoare());
                if (j < noteSem1.size() - 1) sb1.append(", ");
            }
            String med1 = noteSem1.isEmpty() ? "—" : String.format("%.2f", (suma1 / noteSem1.size()));

            StringBuilder sb2 = new StringBuilder();
            for (int j = 0; j < noteSem2.size(); j++) {
                sb2.append(noteSem2.get(j).getValoare());
                if (j < noteSem2.size() - 1) sb2.append(", ");
            }
            String med2 = noteSem2.isEmpty() ? "—" : String.format("%.2f", (suma2 / noteSem2.size()));

            modelTabel.addRow(new Object[]{
                    pi.getNumeMaterie(),
                    sb1.length() == 0 ? "Fără note" : sb1.toString(),
                    med1,
                    sb2.length() == 0 ? "Fără note" : sb2.toString(),
                    med2
            });
        }
    }

    private void exportaSituatiaInPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvează Foaia Matricolă");
        fileChooser.setSelectedFile(new File("Situatie_Scolara_" + numeElevCurent.replace(" ", "_") + "_" + comboAnScolar.getSelectedItem() + ".pdf"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
            document.open();

            document.add(new Paragraph("Situatie scolara"));
            document.add(new Paragraph("Elev: " + numeElevCurent));
            document.add(new Paragraph("Clasa: " + clasaElevCurentString));
            document.add(new Paragraph("Anul vizualizat: " + comboAnScolar.getSelectedItem()));
            document.add(new Paragraph("Data generării: " + new java.util.Date().toString() + "\n\n"));

            PdfPTable pdfTable = new PdfPTable(tabelSituatie.getColumnCount());
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{2.5f, 3.5f, 1.2f, 3.5f, 1.2f});

            for (int i = 0; i < tabelSituatie.getColumnCount(); i++) {
                pdfTable.addCell(tabelSituatie.getColumnName(i));
            }

            for (int r = 0; r < tabelSituatie.getRowCount(); r++) {
                for (int c = 0; c < tabelSituatie.getColumnCount(); c++) {
                    pdfTable.addCell(tabelSituatie.getValueAt(r, c).toString());
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "Foaia matricolă a fost exportată în siguranță sub formă de PDF!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Eroare la generarea fișierului PDF: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }
}