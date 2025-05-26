package interfaz.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL; // Importa java.net.URL para cargar recursos

public class UIUtils {


    public static final Color COLOR_PRIMARIO = new Color(76, 175, 80); // Verde vibrante
    public static final Color COLOR_SECUNDARIO = new Color(139, 195, 74); // Verde claro
    public static final Color COLOR_FONDO = new Color(245, 245, 245); // Gris muy claro
    public static final Color COLOR_TEXTO_PRINCIPAL = new Color(33, 33, 33); // Gris oscuro
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(97, 97, 97); // Gris medio
    public static final Color COLOR_ERROR = new Color(244, 67, 54); // Rojo
    public static final Color COLOR_EXITO = new Color(76, 175, 80); // Verde


    public static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public static void applyButtonStyle(JButton button) {
        button.setBackground(COLOR_PRIMARIO);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void setupFrame(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_FONDO);
        frame.setLocationRelativeTo(null); // Centrar en la pantalla
    }

    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static String showInputDialog(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }

 
    public static int showConfirmDialog(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }


    public static ImageIcon createTabIcon(String path) {
        try {
  
            URL imgURL = UIUtils.class.getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                // Si no se encuentra como recurso, intenta cargarlo directamente desde el sistema de archivos
                System.err.println("Advertencia: No se encontró el recurso de icono en el classpath: " + path + ". Intentando cargar directamente.");
                return new ImageIcon(path);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar icono '" + path + "': " + e.getMessage());
            return null; // Retorna null si hay un error al cargar el icono
        }
    }
}