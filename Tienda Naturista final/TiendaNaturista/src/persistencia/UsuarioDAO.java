package persistencia;

import enumeraciones.Roles;
import modelo.Administrador;
import modelo.Empleado;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuarioDAO {
    private static final String FILE_NAME = "usuarios.csv"; 

    public List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        List<String> lineas = GestorArchivos.leerArchivo(FILE_NAME);

        for (String linea : lineas) {
            String[] partes = linea.split(",");
            if (partes.length >= 4) { // ID, NombreUsuario, Contrasena, Rol
                String id = partes[0];
                String nombreUsuario = partes[1];
                String contrasena = partes[2];
                Roles rol = Roles.valueOf(partes[3]);

                if (rol == Roles.ADMINISTRADOR) {
                    usuarios.add(new Administrador(id, nombreUsuario, contrasena));
                } else if (rol == Roles.EMPLEADO) {
                    usuarios.add(new Empleado(id, nombreUsuario, contrasena));
                } else {
                    System.err.println("Advertencia: Rol de usuario no reconocido: " + linea);
                }
            } else {
                System.err.println("Advertencia: Línea de usuario incompleta o mal formada: " + linea);
            }
        }
        return usuarios;
    }

    public void guardarUsuarios(List<Usuario> usuarios) {
        List<String> lineas = usuarios.stream()
                                     .map(Usuario::toString)
                                     .collect(Collectors.toList());
        GestorArchivos.reescribirArchivo(FILE_NAME, lineas);
    }

    public Optional<Usuario> buscarUsuarioPorNombreUsuario(String nombreUsuario) {
        return cargarUsuarios().stream()
                                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                                .findFirst();
    }

    // Método para inicializar usuarios si el archivo está vacío
    public void inicializarUsuariosSiVacio() {
        List<Usuario> usuarios = cargarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No se encontraron usuarios, inicializando con datos por defecto.");
            usuarios.add(new Administrador("ADM001", "admin", "adminpass"));
            usuarios.add(new Empleado("EMP001", "empleado", "empleadopass"));
            guardarUsuarios(usuarios);
        }
    }
}