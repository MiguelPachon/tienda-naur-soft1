package modelo;

import enumeraciones.Roles;

public abstract class Usuario {
    protected String id;
    protected String nombreUsuario;
    protected String contrasena;
    protected Roles rol;

    public Usuario(String id, String nombreUsuario, String contrasena, Roles rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public Roles getRol() {
        return rol;
    }
    
    // Setters
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public String toString() {
        return id + "," + nombreUsuario + "," + contrasena + "," + rol.name();
    }
    
    public abstract String obtenerInfoAdicional();
}