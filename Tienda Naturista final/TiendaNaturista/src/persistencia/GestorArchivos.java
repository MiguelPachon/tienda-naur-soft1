package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    private static final String DATA_DIR = "data/";

    static {

        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error al crear el directorio de datos: " + e.getMessage());
        }
    }

    public static List<String> leerArchivo(String nombreArchivo) {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_DIR + nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
            // Si el archivo no existe, simplemente retorna una lista vacía
        }
        return lineas;
    }

    public static void escribirArchivo(String nombreArchivo, List<String> lineas, boolean append) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(DATA_DIR + nombreArchivo, append)))) {
            for (String linea : lineas) {
                pw.println(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }

    public static void reescribirArchivo(String nombreArchivo, List<String> lineas) {
        escribirArchivo(nombreArchivo, lineas, false); // No append, sobrescribe
    }
}
