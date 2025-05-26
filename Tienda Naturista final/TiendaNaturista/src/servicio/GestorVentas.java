package servicio;


import enumeraciones.EstadoVenta;
import enumeraciones.MetodoPago;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;
import persistencia.VentaDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorVentas {
    private VentaDAO ventaDAO;
    private GestorInventario gestorInventario;
    private static int contadorVentas = 0; 

    public GestorVentas(GestorInventario gestorInventario) {
        this.ventaDAO = new VentaDAO();
        this.gestorInventario = gestorInventario;
        // Inicializar contador de ventas basándose en las ventas existentes
        List<Venta> ventasExistentes = ventaDAO.cargarVentas();
        if (!ventasExistentes.isEmpty()) {
            Optional<Integer> maxId = ventasExistentes.stream()
                                                      .map(v -> Integer.parseInt(v.getId().replace("VENTA-", "")))
                                                      .max(Integer::compare);
            maxId.ifPresent(integer -> contadorVentas = integer);
        }
    }

    public List<Venta> obtenerTodasLasVentas() {
        return ventaDAO.cargarVentas();
    }

    public boolean registrarNuevaVenta(List<DetalleVenta> detallesVenta, MetodoPago metodoPago, String empleadoId) {
        if (detallesVenta == null || detallesVenta.isEmpty()) {
            System.out.println("No se pueden registrar ventas sin detalles.");
            return false;
        }

        double totalVenta = 0;
        List<DetalleVenta> detallesValidados = new ArrayList<>();

        // 1. Validar stock y calcular total
        for (DetalleVenta detalle : detallesVenta) {
            Optional<Producto> productoOpt = gestorInventario.obtenerProductoPorId(detalle.getProductoId());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                if (producto.getStock() >= detalle.getCantidad()) {
                    totalVenta += detalle.getCantidad() * producto.getPrecio();
                    detallesValidados.add(detalle);
                } else {
                    System.err.println("Error: Stock insuficiente para " + producto.getNombre() + ". Stock disponible: " + producto.getStock() + ", requerido: " + detalle.getCantidad());
                    return false; // No se puede completar la venta si el stock es insuficiente
                }
            } else {
                System.err.println("Error: Producto con ID " + detalle.getProductoId() + " no encontrado.");
                return false; // Producto no encontrado
            }
        }

        // 2. Reducir stock
        for (DetalleVenta detalle : detallesValidados) {
            gestorInventario.reducirStock(detalle.getProductoId(), detalle.getCantidad());
        }

        // 3. Generar ID de venta y registrar
        contadorVentas++;
        String idVenta = "VENTA-" + String.format("%04d", contadorVentas);
        Venta nuevaVenta = new Venta(idVenta, LocalDateTime.now(), detallesValidados, totalVenta, metodoPago, EstadoVenta.COMPLETADA, empleadoId);
        ventaDAO.agregarVenta(nuevaVenta);

        System.out.println("Venta " + idVenta + " registrada exitosamente. Total: " + totalVenta);
        return true;
    }
    
    // Método para anular una venta
    public boolean anularVenta(String ventaId) {
        List<Venta> ventas = ventaDAO.cargarVentas();
        Optional<Venta> ventaOpt = ventas.stream().filter(v -> v.getId().equals(ventaId)).findFirst();

        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            if (venta.getEstado() == EstadoVenta.COMPLETADA) {
                // Devolver productos al inventario
                for (DetalleVenta detalle : venta.getDetalles()) {
                    gestorInventario.aumentarStock(detalle.getProductoId(), detalle.getCantidad());
                }
                venta.setEstado(EstadoVenta.ANULADA);
                ventaDAO.actualizarVenta(venta);
                System.out.println("Venta " + ventaId + " anulada exitosamente.");
                return true;
            } else {
                System.out.println("La venta " + ventaId + " no puede ser anulada porque su estado es " + venta.getEstado());
                return false;
            }
        } else {
            System.err.println("Venta con ID " + ventaId + " no encontrada.");
            return false;
        }
    }
}
