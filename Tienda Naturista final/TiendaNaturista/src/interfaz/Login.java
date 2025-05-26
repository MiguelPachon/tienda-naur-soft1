package interfaz;

import modelo.Usuario; 
import servicio.GestorAutenticacion;
import interfaz.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private GestorAutenticacion gestorAutenticacion;

    public Login() {
        super("Iniciar Sesión - Tienda Naturista");
        gestorAutenticacion = new GestorAutenticacion();
        initComponents();
        setupLayout();
        addListeners();
        UIUtils.setupFrame(this, "Iniciar Sesión - Tienda Naturista");
        pack();
        setLocationRelativeTo(null); 
    }

    private void initComponents() {
        userField = new JTextField(20);
        passField = new JPasswordField(20);
        loginButton = new JButton("Ingresar");
        UIUtils.applyButtonStyle(loginButton);
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Bienvenido a Tienda Naturista");
        titleLabel.setFont(UIUtils.FONT_TITULO);
        titleLabel.setForeground(UIUtils.COLOR_PRIMARIO);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Inicia sesión para continuar");
        subtitleLabel.setFont(UIUtils.FONT_SUBTITULO);
        subtitleLabel.setForeground(UIUtils.COLOR_TEXTO_PRINCIPAL);
        gbc.gridy = 1;
        panel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(UIUtils.FONT_LABEL);
        userLabel.setForeground(UIUtils.COLOR_TEXTO_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        panel.add(userField, gbc);

        // Contraseña
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(UIUtils.FONT_LABEL);
        passLabel.setForeground(UIUtils.COLOR_TEXTO_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        panel.add(passField, gbc);

        // Botón
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Para que el botón no se estire
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        add(panel);
    }

    private void addListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (gestorAutenticacion.autenticar(username, password)) {
                    Usuario usuario = gestorAutenticacion.getUsuarioLogueado(); // Obtener Usuario
                    UIUtils.showInfoMessage(Login.this, "Bienvenido, " + usuario.getNombreUsuario() + "!", "Inicio de Sesión Exitoso");
                    new Main(usuario, gestorAutenticacion).setVisible(true); // Pasar Usuario
                    dispose(); // Cierra la ventana de login
                } else {
                    UIUtils.showErrorMessage(Login.this, "Usuario o contraseña incorrectos.", "Error de Autenticación");
                }
            }
        });
    }
}
