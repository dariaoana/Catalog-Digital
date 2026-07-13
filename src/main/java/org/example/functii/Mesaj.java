package org.example.functii;

import java.sql.Timestamp;

public class Mesaj {
    private int idMesaj;
    private String expeditor;
    private String continut;
    private Timestamp dataTrimitere;

    public Mesaj(int idMesaj, String expeditor, String continut, Timestamp dataTrimitere) {
        this.idMesaj = idMesaj;
        this.expeditor = expeditor;
        this.continut = continut;
        this.dataTrimitere = dataTrimitere;
    }

    public int getIdMesaj() {
        return idMesaj;
    }

    public String getExpeditor() {
        return expeditor;
    }

    public String getContinut() {
        return continut;
    }

    public Timestamp getDataTrimitere() {
        return dataTrimitere;
    }
}