package interfaz;

import enumeraciones.MetodoPago;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Usuario; // Importar Usuario
import servicio.GestorInventario;
import servicio.GestorVentas;
import interfaz.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PanelVentas extends JPanel {

    private GestorVentas gestorVentas;
    private GestorInventario gestorInventario;
    private DefaultTableModel carritoTableModel;
    private JTable tablaCarrito;
    private JTextField txtProductoId, txtCantidadProducto;
    private JLabel lblTotalVenta;
    private JComboBox<MetodoPago> cmbMetodoPago;
    private List<DetalleVenta> carritoActual;
    private Usuario usuarioLogueado; 

 
    private JButton btnAgregarACarrito;
    private JButton btnRegistrarVenta;
    private JButton btnLimpiarCarrito;

    public PanelVentas(GestorVentas gestorVentas, GestorInventario gestorInventario, Usuario usuario) {
        this.gestorVentas = gestorVentas;
        this.gestorInventario = gestorInventario;
        this.carritoActual = new ArrayList<>();
        this.usuarioLogueado = usuario;

        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        setupLayout();
        addListeners();
        actualizarTotalVenta();
    }

    private void initComponents() {
      
        String[] columnNames = {"ID Producto", "Nombre", "Cantidad", "Precio Unitario", "Subtotal"};
        carritoTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCarrito = new JTable(carritoTableModel);
        tablaCarrito.setFont(UIUtils.FONT_LABEL);
        tablaCarrito.getTableHeader().setFont(UIUtils.FONT_BUTTON);
        tablaCarrito.setRowHeight(25);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

       
        txtProductoId = new JTextField(10);
        txtCantidadProducto = new JTextField(5);
        
       
        btnAgregarACarrito = new JButton("Agregar al Carrito");
        UIUtils.applyButtonStyle(btnAgregarACarrito);

    
        lblTotalVenta = new JLabel("Total de Venta: $0.00");
        lblTotalVenta.setFont(UIUtils.FONT_SUBTITULO);
        lblTotalVenta.setForeground(UIUtils.COLOR_TEXTO_PRINCIPAL);

        cmbMetodoPago = new JComboBox<>(MetodoPago.values());
        cmbMetodoPago.setSelectedIndex(-1);

  
        btnRegistrarVenta = new JButton("Registrar Venta");
        UIUtils.applyButtonStyle(btnRegistrarVenta);
        btnRegistrarVenta.setBackground(UIUtils.COLOR_EXITO);

        btnLimpiarCarrito = new JButton("Limpiar Carrito");
        UIUtils.applyButtonStyle(btnLimpiarCarrito);
        btnLimpiarCarrito.setBackground(UIUtils.COLOR_ERROR);
    }

    private void setupLayout() {
        // Panel superior para agregar productos
        JPanel addProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addProductPanel.setBackground(UIUtils.COLOR_FONDO);
        addProductPanel.setBorder(BorderFactory.createTitledBorder("Añadir Producto a Venta"));
        addProductPanel.add(new JLabel("ID Producto:"));
        addProductPanel.add(txtProductoId);
        addProductPanel.add(new JLabel("Cantidad:"));
        addProductPanel.add(txtCantidadProducto);
        addProductPanel.add(btnAgregarACarrito); 

        // Panel del carrito
        JScrollPane scrollPaneCarrito = new JScrollPane(tablaCarrito);
        scrollPaneCarrito.setBorder(BorderFactory.createTitledBorder("Productos en Carrito"));

        // Panel inferior para resumen y acciones de venta
        JPanel checkoutPanel = new JPanel(new GridBagLayout());
        checkoutPanel.setBackground(UIUtils.COLOR_FONDO);
        checkoutPanel.setBorder(BorderFactory.createTitledBorder("Finalizar Venta"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        checkoutPanel.add(lblTotalVenta, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        checkoutPanel.add(new JLabel("Método de Pago:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        checkoutPanel.add(cmbMetodoPago, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionButtonsPanel.setBackground(UIUtils.COLOR_FONDO);
        actionButtonsPanel.add(btnRegistrarVenta); 
        actionButtonsPanel.add(btnLimpiarCarrito); 
        checkoutPanel.add(actionButtonsPanel, gbc);

        
        add(addProductPanel, BorderLayout.NORTH);
        add(scrollPaneCarrito, BorderLayout.CENTER);
        add(checkoutPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
     
        btnAgregarACarrito.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProductoACarrito();
            }
        });

        btnRegistrarVenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarVenta();
            }
        });

        btnLimpiarCarrito.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCarrito();
            }
        });
    }

    private void agregarProductoACarrito() {
        String idProducto = txtProductoId.getText().trim();
        String cantidadStr = txtCantidadProducto.getText().trim();

        if (idProducto.isEmpty() || cantidadStr.isEmpty()) {
            UIUtils.showErrorMessage(this, "ID de Producto y Cantidad no pueden estar vacíos.", "Error de Entrada");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                UIUtils.showErrorMessage(this, "La cantidad debe ser un número positivo.", "Error de Cantidad");
                return;
            }

            Optional<Producto> productoOpt = gestorInventario.obtenerProductoPorId(idProducto);
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                if (producto.getStock() >= cantidad) {
                    // Verificar si el producto ya está en el carrito
                    Optional<DetalleVenta> existingDetail = carritoActual.stream()
                                                                         .filter(d -> d.getProductoId().equals(idProducto))
                                                                         .findFirst();
                    if (existingDetail.isPresent()) {
                        // Si ya está, actualiza la cantidad
                        DetalleVenta detail = existingDetail.get();
                        detail.setCantidad(detail.getCantidad() + cantidad);
                    } else {
                        // Si no, añade uno nuevo
                        carritoActual.add(new DetalleVenta(producto.getId(), producto.getNombre(), cantidad, producto.getPrecio()));
                    }
                    
                    actualizarTablaCarrito();
                    actualizarTotalVenta();
                    txtProductoId.setText("");
                    txtCantidadProducto.setText("");
                } else {
                    UIUtils.showErrorMessage(this, "Stock insuficiente para " + producto.getNombre() + ". Disponible: " + producto.getStock(), "Stock Insuficiente");
                }
            } else {
                UIUtils.showErrorMessage(this, "Producto con ID " + idProducto + " no encontrado.", "Producto no encontrado");
            }
        } catch (NumberFormatException ex) {
            UIUtils.showErrorMessage(this, "La cantidad debe ser un número válido.", "Error de Formato");
        }
    }

    private void actualizarTablaCarrito() {
        carritoTableModel.setRowCount(0); // Limpiar tabla
        for (DetalleVenta detalle : carritoActual) {
            Object[] row = {
                detalle.getProductoId(),
                detalle.getNombreProducto(),
                detalle.getCantidad(),
                String.format("%.2f", detalle.getPrecioUnitario()),
                String.format("%.2f", detalle.getTotalDetalle())
            };
            carritoTableModel.addRow(row);
        }
    }

    private void actualizarTotalVenta() {
        double total = carritoActual.stream()
                                    .mapToDouble(DetalleVenta::getTotalDetalle)
                                    .sum();
        lblTotalVenta.setText("Total de Venta: $" + String.format("%.2f", total));
    }

    private void registrarVenta() {
        if (carritoActual.isEmpty()) {
            UIUtils.showErrorMessage(this, "El carrito está vacío. Agregue productos para registrar una venta.", "Carrito Vacío");
            return;
        }

        MetodoPago metodoPago = (MetodoPago) cmbMetodoPago.getSelectedItem();
        if (metodoPago == null) {
            UIUtils.showErrorMessage(this, "Seleccione un método de pago.", "Método de Pago Requerido");
            return;
        }
        
        String empleadoId = usuarioLogueado != null ? usuarioLogueado.getId() : "DESCONOCIDO";

      
        if (gestorVentas.registrarNuevaVenta(carritoActual, metodoPago, empleadoId)) {
            UIUtils.showInfoMessage(this, "Venta registrada exitosamente.", "Venta Exitosa");
            limpiarCarrito();
            Component parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof Main) {
                JTabbedPane tabbedPane = ((Main) parent).getTabbedPane();

                if (tabbedPane.getComponentCount() > 0 && tabbedPane.getComponentAt(0) instanceof PanelProductos) {
                    ((PanelProductos) tabbedPane.getComponentAt(0)).cargarProductosEnTabla();
                }
            }
        } else {
            UIUtils.showErrorMessage(this, "Error al registrar la venta. Verifique stock o ID de productos.", "Error de Venta");
        }
    }

    private void limpiarCarrito() {
        carritoActual.clear();
        actualizarTablaCarrito();
        actualizarTotalVenta();
        cmbMetodoPago.setSelectedIndex(-1);
    }
}