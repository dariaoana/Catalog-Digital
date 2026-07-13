package org.example.interfata;

import org.example.JDBC;

import javax.swing.*;
import org.example.functii.FunctiiElev;
import org.example.functii.PlanInvatamant;

import java.awt.*;
import java.util.List;

import static org.example.functii.FunctiiElev.numeElev;

public class ParinteFrame extends JFrame {

        private int idUtilizatorParinte;
        private int idCopil;
        private String clasa;
        private int clasaCopil;
        public ParinteFrame(){
            JFrame fereastraParinte=new JFrame();
            fereastraParinte.setTitle("Situatie Elev"+ numeElev(idUtilizatorParinte) );
            fereastraParinte.setSize(400,400);
            fereastraParinte.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            this.clasa= FunctiiElev.determinaClasa(idCopil);
            clasaCopil = Integer.parseInt(clasa.charAt(0) + "");
            initComponente();
        }
        public void initComponente(){
            JPanel panelParinte=new JPanel();


        }


    public JPanel cardMaterie(String numeMaterie, String numeProfesor, List<Integer> note) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(160, 140));
        card.setMaximumSize(new Dimension(160, 140));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblMaterie = new JLabel(numeMaterie);
        lblMaterie.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel lblProfesor = new JLabel(numeProfesor);
        lblProfesor.setHorizontalAlignment(SwingConstants.LEFT);

        String textNote;
        if (note.isEmpty()) {
            textNote = "Fără note";
        } else {
            StringBuilder sb = new StringBuilder();
            double suma = 0;
            for (int i = 0; i < note.size(); i++) {
                sb.append(note.get(i));
                if (i < note.size() - 1) sb.append(", ");
                suma += note.get(i);
            }
            double media = suma / note.size();
            textNote = "Note: " + sb.toString();
            JLabel lblMedia = new JLabel(String.format("Medie: %.2f", media));

            card.add(lblMaterie);
            card.add(Box.createRigidArea(new Dimension(0, 6)));
            card.add(lblProfesor);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(new JLabel(textNote));
            card.add(Box.createRigidArea(new Dimension(0, 4)));
            card.add(lblMedia);
            card.add(Box.createVerticalGlue());

            return card;
        }

        card.add(lblMaterie);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblProfesor);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(new JLabel(textNote));
        card.add(Box.createVerticalGlue());

        return card;
    }

}