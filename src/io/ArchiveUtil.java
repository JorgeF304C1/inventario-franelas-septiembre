package arr.io;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ArchiveUtil {
    private final Path baseDir;

    public ArchiveUtil(String router) {
        if (router == null || router.isBlank()) throw new IllegalArgumentException("router");
        this.baseDir = Path.of(router);
        ensureDirectory(this.baseDir.toString());
    }

    public static void ensureDirectory(String dir) {
        try {
            Files.createDirectories(Path.of(dir));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear directorio: " + dir, e);
        }
    }

    public BufferedWriter openWriter(String filename, boolean append) {
        try {
            Path p = baseDir.resolve(filename);
            ensureDirectory(baseDir.toString());
            return new BufferedWriter(new FileWriter(p.toFile(), append));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BufferedReader openReader(String filename) {
        try {
            Path p = baseDir.resolve(filename);
            return new BufferedReader(new FileReader(p.toFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static String safeName(String base, String serial) {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HHmmss"));
        return base + "_" + stamp + "_" + serial + ".txt";
    }


    public static String serialesName(String serial) {
        String stamp = LocalDate.now().toString();
        return "serialesArchivos_" + stamp + "_" + serial + ".txt";
    }
}
