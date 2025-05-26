package modelo;

import enumeraciones.TipoProducto;

public class Producto {
    private String id;
    private String nombre;
    private double precio;
    private int stock;
    private TipoProducto tipo;

    private String detalleAdicional1;
    private String detalleAdicional2;

    public Producto(String id, String nombre, double precio, int stock, TipoProducto tipo, String detalleAdicional1, String detalleAdicional2) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.tipo = tipo;
        this.detalleAdicional1 = detalleAdicional1;
        this.detalleAdicional2 = detalleAdicional2;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProducto tipo) {
        this.tipo = tipo;
    }

    public String getDetalleAdicional1() {
        return detalleAdicional1;
    }

    public void setDetalleAdicional1(String detalleAdicional1) {
        this.detalleAdicional1 = detalleAdicional1;
    }

    public String getDetalleAdicional2() {
        return detalleAdicional2;
    }

    public void setDetalleAdicional2(String detalleAdicional2) {
        this.detalleAdicional2 = detalleAdicional2;
    }

    @Override
    public String toString() {

        return id + "," + nombre + "," + precio + "," + stock + "," + tipo.name() + "," +
               (detalleAdicional1 != null ? detalleAdicional1 : "") + "," +
               (detalleAdicional2 != null ? detalleAdicional2 : "");
    }
}
