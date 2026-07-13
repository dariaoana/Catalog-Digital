package org.example.functii;

public class PlanInvatamant {
    int clasaAn;
    String numeMaterie;
    String numeProfesor;
    int idMaterie;
    PlanInvatamant(int clasaAn, String numeMaterie, String numeProfesor) {
        this.clasaAn = clasaAn;
        this.numeMaterie = numeMaterie;
        this.numeProfesor = numeProfesor;
    }
    PlanInvatamant(int clasaAn, String numeMaterie, String numeProfesor,  int idMaterie) {
        this.clasaAn = clasaAn;
        this.numeMaterie = numeMaterie;
        this.numeProfesor = numeProfesor;
        this.idMaterie = idMaterie;
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
    public int getIdMaterie() {
        return idMaterie;
    }
}
