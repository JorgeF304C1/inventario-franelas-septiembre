package arr.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class validate {

    // --- Sistema de Archivos y Errores ---

    public static void addError(String errorMessage) {
        try {
            String logFilePath = Paths.get("").toRealPath().toString() + "/src/arr/ErrorSystem.log";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedError = "[" + timestamp + "] " + errorMessage;
            useArchive(formattedError, logFilePath, true, false);
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not write to log file: " + e.getMessage());
        }
    }

    public static void useArchive(String content, String route, boolean addNewLine, boolean logErrors) {
        if (route == null || route.trim().isEmpty()) {
            String errorMsg = "File Error: File path is null, cannot save.";
            System.out.println(errorMsg);
            if(logErrors) addError(errorMsg);
            return;
        }
        try (BufferedWriter addArchive = new BufferedWriter(new FileWriter(route, true))) {
            addArchive.write(content);
            if (addNewLine) {
                addArchive.newLine();
            }
        } catch (IOException e) {
            String errorMsg = "Write Error: Could not write to '" + route + "'. Cause: " + e.getMessage();
            System.out.println(errorMsg);
            if(logErrors) addError(errorMsg);
        }
    }

    public static void useArchive(String content, String route, boolean addNewLine) {
        useArchive(content, route, addNewLine, true);
    }

    public static String utilDirectory(String router) {
        File directories = new File(router);
        if (!directories.exists()) {
            if (!directories.mkdirs()) {
                System.out.println("Error: Could not create directory. Report will not be saved.");
                return null;
            }
        }
        return router;
    }

    public static String nameArchiveGenerate(String prefix) {
        LocalDateTime actualDateTime = LocalDateTime.now();
        int rand = (int) (Math.random() * 100) + 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDate = actualDateTime.format(formatter);
        return prefix + "_" + formattedDate + "_Serial" + rand;
    }

    // --- Sistema de Validación Recursiva ---

    public static int valInt(String text, Scanner scanner) {
        System.out.println(text);
        try {
            int size = scanner.nextInt();
            scanner.nextLine();
            if (size >= 0) {
                return size;
            } else {
                System.out.println("Error: No se permiten valores negativos.");
                return valInt(text, scanner);
            }
        } catch (InputMismatchException e) {
            addError("Invalid Input: User entered non-integer value.");
            System.out.println("Error: Debes ingresar un número entero.");
            scanner.nextLine();
            return valInt(text, scanner);
        }
    }

    public static int valInt(String text, int maxValue, Scanner scanner) {
        System.out.println(text);
        try {
            int size = scanner.nextInt();
            scanner.nextLine(); 
            if (size >= 0 && size <= maxValue) {
                return size;
            } else {
                System.out.println("- Error: El valor debe estar entre 0 y " + maxValue + ".");
                return valInt(text, maxValue, scanner);
            }
        } catch (InputMismatchException e) {
            addError("Invalid Input: User entered non-integer value for a ranged input.");
            System.out.println("Error: Debes ingresar un número entero.");
            scanner.nextLine();
            return valInt(text, maxValue, scanner);
        }
    }

    public static String valName(String text, Scanner scanner) {
        System.out.println(text);
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("- Error: El nombre no puede estar vacío.");
            return valName(text, scanner);
        } else if (name.length() > 35) { // Aumentado el límite para nombres de archivo
            System.out.println("- Error: El nombre no puede exceder los 35 caracteres.");
            return valName(text, scanner);
        // *** ESTA ES LA LÍNEA CORREGIDA ***
        } else if (!name.matches("[a-zA-Z0-9 ._]+")) { // Se añadió el guion bajo "_" a los caracteres permitidos
            System.out.println("- Error: El nombre solo puede contener letras, números, espacios, puntos y guiones bajos.");
            return valName(text, scanner);
        } else {
            return name.trim();
        }
    }
}
