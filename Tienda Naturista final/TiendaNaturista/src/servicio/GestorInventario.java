package servicio;

import enumeraciones.TipoProducto;
import modelo.Producto;
import persistencia.ProductoDAO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestorInventario {
    private ProductoDAO productoDAO;

    public GestorInventario() {
        this.productoDAO = new ProductoDAO();
        // Cargar algunos productos iniciales si el archivo está vacío (para pruebas)
        if (productoDAO.cargarProductos().isEmpty()) {
            System.out.println("Inicializando inventario con productos de ejemplo.");
            productoDAO.agregarProducto(new Producto("PROD001", "Miel Pura", 25.50, 100, TipoProducto.ALIMENTICIO, "2026-12-31", "Vitaminas, Minerales"));
            productoDAO.agregarProducto(new Producto("PROD002", "Crema Facial de Aloe", 45.00, 50, TipoProducto.COSMETICO, "Piel Seca", "Aloe Vera, Vitamina E"));
            productoDAO.agregarProducto(new Producto("PROD003", "Té de Hierbas Relajante", 15.00, 75, TipoProducto.MEDICINAL, "2025-06-30", "Manzanilla, Tilo"));
            productoDAO.agregarProducto(new Producto("PROD004", "Jabón Artesanal de Avena", 10.00, 120, TipoProducto.COSMETICO, "Piel Sensible", "Avena, Glicerina"));
            productoDAO.agregarProducto(new Producto("PROD005", "Aceite Esencial Lavanda", 30.00, 40, TipoProducto.OTRO, "Aromaterapia", "Lavandula angustifolia"));
        }
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoDAO.cargarProductos();
    }

    public Optional<Producto> obtenerProductoPorId(String id) {
        return productoDAO.buscarProductoPorId(id);
    }

    public void agregarProducto(Producto producto) {
        productoDAO.agregarProducto(producto);
    }

    public void actualizarProducto(Producto producto) {
        productoDAO.actualizarProducto(producto);
    }
    
    public void eliminarProducto(String id) {
        productoDAO.eliminarProducto(id);
    }

    public boolean reducirStock(String idProducto, int cantidad) {
        Optional<Producto> productoOpt = productoDAO.buscarProductoPorId(idProducto);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            if (producto.getStock() >= cantidad) {
                producto.setStock(producto.getStock() - cantidad);
                productoDAO.actualizarProducto(producto);
                return true;
            } else {
                System.out.println("Stock insuficiente para el producto: " + producto.getNombre());
            }
        } else {
            System.out.println("Producto no encontrado con ID: " + idProducto);
        }
        return false;
    }

    public void aumentarStock(String idProducto, int cantidad) {
        Optional<Producto> productoOpt = productoDAO.buscarProductoPorId(idProducto);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setStock(producto.getStock() + cantidad);
            productoDAO.actualizarProducto(producto);
        } else {
            System.out.println("Producto no encontrado con ID: " + idProducto);
        }
    }
}
