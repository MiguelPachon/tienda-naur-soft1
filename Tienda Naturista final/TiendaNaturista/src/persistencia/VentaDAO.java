package persistencia;

import enumeraciones.EstadoVenta;
import enumeraciones.MetodoPago;
import modelo.DetalleVenta;
import modelo.Venta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VentaDAO {
    private static final String FILE_NAME = "ventas.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Venta> cargarVentas() {
        List<Venta> ventas = new ArrayList<>();
        List<String> lineas = GestorArchivos.leerArchivo(FILE_NAME);

        for (String linea : lineas) {
            String[] partes = linea.split(",");
            if (partes.length >= 6) { 
                String id = partes[0];
                LocalDateTime fechaHora = LocalDateTime.parse(partes[1], FORMATTER);
                double total = Double.parseDouble(partes[2]);
                MetodoPago metodoPago = MetodoPago.valueOf(partes[3]);
                EstadoVenta estado = EstadoVenta.valueOf(partes[4]);
                String empleadoId = partes[5];

                List<DetalleVenta> detalles = new ArrayList<>();
                if (partes.length > 6) { 
                    String detallesStr = partes[6];
                    String[] detallePartes = detallesStr.split("\\|");
                    for (String dStr : detallePartes) {
                        String[] itemPartes = dStr.split(";");
                        if (itemPartes.length == 4) { // productoId;nombreProducto;cantidad;precioUnitario
                            detalles.add(new DetalleVenta(
                                itemPartes[0],
                                itemPartes[1],
                                Integer.parseInt(itemPartes[2]),
                                Double.parseDouble(itemPartes[3])
                            ));
                        }
                    }
                }
                ventas.add(new Venta(id, fechaHora, detalles, total, metodoPago, estado, empleadoId));
            } else {
                System.err.println("Advertencia: Línea de venta incompleta o mal formada: " + linea);
            }
        }
        return ventas;
    }

    public void guardarVentas(List<Venta> ventas) {
        List<String> lineas = ventas.stream()
                                     .map(Venta::toString)
                                     .collect(Collectors.toList());
        GestorArchivos.reescribirArchivo(FILE_NAME, lineas);
    }

    public void agregarVenta(Venta venta) {
        List<Venta> ventas = cargarVentas();
        ventas.add(venta);
        guardarVentas(ventas);
    }

    public void actualizarVenta(Venta ventaActualizada) {
        List<Venta> ventas = cargarVentas();
        boolean encontrado = false;
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getId().equals(ventaActualizada.getId())) {
                ventas.set(i, ventaActualizada);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            guardarVentas(ventas);
        } else {
            System.err.println("Venta con ID " + ventaActualizada.getId() + " no encontrada para actualizar.");
        }
    }
}
