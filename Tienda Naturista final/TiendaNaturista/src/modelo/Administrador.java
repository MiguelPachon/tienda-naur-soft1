package modelo;

import enumeraciones.Roles;

public class Administrador extends Usuario {


    public Administrador(String id, String nombreUsuario, String contrasena) {
        super(id, nombreUsuario, contrasena, Roles.ADMINISTRADOR);
    }

    @Override
    public String obtenerInfoAdicional() {
        return "Rol: Administrador (Acceso total)";
    }
    
}
