package org.example.interfata;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.example.functii.FunctiiMesaj;
import org.example.functii.Mesaj;

public class ChatFrame extends JFrame {

    private int idProfesor;
    private int idParinte;
    private String rolCurent; // "profesor" sau "parinte"

    private JTextArea zonaMesaje;
    private JTextField campMesaj;
    private JButton btnTrimite;

    public ChatFrame(int idProfesor, int idParinte, String rolCurent, String numeInterlocutor) {
        this.idProfesor = idProfesor;
        this.idParinte = idParinte;
        this.rolCurent = rolCurent;

        setTitle("Conversație cu " + numeInterlocutor);
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponente();
        incarcaMesaje();
    }

    private void initComponente() {

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        zonaMesaje = new JTextArea();
        zonaMesaje.setEditable(false);
        zonaMesaje.setLineWrap(true);
        zonaMesaje.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(zonaMesaje);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        panelPrincipal.add(scrollPane);

        JPanel randTrimitere = new JPanel();
        randTrimitere.setLayout(new BoxLayout(randTrimitere, BoxLayout.X_AXIS));
        randTrimitere.setBorder(new EmptyBorder(0, 10, 10, 10));

        campMesaj = new JTextField();
        btnTrimite = new JButton("Trimite");

        randTrimitere.add(campMesaj);
        randTrimitere.add(Box.createHorizontalStrut(8));
        randTrimitere.add(btnTrimite);

        panelPrincipal.add(randTrimitere);

        add(panelPrincipal);

        btnTrimite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trimiteMesajNou();
            }
        });
    }

    private void incarcaMesaje() {
        List<Mesaj> mesaje = FunctiiMesaj.obtineConversatie(idProfesor, idParinte);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mesaje.size(); i++) {
            Mesaj m = mesaje.get(i);
            String eticheta = m.getExpeditor().equals("profesor") ? "Profesor" : "Părinte";
            sb.append("[").append(m.getDataTrimitere()).append("] ");
            sb.append(eticheta).append(": ");
            sb.append(m.getContinut());
            sb.append("\n\n");
        }

        zonaMesaje.setText(sb.toString());
        zonaMesaje.setCaretPosition(zonaMesaje.getDocument().getLength());
    }

    private void trimiteMesajNou() {
        String text = campMesaj.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        boolean succes = FunctiiMesaj.trimiteMesaj(idProfesor, idParinte, rolCurent, text);

        if (succes) {
            campMesaj.setText("");
            incarcaMesaje();
        } else {
            JOptionPane.showMessageDialog(this, "Mesajul nu a putut fi trimis.");
        }
    }
}