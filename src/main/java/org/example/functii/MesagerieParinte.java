package org.example.functii;

public class MesagerieParinte {
    private int idParinte;
    private String numeParinte;
    private String numeElev;

    public MesagerieParinte(int idParinte, String numeParinte, String numeElev) {
        this.idParinte = idParinte;
        this.numeParinte = numeParinte;
        this.numeElev = numeElev;
    }

    public int getIdParinte() {
        return idParinte;
    }

    public String getNumeParinte() {
        return numeParinte;
    }

    public String getNumeElev() {
        return numeElev;
    }
}