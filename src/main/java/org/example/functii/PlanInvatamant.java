package org.example.functii;

public class PlanInvatamant {
    int clasaAn;
    String numeMaterie;
    String numeProfesor;

    PlanInvatamant(int clasaAn, String numeMaterie, String numeProfesor) {
        this.clasaAn = clasaAn;
        this.numeMaterie = numeMaterie;
        this.numeProfesor = numeProfesor;
    }

    public int getClasaAn() {
        return clasaAn;
    }
    public String getNumeMaterie() {
        return numeMaterie;
    }
    public String getNumeProfesor() {
        return numeProfesor;
    }
}
