package arr.consult;

import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedWriter;

import arr.helpers.validate;
import arr.ds.Queue;
import arr.ds.Stack;
import arr.domain.InventoryItem;
import arr.io.ArchiveUtil;

public class DataConsultant {

    private String[] leaguesName;
    private String[][] teamsName;
    private String[][][] leaguesTeamsPlayers;
    private int[][][] availability;
    private Scanner scanner;

    // ========= MENÚ DE CONSULTAS EN MEMORIA =========
    public void runConsultMenu(String[] leagues, String[][] teams, String[][][] players, int[][][] avail, Scanner sc) throws IOException {
        this.leaguesName = leagues;
        this.teamsName = teams;
        this.leaguesTeamsPlayers = players;
        this.availability = avail;
        this.scanner = sc;

        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("\n--- Consulta de Inventario (en memoria) ---");
            System.out.println("1. Ver detalles de una Liga");
            System.out.println("2. Ver stock total de un Equipo");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            int choice = validate.valInt("", scanner);

            switch (choice) {
                case 1:
                    consultLeagueDetails();
                    break;
                case 2:
                    consultTeamStock();
                    break;
                case 0:
                    exitMenu = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void consultLeagueDetails() throws IOException {
        String leagueToFind = validate.valName("Ingrese el nombre de la liga:", this.scanner);
        String reportContent = findLeagueDetailsRecursive(leagueToFind, 0);

        if (!"Liga no encontrada.".equals(reportContent)) {
            System.out.println("\n--- Reporte: " + leagueToFind + " ---");
            System.out.println(reportContent);
            saveQueryReport("query_report_liga_" + leagueToFind.replace(" ", "_"), reportContent);
        } else {
            System.out.println("No se encontró la liga '" + leagueToFind + "'.");
        }
    }

    private void consultTeamStock() throws IOException {
        String leagueToFind = validate.valName("Liga:", this.scanner);
        String teamToFind = validate.valName("Equipo:", this.scanner);
        int stock = findTeamStockRecursive(leagueToFind, teamToFind, 0, 0);

        if (stock != -1) {
            String reportContent = String.format("Stock total del equipo '%s' (liga '%s'): %d", teamToFind, leagueToFind, stock);
            System.out.println(reportContent);
            saveQueryReport("query_report_equipo_" + teamToFind.replace(" ", "_"), reportContent);
        } else {
            System.out.printf("No se encontró el equipo '%s' en la liga '%s'.\n", teamToFind, leagueToFind);
        }
    }

    private void saveQueryReport(String reportPrefix, String content) throws IOException {
        String storageDirectory = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        validate.utilDirectory(storageDirectory);
        String reportName = validate.nameArchiveGenerate(reportPrefix);
        String fullPath = storageDirectory + "/" + reportName + ".txt";
        validate.useArchive(content, fullPath, false);
        System.out.println("-> Reporte guardado en: " + fullPath);
    }

    // ========= BÚSQUEDA EN ARCHIVOS + PIPELINE (Cola→Pila→Archivo) =========
    public void runFileSearchQueueStack(Scanner sc) throws IOException {
    this.scanner = sc;
    System.out.println("\n--- Buscador por archivos (Archivos → Cola→Pila→Archivo) ---");

    // 0) Ámbito
    String projectRoot = Paths.get("").toRealPath().toString();
    String srcPath = projectRoot + "/src";
    String storagePath = projectRoot + "/src/arr/storage";
    String reportsPath = projectRoot + "/src/arr/reports";

    System.out.println("Seleccione el ámbito de búsqueda:");
    System.out.println("1) src (código fuente)");
    System.out.println("2) storage (inventarios .dat y salidas intermedias)");
    System.out.println("3) reports (reportes finales)");
    System.out.println("4) Ruta personalizada");
    System.out.print("Opción: ");
    int scope = validate.valInt("", this.scanner);

    String basePath;
    switch (scope) {
        case 2: basePath = storagePath; break;
        case 3: basePath = reportsPath; break;
        case 4: basePath = validate.valName("Indique ruta absoluta o relativa:", this.scanner); break;
        case 1:
        default: basePath = srcPath; break;
    }

    String needle = validate.valName("Patrón a buscar (en nombre de archivo y en líneas): ", this.scanner);
    System.out.print("Filtrar por extensión? (ej: .txt / .dat / .java) o Enter para ninguno: ");
    String ext = this.scanner.nextLine();
    boolean filterByExt = (ext != null && ext.trim().length() > 0);
    if (filterByExt) ext = ext.trim().toLowerCase();

    // 2) ENCOLAR resultados
    Queue<InventoryItem> outQueue = new Queue<InventoryItem>();
    Queue<String> fileHits = new Queue<String>();

    // Recorrido de archivos (NO final)
    findFilesNonTail(new File(basePath), needle, fileHits, filterByExt ? ext : null);

    // Contadores para mensajes claros
    int fileHitCount = 0;
    int lineHitCount = 0;

    // Por cada archivo candidato: encola FILE_HIT y busca líneas (FINAL, case-insensitive)
    while (!fileHits.isEmpty()) {
        String path = fileHits.dequeue();

        // el archivo califica; encolamos FILE_HIT siempre
        outQueue.enqueue(new InventoryItem(InventoryItem.Kind.FILE_HIT, path));
        fileHitCount++;

        // buscar líneas dentro del archivo
        List<String> lines = readAllLines(path);
        Queue<Integer> lineHits = new Queue<Integer>();
        findLinesTailCI(lines, needle, 0, lineHits);

        while (!lineHits.isEmpty()) {
            int line = lineHits.dequeue();
            outQueue.enqueue(new InventoryItem(
                InventoryItem.Kind.LINE_HIT,
                path + " : " + (line + 1) + " -> " + lines.get(line)
            ));
            lineHitCount++;
        }
    }

    // Mensajes cuando no hay nada que mostrar
    if (fileHitCount == 0 && lineHitCount == 0) {
        System.out.println("\nNo se encontraron archivos candidatos en " + basePath +
            " para: \"" + needle + "\"" + (filterByExt ? (" con extensión " + ext) : "") + ".");
        return;
    }
    if (fileHitCount > 0 && lineHitCount == 0) {
        System.out.println("\nSe encontraron " + fileHitCount + " archivo(s) candidato(s), pero ");
        System.out.println("**No hubo coincidencias en el texto** dentro de esos archivos para: \"" + needle + "\".");
        // Aun así, mostramos/guardamos los FILE_HIT para que quede rastro del ámbito revisado
    }

    // 3) DESENCOLAR / mostrar + APILAR
    System.out.println("\n--- Resultados (desencolados) ---");
    Stack<InventoryItem> stack = new Stack<InventoryItem>();
    while (!outQueue.isEmpty()) {
        InventoryItem item = outQueue.dequeue();
        System.out.println(item);
        stack.push(item);
    }

    // 4) DESAPILAR / guardar
    ArchiveUtil.ensureDirectory(storagePath);
    ArchiveUtil au = new ArchiveUtil(storagePath);
    String outName = ArchiveUtil.serialesName(java.util.UUID.randomUUID().toString().substring(0, 8));

    BufferedWriter w = null;
    try {
        w = au.openWriter(outName, false);
        while (!stack.isEmpty()) {
            w.write(stack.pop().toString());
            w.newLine();
        }
    } finally {
        if (w != null) try { w.close(); } catch (IOException e) { /* ignore */ }
    }
    System.out.println("-> Guardado en: " + storagePath + "/" + outName);
}

    // ========= Recursión sobre archivos =========
    // NO FINAL: directorios (ahora con filtro de extensión opcional)
    private void findFilesNonTail(File dir, String needle, Queue<String> results, String extensionOrNull) throws IOException {
        if (dir == null || !dir.exists()) return;
        File[] entries = dir.listFiles();
        if (entries == null) return;

        String nlow = needle.toLowerCase();
        for (int i = 0; i < entries.length; i++) {
            File f = entries[i];
            if (f.isDirectory()) {
                findFilesNonTail(f, needle, results, extensionOrNull);
            } else {
                String name = f.getName();
                String nameLow = name.toLowerCase();
                if (nameLow.contains(nlow)) {
                    if (extensionOrNull == null || nameLow.endsWith(extensionOrNull)) {
                        results.enqueue(f.getAbsolutePath());
                    }
                } else {
                    // Si no coincide por nombre, aún puede pasar el filtro por extensión (para buscar solo en cierto tipo)
                    if (extensionOrNull != null && nameLow.endsWith(extensionOrNull)) {
                        results.enqueue(f.getAbsolutePath()); // Permite revisar líneas aunque no matchee el nombre
                    }
                }
            }
        }
    }

    // FINAL (tail) case-insensitive en líneas
    private void findLinesTailCI(List<String> lines, String needle, int i, Queue<Integer> results) {
        if (i >= lines.size()) return;
        String line = lines.get(i);
        if (line != null) {
            String l = line.toLowerCase();
            String n = needle.toLowerCase();
            if (l.contains(n)) results.enqueue(i);
        }
        findLinesTailCI(lines, needle, i + 1, results);
    }

    private List<String> readAllLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }

    // ========= Recursión en memoria =========
    private String findLeagueDetailsRecursive(String leagueToFind, int i) {
        if (leaguesName == null || leaguesName.length == 0) return "No hay datos cargados.";
        if (i >= leaguesName.length) return "Liga no encontrada.";

        if (leaguesName[i] != null && leaguesName[i].equalsIgnoreCase(leagueToFind)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Liga: ").append(leaguesName[i]).append("\n");
            if (teamsName[i] != null) {
                for (int j = 0; j < teamsName[i].length; j++) {
                    String team = teamsName[i][j];
                    if (team == null || team.isEmpty()) continue;
                    sb.append("\tEquipo: ").append(team).append("\n");
                    if (leaguesTeamsPlayers[i][j] != null) {
                        for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                            String player = leaguesTeamsPlayers[i][j][k];
                            int stock = availability[i][j][k];
                            sb.append("\t\tJugador: ").append(player).append(" | Stock: ").append(stock).append("\n");
                        }
                    }
                }
            }
            return sb.toString();
        }
        return findLeagueDetailsRecursive(leagueToFind, i + 1);
    }

    private int findTeamStockRecursive(String leagueToFind, String teamToFind, int i, int j) {
        if (leaguesName == null || leaguesName.length == 0) return -1;
        if (i >= leaguesName.length) return -1;

        if (leaguesName[i] != null && leaguesName[i].equalsIgnoreCase(leagueToFind)) {
            if (j >= teamsName[i].length) return -1;
            if (teamsName[i][j] != null && teamsName[i][j].equalsIgnoreCase(teamToFind)) {
                int total = 0;
                if (availability[i][j] != null) {
                    for (int k = 0; k < availability[i][j].length; k++) total += availability[i][j][k];
                }
                return total;
            }
            return findTeamStockRecursive(leagueToFind, teamToFind, i, j + 1);
        }
        return findTeamStockRecursive(leagueToFind, teamToFind, i + 1, 0);
    }
}
