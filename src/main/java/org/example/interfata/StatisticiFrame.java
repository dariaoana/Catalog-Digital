package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Date;

import org.example.JDBC;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

// Importuri pentru PDF (OpenPDF)
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

class StatisticiProfesorFrame extends JFrame {

    private String clasaSelectata;
    private int idMaterie;
    private String numeMaterie;
    private JFreeChart barChart; // Am făcut graficul variabilă de instanță pentru a-l putea exporta

    private static final Color CULOARE_ACCENT = new Color(52, 100, 189);
    private static final Color CULOARE_FUNDAL = new Color(248, 249, 251);

    public StatisticiProfesorFrame(String clasaSelectata, int idMaterie, String numeMaterie) {
        this.clasaSelectata = clasaSelectata;
        this.idMaterie = idMaterie;
        this.numeMaterie = numeMaterie;

        setTitle("Statistici Catalog - " + numeMaterie);
        setSize(750, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponente();
    }

    private void initComponente() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ---------- HEADER PANELS ----------
        JPanel panelHeaderWrapper = new JPanel(new BorderLayout());
        panelHeaderWrapper.setBackground(CULOARE_ACCENT);
        panelHeaderWrapper.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Panoul cu titlurile (Stânga)
        JPanel panelTitluri = new JPanel();
        panelTitluri.setLayout(new BoxLayout(panelTitluri, BoxLayout.Y_AXIS));
        panelTitluri.setOpaque(false);

        String clasaText = (clasaSelectata == null || clasaSelectata.equals("Toate clasele"))
                ? "Toți elevii"
                : "Clasa " + clasaSelectata;

        JLabel lblTitlu = new JLabel("Distribuția notelor la " + numeMaterie);
        lblTitlu.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitlu.setForeground(Color.WHITE);
        lblTitlu.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitlu = new JLabel(clasaText);
        lblSubtitlu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitlu.setForeground(new Color(220, 228, 245));
        lblSubtitlu.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTitluri.add(lblTitlu);
        panelTitluri.add(Box.createVerticalStrut(4));
        panelTitluri.add(lblSubtitlu);

        panelHeaderWrapper.add(panelTitluri, BorderLayout.WEST);

        // Butonul de Export (Dreapta)
        JButton btnExport = new JButton("Exportă PDF");
        btnExport.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnExport.setBackground(Color.WHITE);
        btnExport.setForeground(CULOARE_ACCENT);
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Îl punem într-un FlowLayout ca să își păstreze dimensiunile ideale
        JPanel panelButon = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        panelButon.setOpaque(false);
        panelButon.add(btnExport);

        panelHeaderWrapper.add(panelButon, BorderLayout.EAST);

        add(panelHeaderWrapper, BorderLayout.NORTH);

        // ---------- DATE ----------
        DefaultCategoryDataset dataset = genereazaDatasetNote();

        if (dataset.getRowCount() == 0) {
            btnExport.setEnabled(false); // Dezactivăm butonul dacă nu avem ce exporta

            JPanel panelGol = new JPanel();
            panelGol.setBackground(Color.WHITE);
            panelGol.setLayout(new GridBagLayout());

            JLabel lblMesaj = new JLabel("Nu există note înregistrate pentru această selecție.");
            lblMesaj.setFont(new Font("SansSerif", Font.ITALIC, 14));
            lblMesaj.setForeground(new Color(130, 130, 130));

            panelGol.add(lblMesaj);
            add(panelGol, BorderLayout.CENTER);
            return;
        }

        // ---------- GRAFIC ----------
        barChart = ChartFactory.createBarChart(
                null,
                "Nota primită",
                "Număr de note",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        stilizeazaGrafic(barChart);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(700, 420));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        add(chartPanel, BorderLayout.CENTER);

        // ---------- ASCULTĂTOR BUTON EXPORT ----------
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportaSituatieSiGraficPDF();
            }
        });
    }

    private void stilizeazaGrafic(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(CULOARE_FUNDAL);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(225, 228, 232));
        plot.setDomainGridlinesVisible(false);

        CategoryAxis axaX = plot.getDomainAxis();
        axaX.setLabelFont(new Font("SansSerif", Font.PLAIN, 13));
        axaX.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        axaX.setLabelPaint(new Color(80, 80, 80));
        axaX.setTickLabelPaint(new Color(80, 80, 80));

        NumberAxis axaY = (NumberAxis) plot.getRangeAxis();
        axaY.setLabelFont(new Font("SansSerif", Font.PLAIN, 13));
        axaY.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        axaY.setLabelPaint(new Color(80, 80, 80));
        axaY.setTickLabelPaint(new Color(80, 80, 80));
        axaY.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CULOARE_ACCENT);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.12);
        renderer.setItemMargin(0.05);
    }

    private void exportaSituatieSiGraficPDF() {
        if (barChart == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvează Raportul Grafic");
        String clasaNume = (clasaSelectata == null || clasaSelectata.equals("Toate clasele")) ? "Toate_Clasele" : clasaSelectata;
        fileChooser.setSelectedFile(new File("Raport_Note_" + numeMaterie.replace(" ", "_") + "_" + clasaNume + ".pdf"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fisierDestinatie = fileChooser.getSelectedFile();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fisierDestinatie));
            document.open();

            // 1. Text și metadate raport
            document.add(new Paragraph("Raport statistic" + numeMaterie));
            String clasaInfo = (clasaSelectata == null || clasaSelectata.equals("Toate clasele")) ? "Toți elevii înscriși" : "Clasa " + clasaSelectata;
            document.add(new Paragraph("Clasa:  " + clasaInfo));
            document.add(new Paragraph("Data:  " + new Date().toString()));
            document.add(new Paragraph("\n\n")); // Spațiere înainte de grafic

            // 2. RENDERING GRAFIC ÎN IMAGINE ȘI INSERARE ÎN PDF
            // Generăm o imagine Buffered din graficul nostru JFreeChart
            int latimeImg = 500;
            int inaltimeImg = 320;
            BufferedImage bufferedImage = barChart.createBufferedImage(latimeImg, inaltimeImg);

            // Convertim BufferedImage într-o instanță de Image acceptată de OpenPDF
            Image imagineGrafic = Image.getInstance(bufferedImage, null);
            imagineGrafic.setAlignment(Image.ALIGN_CENTER);

            document.add(imagineGrafic);

            document.close();
            JOptionPane.showMessageDialog(this, "Raportul grafic PDF a fost exportat cu succes!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Eroare la generarea fișierului PDF: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private DefaultCategoryDataset genereazaDatasetNote() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        StringBuilder sql = new StringBuilder(
                "SELECT n.valoare, COUNT(*) AS total " +
                        "FROM nota n " +
                        "INNER JOIN elev e ON n.elev = e.id_elev " +
                        "WHERE n.materie = ? "
        );

        if (clasaSelectata != null && !clasaSelectata.equals("Toate clasele")) {
            sql.append("AND e.clasa = ? ");
        }

        sql.append("GROUP BY n.valoare ORDER BY n.valoare ASC");

        try (Connection conn = JDBC.conecteaza();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, idMaterie);
            if (clasaSelectata != null && !clasaSelectata.equals("Toate clasele")) {
                ps.setString(2, clasaSelectata);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int nota = rs.getInt("valoare");
                    int total = rs.getInt("total");
                    dataset.addValue(total, "Note", String.valueOf(nota));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare generare statistici: " + e.getMessage());
        }

        return dataset;
    }
}