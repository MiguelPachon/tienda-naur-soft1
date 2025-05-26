package servicio;

import modelo.Usuario;
import persistencia.UsuarioDAO; // Importar UsuarioDAO

public class GestorAutenticacion {
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogueado; // Ahora es un Usuario

    public GestorAutenticacion() {
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioDAO.inicializarUsuariosSiVacio(); 
    }

    public boolean autenticar(String nombreUsuario, String contrasena) {
        usuarioLogueado = usuarioDAO.buscarUsuarioPorNombreUsuario(nombreUsuario)
                                       .orElse(null);
        if (usuarioLogueado != null && usuarioLogueado.getContrasena().equals(contrasena)) {
            return true;
        }
        usuarioLogueado = null; // Limpiar si la autenticación falla
        return false;
    }

    public Usuario getUsuarioLogueado() { // Ahora retorna Usuario
        return usuarioLogueado;
    }
    
    public void cerrarSesion() {
        usuarioLogueado = null;
    }
}
