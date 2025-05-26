package modelo;

import enumeraciones.Roles;

public class Empleado extends Usuario {


    public Empleado(String id, String nombreUsuario, String contrasena) {
        super(id, nombreUsuario, contrasena, Roles.EMPLEADO);
    }

    @Override
    public String obtenerInfoAdicional() {
        return "Rol: Empleado";
    }

}
