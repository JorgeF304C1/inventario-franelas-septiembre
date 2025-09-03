package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ArchiveUtil {
    private final String baseDir;

    public ArchiveUtil(String router) {
        if (router == null || router.trim().isEmpty()) throw new IllegalArgumentException("router");
        this.baseDir = router;
        ensureDirectory(this.baseDir);
    }

    public static void ensureDirectory(String dir) {
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear directorio: " + dir, e);
        }
    }

    public BufferedWriter openWriter(String filename, boolean append) {
        try {
            ensureDirectory(baseDir);
            return new BufferedWriter(new FileWriter(baseDir + "/" + filename, append));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BufferedReader openReader(String filename) {
        try {
            return new BufferedReader(new FileReader(baseDir + "/" + filename));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** nombre_fechaActual:TiempoActual_Serial.txt */
    public static String safeName(String base, String serial) {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HHmmss"));
        return base + "_" + stamp + "_" + serial + ".txt";
    }

    /** serialesArchivos_fechaActual_serialUnico.txt */
    public static String serialesName(String serial) {
        String stamp = LocalDate.now().toString();
        return "serialesArchivos_" + stamp + "_" + serial + ".txt";
    }
}
