package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import org.example.functii.FunctiiElev;
import org.example.functii.PlanInvatamant;
import org.example.functii.Nota;

public class ElevFrame extends JFrame {

    public String clasaElevCurentString;
    public int clasaElevCurent;
    public int idElevCurent;
    public String numeElevCurent;
    public FunctiiElev functiiElev;

    public ElevFrame(int id_elev) {
        this.idElevCurent = id_elev;
        this.functiiElev = new FunctiiElev();

        this.clasaElevCurentString = FunctiiElev.determinaClasa(idElevCurent);
        this.numeElevCurent = FunctiiElev.determinaNumeComplet(idElevCurent);
        this.clasaElevCurent = Integer.parseInt(clasaElevCurentString.charAt(0) + "");

        setTitle("Catalog Digital - Notele mele");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponente();
    }

    public void initComponente() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // ---------- ANTET (FĂRĂ CULORI CUSTOM) ----------
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setBorder(new EmptyBorder(15, 20, 10, 20));
        // Am scos setBackground ca să rămână culoarea implicită a sistemului

        JLabel lblElev = new JLabel("<html><b>Elev:</b> " + numeElevCurent + "</html>");
        lblElev.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblClasa = new JLabel("<html><b>Clasa:</b> " + clasaElevCurentString + "</html>");
        lblClasa.setFont(new Font("Arial", Font.PLAIN, 14));

        panelHeader.add(lblElev);
        panelHeader.add(Box.createVerticalStrut(4));
        panelHeader.add(lblClasa);
        panelHeader.add(Box.createVerticalStrut(10));
        panelHeader.add(new JSeparator());

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // ---------- GRID CARDURI ----------
        JPanel panelElev = new JPanel(new GridLayout(0, 3, 15, 15));
        panelElev.setBorder(new EmptyBorder(15, 20, 15, 20));

        List<PlanInvatamant> planInvatamant = FunctiiElev.determinaMaterii(clasaElevCurent);
        for (int i = 0; i < planInvatamant.size(); i++) {
            String materie = planInvatamant.get(i).getNumeMaterie();
            String profesor = planInvatamant.get(i).getNumeProfesor();
            int idMaterie = planInvatamant.get(i).getIdMaterie();

            List<Nota> note = FunctiiElev.determinaNoteCuData(idElevCurent, idMaterie);

            panelElev.add(cardMaterie(materie, profesor, note));
        }

        JScrollPane scrollPane = new JScrollPane(panelElev);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        this.add(panelPrincipal);
    }

    public JPanel cardMaterie(String numeMaterie, String numeProfesor, List<Nota> note) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setPreferredSize(new Dimension(180, 170));
        card.setMaximumSize(new Dimension(180, 170));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        // Am scos card.setBackground(Color.WHITE) -> rămâne griul lui standard

        JLabel lblMaterie = new JLabel("<html><b>" + numeMaterie + "</b></html>");

        JLabel lblProfesor = new JLabel("Prof: " + numeProfesor);


        card.add(lblMaterie);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblProfesor);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        if (note.isEmpty()) {
            JLabel lblFaraNote = new JLabel("Fără note");
            card.add(lblFaraNote);
        } else {
            StringBuilder sb = new StringBuilder("<html><font size='2'>");
            for (int i = 0; i < note.size(); i++) {
                Nota n = note.get(i);

                // --- SCOATEREA OREI DIN DATĂ ---
                String dataCompleta = n.getData();
                String doarData = dataCompleta;
                if (dataCompleta != null && dataCompleta.contains(" ")) {
                    // Dacă data este formatată ca "YYYY-MM-DD HH:MM:SS", tăiem după spațiu
                    doarData = dataCompleta.split(" ")[0];
                }

                sb.append("<b>Nota ").append(n.getValoare()).append("</b>")
                        .append(" (").append(doarData).append(")");

                if (i < note.size() - 1) {
                    sb.append("<br>");
                }
            }
            sb.append("</font></html>");

            JLabel lblNote = new JLabel(sb.toString());
            lblNote.setVerticalAlignment(SwingConstants.TOP);

            JScrollPane noteScroll = new JScrollPane(lblNote);
            noteScroll.setBorder(null);
            noteScroll.setOpaque(false);
            noteScroll.getViewport().setOpaque(false);

            card.add(noteScroll);
        }

        card.add(Box.createVerticalGlue());
        return card;
    }
}