package persistencia;

import enumeraciones.TipoProducto;
import modelo.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductoDAO {
    private static final String FILE_NAME = "productos.csv";

    public List<Producto> cargarProductos() {
        List<Producto> productos = new ArrayList<>();
        List<String> lineas = GestorArchivos.leerArchivo(FILE_NAME);

        for (String linea : lineas) {
            String[] partes = linea.split(",");
            if (partes.length >= 5) { // ID, Nombre, Precio, Stock, Tipo, Detalle1, Detalle2
                String id = partes[0];
                String nombre = partes[1];
                double precio = Double.parseDouble(partes[2]);
                int stock = Integer.parseInt(partes[3]);
                TipoProducto tipo = TipoProducto.valueOf(partes[4]);
                
                String detalle1 = partes.length > 5 ? partes[5] : "";
                String detalle2 = partes.length > 6 ? partes[6] : "";

                productos.add(new Producto(id, nombre, precio, stock, tipo, detalle1, detalle2));
            } else {
                System.err.println("Advertencia: Línea de producto incompleta o mal formada: " + linea);
            }
        }
        return productos;
    }

    public void guardarProductos(List<Producto> productos) {
        List<String> lineas = productos.stream()
                                     .map(Producto::toString)
                                     .collect(Collectors.toList());
        GestorArchivos.reescribirArchivo(FILE_NAME, lineas);
    }

    public Optional<Producto> buscarProductoPorId(String id) {
        return cargarProductos().stream()
                                .filter(p -> p.getId().equals(id))
                                .findFirst();
    }

    public void agregarProducto(Producto producto) {
        List<Producto> productos = cargarProductos();
        productos.add(producto);
        guardarProductos(productos);
    }

    public void actualizarProducto(Producto productoActualizado) {
        List<Producto> productos = cargarProductos();
        boolean encontrado = false;
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equals(productoActualizado.getId())) {
                productos.set(i, productoActualizado);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            guardarProductos(productos);
        } else {
            System.err.println("Producto con ID " + productoActualizado.getId() + " no encontrado para actualizar.");
        }
    }
    
    public void eliminarProducto(String id) {
        List<Producto> productos = cargarProductos();
        List<Producto> productosActualizados = productos.stream()
                .filter(p -> !p.getId().equals(id))
                .collect(Collectors.toList());
        if (productosActualizados.size() < productos.size()) { 
            guardarProductos(productosActualizados);
        } else {
            System.err.println("Producto con ID " + id + " no encontrado para eliminar.");
        }
    }
}
