package interfaz;

import enumeraciones.TipoProducto;
import modelo.Producto;
import servicio.GestorInventario;
import interfaz.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class PanelProductos extends JPanel {

    private GestorInventario gestorInventario;
    private JTable tablaProductos;
    private DefaultTableModel tableModel;

    
    private JTextField txtId, txtNombre, txtPrecio, txtStock;
    private JComboBox<TipoProducto> cmbTipo;
    private JTextField txtDetalle1, txtDetalle2; // Campos para detalles adicionales
    private JLabel lblDetalle1, lblDetalle2;

    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar;

    public PanelProductos(GestorInventario gestorInventario) {
        this.gestorInventario = gestorInventario;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        setupTable();
        setupForm();
        addListeners();
        cargarProductosEnTabla();
    }

    private void initComponents() {
    
        String[] columnNames = {"ID", "Nombre", "Precio", "Stock", "Tipo", "Detalle Adic. 1", "Detalle Adic. 2"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaProductos = new JTable(tableModel);
        tablaProductos.setFont(UIUtils.FONT_LABEL);
        tablaProductos.getTableHeader().setFont(UIUtils.FONT_BUTTON);
        tablaProductos.setRowHeight(25);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setAutoCreateRowSorter(true);
    }

    private void setupTable() {
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_FONDO);
		formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.COLOR_SECUNDARIO),
            "Gestión de Productos",
            WHEN_FOCUSED,
            WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            UIUtils.FONT_SUBTITULO,
            UIUtils.COLOR_PRIMARIO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: ID
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID Producto:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; txtId = new JTextField(15); formPanel.add(txtId, gbc);

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Nombre:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; txtNombre = new JTextField(15); formPanel.add(txtNombre, gbc);

        // Fila 2: Precio
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Precio:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; txtPrecio = new JTextField(15); formPanel.add(txtPrecio, gbc);

        // Fila 3: Stock
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Stock:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; txtStock = new JTextField(15); formPanel.add(txtStock, gbc);

        // Fila 4: Tipo de Producto (Enum)
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Tipo:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(TipoProducto.values());
        cmbTipo.setSelectedIndex(-1); // No seleccionar nada por defecto
        cmbTipo.addActionListener(e -> actualizarLabelsDetalleAdicional());
        formPanel.add(cmbTipo, gbc);

        // Fila 5: Detalle Adicional 1
        gbc.gridx = 0; gbc.gridy = 5; lblDetalle1 = new JLabel("Detalle 1:", SwingConstants.RIGHT); formPanel.add(lblDetalle1, gbc);
        gbc.gridx = 1; txtDetalle1 = new JTextField(15); formPanel.add(txtDetalle1, gbc);

        // Fila 6: Detalle Adicional 2
        gbc.gridx = 0; gbc.gridy = 6; lblDetalle2 = new JLabel("Detalle 2:", SwingConstants.RIGHT); formPanel.add(lblDetalle2, gbc);
        gbc.gridx = 1; txtDetalle2 = new JTextField(15); formPanel.add(txtDetalle2, gbc);

        actualizarLabelsDetalleAdicional(); 

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(UIUtils.COLOR_FONDO);
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        UIUtils.applyButtonStyle(btnAgregar);
        UIUtils.applyButtonStyle(btnActualizar);
        UIUtils.applyButtonStyle(btnEliminar);
        UIUtils.applyButtonStyle(btnLimpiar);

        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);

        
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void actualizarLabelsDetalleAdicional() {
        TipoProducto selectedTipo = (TipoProducto) cmbTipo.getSelectedItem();
        
        if (selectedTipo == TipoProducto.ALIMENTICIO) {
            lblDetalle1.setText("Fecha Caducidad:");
            lblDetalle2.setText("Info. Nutricional:");
            txtDetalle1.setVisible(true);
            txtDetalle2.setVisible(true);
        } else if (selectedTipo == TipoProducto.COSMETICO) {
            lblDetalle1.setText("Tipo Piel:");
            lblDetalle2.setText("Ingredientes:");
            txtDetalle1.setVisible(true);
            txtDetalle2.setVisible(true);
        } else if (selectedTipo == TipoProducto.MEDICINAL) { // Ejemplo para un nuevo tipo
            lblDetalle1.setText("Uso Terapéutico:");
            lblDetalle2.setText("Dosis Recomendada:");
            txtDetalle1.setVisible(true);
            txtDetalle2.setVisible(true);
        } else { 
            lblDetalle1.setText("Detalle Adic. 1:");
            lblDetalle2.setText("Detalle Adic. 2:");
            txtDetalle1.setVisible(true); // Mantener visible por defecto para "OTRO"
            txtDetalle2.setVisible(true);
        }
        revalidate();
        repaint();
    }

    private void addListeners() {
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarProducto();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                llenarFormularioDesdeTabla();
            }
        });
    }

    public void cargarProductosEnTabla() { 
        tableModel.setRowCount(0);
        List<Producto> productos = gestorInventario.obtenerTodosLosProductos();
        for (Producto p : productos) {
            Object[] row = new Object[7];
            row[0] = p.getId();
            row[1] = p.getNombre();
            row[2] = String.format("%.2f", p.getPrecio());
            row[3] = p.getStock();
            row[4] = p.getTipo();
            row[5] = p.getDetalleAdicional1();
            row[6] = p.getDetalleAdicional2();
            tableModel.addRow(row);
            tablaProductos.requestFocusInWindow();
        }
    }

    private void agregarProducto() {
        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            TipoProducto tipo = (TipoProducto) cmbTipo.getSelectedItem();
            String detalle1 = txtDetalle1.getText().trim();
            String detalle2 = txtDetalle2.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || tipo == null) {
                UIUtils.showErrorMessage(this, "ID, Nombre y Tipo de Producto son obligatorios.", "Error de Entrada");
                return;
            }
            if (gestorInventario.obtenerProductoPorId(id).isPresent()) {
                UIUtils.showErrorMessage(this, "Ya existe un producto con este ID.", "ID Duplicado");
                return;
            }

            Producto nuevoProducto = new Producto(id, nombre, precio, stock, tipo, detalle1, detalle2);
            gestorInventario.agregarProducto(nuevoProducto);
            cargarProductosEnTabla();
            limpiarFormulario();
            UIUtils.showInfoMessage(this, "Producto agregado exitosamente.", "Éxito");

        } catch (NumberFormatException ex) {
            UIUtils.showErrorMessage(this, "Precio y Stock deben ser números válidos.", "Error de Formato");
        } catch (Exception ex) {
            UIUtils.showErrorMessage(this, "Error al agregar producto: " + ex.getMessage(), "Error");
            ex.printStackTrace();
        }
    }

    private void actualizarProducto() {
        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            TipoProducto tipo = (TipoProducto) cmbTipo.getSelectedItem();
            String detalle1 = txtDetalle1.getText().trim();
            String detalle2 = txtDetalle2.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || tipo == null) {
                UIUtils.showErrorMessage(this, "ID, Nombre y Tipo de Producto son obligatorios.", "Error de Entrada");
                return;
            }
            if (!gestorInventario.obtenerProductoPorId(id).isPresent()) {
                UIUtils.showErrorMessage(this, "No existe un producto con este ID para actualizar.", "Producto no encontrado");
                return;
            }

            Producto productoActualizado = new Producto(id, nombre, precio, stock, tipo, detalle1, detalle2);
            gestorInventario.actualizarProducto(productoActualizado);
            cargarProductosEnTabla();
            limpiarFormulario();
            UIUtils.showInfoMessage(this, "Producto actualizado exitosamente.", "Éxito");

        } catch (NumberFormatException ex) {
            UIUtils.showErrorMessage(this, "Precio y Stock deben ser números válidos.", "Error de Formato");
        } catch (Exception ex) {
            UIUtils.showErrorMessage(this, "Error al actualizar producto: " + ex.getMessage(), "Error");
            ex.printStackTrace();
        }
    }

    private void eliminarProducto() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorMessage(this, "Seleccione un producto de la tabla para eliminar.", "Ningún Producto Seleccionado");
            return;
        }

        String idProducto = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto con ID: " + idProducto + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            gestorInventario.eliminarProducto(idProducto);
            cargarProductosEnTabla();
            limpiarFormulario();
            UIUtils.showInfoMessage(this, "Producto eliminado exitosamente.", "Éxito");
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cmbTipo.setSelectedIndex(-1); 
        txtDetalle1.setText("");
        txtDetalle2.setText("");
        actualizarLabelsDetalleAdicional(); 
        tablaProductos.clearSelection(); 
    }

    private void llenarFormularioDesdeTabla() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow != -1) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtNombre.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtPrecio.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtStock.setText(tableModel.getValueAt(selectedRow, 3).toString());
            
            TipoProducto tipo = TipoProducto.valueOf(tableModel.getValueAt(selectedRow, 4).toString());
            cmbTipo.setSelectedItem(tipo);
            
            txtDetalle1.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
            txtDetalle2.setText(tableModel.getValueAt(selectedRow, 6) != null ? tableModel.getValueAt(selectedRow, 6).toString() : "");
        }
    }
    
    public JTable getTabla() {
        return tablaProductos;
    }
}

