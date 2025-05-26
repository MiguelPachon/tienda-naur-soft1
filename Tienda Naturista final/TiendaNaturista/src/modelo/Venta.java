package modelo;

import enumeraciones.EstadoVenta;
import enumeraciones.MetodoPago;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Venta {
    private String id;
    private LocalDateTime fechaHora;
    private List<DetalleVenta> detalles;
    private double total;
    private MetodoPago metodoPago;
    private EstadoVenta estado;
    private String empleadoId; 

    public Venta(String id, LocalDateTime fechaHora, List<DetalleVenta> detalles, double total, MetodoPago metodoPago, EstadoVenta estado, String empleadoId) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.detalles = new ArrayList<>(detalles);
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.empleadoId = empleadoId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public String getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(String empleadoId) {
        this.empleadoId = empleadoId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String detallesStr = detalles.stream()
                                     .map(DetalleVenta::toString)
                                     .collect(Collectors.joining("|"));
        return id + "," + fechaHora.format(formatter) + "," + total + "," + metodoPago.name() + "," + estado.name() + "," + empleadoId + "," + detallesStr;
    }
}
