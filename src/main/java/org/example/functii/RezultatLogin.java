package org.example.functii;

public class RezultatLogin {
    private boolean succes;
    private int idUtilizator;
    private String rol;

    public RezultatLogin(boolean succes, int idUtilizator, String rol) {
        this.succes = succes;
        this.idUtilizator = idUtilizator;
        this.rol = rol;
    }

    public boolean isSucces() {
        return succes;
    }

    public int getIdUtilizator() {
        return idUtilizator;
    }

    public String getRol() {
        return rol;
    }
}