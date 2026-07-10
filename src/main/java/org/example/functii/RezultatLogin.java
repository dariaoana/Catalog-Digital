package org.example.functii;

public class RezultatLogin {
    private boolean succes;
    private int id_utilizator;
    private String rol;

    public RezultatLogin(boolean succes, int idUtilizator, String rol) {
        this.succes = succes;
        this.id_utilizator = idUtilizator;
        this.rol = rol;
    }

    public boolean isSucces() {
        return succes;
    }

    public int getIdUtilizator() {
        return id_utilizator;
    }

    public String getRol() {
        return rol;
    }
}