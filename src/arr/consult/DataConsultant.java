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
        System.out.println("\n--- Buscador (Archivos → Cola→Pila→Archivo) ---");

        Queue<InventoryItem> queue = new Queue<InventoryItem>();
        boolean more = true;
        while (more) {
            String needle = validate.valName("Patrón a buscar (en nombres de archivos y líneas): ", this.scanner);

            // a) Recorrer archivos (recursión NO final) bajo ./src
            String basePath = Paths.get("").toRealPath().toString() + "/src";
            Queue<String> fileHits = new Queue<String>();
            findFilesNonTail(new File(basePath), needle, fileHits);

            // b) Por cada archivo, recorrer líneas (recursión FINAL)
            while (!fileHits.isEmpty()) {
                String path = fileHits.dequeue();
                List<String> lines = readAllLines(path);
                Queue<Integer> lineHits = new Queue<Integer>();
                findLinesTail(lines, needle, 0, lineHits);
                while (!lineHits.isEmpty()) {
                    int line = lineHits.dequeue();
                    queue.enqueue(new InventoryItem(
                        InventoryItem.Kind.LINE_HIT,
                        path + " : " + (line + 1) + " -> " + lines.get(line)
                    ));
                }
            }

            System.out.print("¿Agregar otra búsqueda? (1=Sí / 0=No): ");
            more = validate.valInt("", this.scanner) == 1;
        }

        // 2) Desencolar/mostrar + 3) apilar
        System.out.println("\n--- Resultados (desencolados) ---");
        Stack<InventoryItem> stack = new Stack<InventoryItem>();
        while (!queue.isEmpty()) {
            InventoryItem item = queue.dequeue();
            System.out.println(item);
            stack.push(item);
        }

        // 4) Desapilar/guardar
        String storagePath = Paths.get("").toRealPath().toString() + "/src/arr/storage";
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
    // NO FINAL: directorios
    private void findFilesNonTail(File dir, String needle, Queue<String> results) throws IOException {
        if (dir == null || !dir.exists()) return;
        File[] entries = dir.listFiles();
        if (entries == null) return;

        for (int i = 0; i < entries.length; i++) {
            File f = entries[i];
            if (f.isDirectory()) {
                findFilesNonTail(f, needle, results);
            } else if (f.getName().toLowerCase().contains(needle.toLowerCase())) {
                results.enqueue(f.getAbsolutePath());
            }
        }
    }

    // FINAL (tail): líneas
    private void findLinesTail(List<String> lines, String needle, int i, Queue<Integer> results) {
        if (i >= lines.size()) return;
        if (lines.get(i).contains(needle)) results.enqueue(i);
        findLinesTail(lines, needle, i + 1, results);
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
