package interfaz;

import enumeraciones.Roles; 
import modelo.DetalleVenta;
import modelo.Usuario; 
import modelo.Venta;
import servicio.GestorAutenticacion;
import servicio.GestorInventario;
import servicio.GestorVentas;
import interfaz.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private Usuario usuarioLogueado; 
    private GestorAutenticacion gestorAutenticacion;
    private GestorInventario gestorInventario;
    private GestorVentas gestorVentas;

    private JTabbedPane tabbedPane;
    private JPanel panelProductos;
    private JPanel panelVentas;

    public Main(Usuario usuario, GestorAutenticacion autenticacion) { // Recibe Usuario
        this.usuarioLogueado = usuario;
        this.gestorAutenticacion = autenticacion;
        this.gestorInventario = new GestorInventario();
        this.gestorVentas = new GestorVentas(gestorInventario); // Pasa el gestorInventario

        initComponents();
        setupLayout();
        addListeners();
        UIUtils.setupFrame(this, "Tienda Naturista - Sesión de " + usuario.getNombreUsuario());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar al inicio
        
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_LABEL);

        // Paneles de contenido
        panelProductos = new PanelProductos(gestorInventario);
        panelVentas = new PanelVentas(gestorVentas, gestorInventario, usuarioLogueado); // Pasar usuarioLogueado
        
        tabbedPane.addTab("Gestión de Productos", null, panelProductos, "Administra el inventario de productos");
        tabbedPane.addTab("Registro de Ventas", null, panelVentas, "Registra nuevas ventas");
        tabbedPane.addTab("Reportes de Ventas", null, crearPanelReportes(), "Visualiza el historial de ventas");
        
        // Deshabilita Pestanas según el rol
        if (usuarioLogueado.getRol() == Roles.EMPLEADO) { // Si el empleado no es ADMINISTRADOR
            tabbedPane.setEnabledAt(0, false); // Deshabilita Gestión de Productos
            // tabbedPane.setEnabledAt(2, false); // Opcional: Deshabilitar reportes si solo es empleado
        }
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.COLOR_FONDO);
        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        add(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.COLOR_PRIMARIO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("¡Hola, " + usuarioLogueado.getNombreUsuario() + " (" + usuarioLogueado.getRol() + ")!");
        welcomeLabel.setFont(UIUtils.FONT_SUBTITULO.deriveFont(Font.BOLD, 20f));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Cerrar Sesión");
        UIUtils.applyButtonStyle(logoutButton);
        logoutButton.setBackground(UIUtils.COLOR_ERROR);
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestorAutenticacion.cerrarSesion();
                UIUtils.showInfoMessage(Main.this, "Sesión cerrada.", "Adiós");
                new Login().setVisible(true);
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(logoutButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_FONDO);
        
        JLabel title = new JLabel("Reportes de Ventas", SwingConstants.CENTER);
        title.setFont(UIUtils.FONT_TITULO);
        title.setForeground(UIUtils.COLOR_TEXTO_PRINCIPAL);
        panel.add(title, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(UIUtils.FONT_LABEL);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        
        // Cargar y mostrar las ventas existentes
        StringBuilder sb = new StringBuilder();
        sb.append("--- Historial de Ventas ---\n\n");
        if (gestorVentas.obtenerTodasLasVentas().isEmpty()) {
            sb.append("No hay ventas registradas aún.\n");
        } else {
            for (Venta venta : gestorVentas.obtenerTodasLasVentas()) {
                sb.append("ID Venta: ").append(venta.getId()).append("\n");
                sb.append("  Fecha: ").append(venta.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                sb.append("  Total: $").append(String.format("%.2f", venta.getTotal())).append("\n");
                sb.append("  Método de Pago: ").append(venta.getMetodoPago()).append("\n");
                sb.append("  Estado: ").append(venta.getEstado()).append("\n");
                sb.append("  Realizada por: ").append(venta.getEmpleadoId()).append("\n");
                sb.append("  Detalles:\n");
                for (DetalleVenta dv : venta.getDetalles()) {
                    sb.append("    - ").append(dv.getNombreProducto())
                      .append(" x ").append(dv.getCantidad())
                      .append(" ($").append(String.format("%.2f", dv.getPrecioUnitario())).append(" c/u)\n");
                }
                sb.append("----------------------------\n");
            }
        }
        reportArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botón para anular venta (solo visible para administradores)
        JButton anularVentaBtn = new JButton("Anular Venta por ID");
        UIUtils.applyButtonStyle(anularVentaBtn);
        
        if (usuarioLogueado.getRol() == Roles.ADMINISTRADOR) {
            anularVentaBtn.addActionListener(e -> {
                String ventaId = UIUtils.showInputDialog(this, "Ingrese el ID de la venta a anular:", "Anular Venta");
                if (ventaId != null && !ventaId.trim().isEmpty()) {
                    if (gestorVentas.anularVenta(ventaId.trim())) {
                        UIUtils.showInfoMessage(this, "Venta anulada y stock devuelto.", "Anulación Exitosa");
                        // Recargar reportes
                        reportArea.setText(""); // Limpiar
                        StringBuilder updatedSb = new StringBuilder();
                        updatedSb.append("--- Historial de Ventas ---\n\n");
                        for (Venta venta : gestorVentas.obtenerTodasLasVentas()) {
                            updatedSb.append("ID Venta: ").append(venta.getId()).append("\n");
                            updatedSb.append("  Fecha: ").append(venta.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                            updatedSb.append("  Total: $").append(String.format("%.2f", venta.getTotal())).append("\n");
                            updatedSb.append("  Método de Pago: ").append(venta.getMetodoPago()).append("\n");
                            updatedSb.append("  Estado: ").append(venta.getEstado()).append("\n");
                            updatedSb.append("  Realizada por: ").append(venta.getEmpleadoId()).append("\n");
                            sb.append("  Detalles:\n");
                            for (DetalleVenta dv : venta.getDetalles()) {
                                updatedSb.append("    - ").append(dv.getNombreProducto())
                                          .append(" x ").append(dv.getCantidad())
                                          .append(" ($").append(String.format("%.2f", dv.getPrecioUnitario())).append(" c/u)\n");
                            }
                            updatedSb.append("----------------------------\n");
                        }
                        reportArea.setText(updatedSb.toString());
                    } else {
                        UIUtils.showErrorMessage(this, "No se pudo anular la venta o ya estaba en estado ANULADA.", "Error al Anular Venta");
                    }
                }
            });
            JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            southPanel.setBackground(UIUtils.COLOR_FONDO);
            southPanel.add(anularVentaBtn);
            panel.add(southPanel, BorderLayout.SOUTH);
        } else {
            // Si no es administrador, no añadir el botón o hacerlo visible
            anularVentaBtn.setVisible(false);
        }

        return panel;
    }
    
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    private void addListeners() {
        
    }
    
    
}