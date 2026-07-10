package org.example.interfata;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import org.example.functii.FunctiiElev;
import org.example.functii.PlanInvatamant;

import java.util.List;

public class ElevFrame extends JFrame {

    public String clasaElevCurentString;
    public int clasaElevCurent;
    public int idElevCurent;
    public FunctiiElev functiiElev;

public ElevFrame(int id_elev){
    this.idElevCurent = id_elev;
    this.functiiElev = new FunctiiElev();
    this.clasaElevCurentString= FunctiiElev.determinaClasa(idElevCurent);
    clasaElevCurent=Integer.parseInt(clasaElevCurentString.charAt(0)+"");

    JFrame elevFrame = new JFrame();
    elevFrame.setTitle("Notele mele");
    setSize(800,600);
    elevFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
     initComponente();

}


public void initComponente(){
    JPanel panelElev = new JPanel(new GridLayout(0, 3, 15, 15));

    List<PlanInvatamant> planInvatamant=FunctiiElev.determinaMaterii(clasaElevCurent);
    for(int i=0; i<planInvatamant.size();i++){
        String materie=planInvatamant.get(i).getNumeMaterie();
        String profesor=planInvatamant.get(i).getNumeProfesor();
        panelElev.add(cardMaterie(materie,profesor));
    }
    JScrollPane scrollPane = new JScrollPane(panelElev);
    this.add(scrollPane);
}
public JPanel cardMaterie(String numeMaterie, String numeProfesor){

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(160, 120));
        card.setMaximumSize(new Dimension(160, 120));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

            JLabel lblMaterie = new JLabel(numeMaterie);
            lblMaterie.setHorizontalAlignment(SwingConstants.LEFT);
            lblMaterie.setVerticalAlignment(SwingConstants.TOP);

            JLabel lblProfesor = new JLabel(numeProfesor);
            lblMaterie.setHorizontalAlignment(SwingConstants.LEFT);
            lblMaterie.setVerticalAlignment(SwingConstants.TOP);

            card.add(lblMaterie);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(lblProfesor);
            card.add(Box.createVerticalGlue());

            return card;
     }
}