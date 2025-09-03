package arr.consult;

import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Scanner;

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

    // ========= MENÚ DE CONSULTAS EN MEMORIA (tu lógica original) =========
    public void runConsultMenu(String[] leagues, String[][] teams, String[][][] players, int[][][] avail, Scanner sc) throws IOException {
        this.leaguesName = leagues;
        this.teamsName = teams;
        this.leaguesTeamsPlayers = players;
        this.availability = avail;
        this.scanner = sc;

        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("\n--- Módulo de Consulta de Datos ---");
            System.out.println("1. Consulta General: Ver detalles de una Liga");
            System.out.println("2. Consulta Directa: Ver stock de un Equipo específico");
            System.out.println("0. Volver al Menú Anterior");
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
        String leagueToFind = validate.valName("Ingrese el nombre de la liga para ver sus detalles:", this.scanner);
        String reportContent = findLeagueDetailsRecursive(leagueToFind, 0);

        if (!"Liga no encontrada.".equals(reportContent)) {
            System.out.println("\n--- Reporte de Consulta para la Liga: " + leagueToFind + " ---");
            System.out.println(reportContent);
            System.out.println("--- Fin del Reporte en Consola ---");
            saveQueryReport("query_report_liga_" + leagueToFind.replace(" ", "_"), reportContent);
        } else {
            System.out.println("Resultado: No se encontró una liga con el nombre '" + leagueToFind + "'.");
        }
    }

    private void consultTeamStock() throws IOException {
        String leagueToFind = validate.valName("Primero, ingrese el nombre de la liga a la que pertenece el equipo:", this.scanner);
        String teamToFind = validate.valName("Ahora, ingrese el nombre del equipo que desea consultar:", this.scanner);
        int stock = findTeamStockRecursive(leagueToFind, teamToFind, 0, 0);

        if (stock != -1) {
            String reportContent = String.format("El stock total para el equipo '%s' en la liga '%s' es de %d camisetas.", teamToFind, leagueToFind, stock);
            System.out.println("Resultado: " + reportContent);
            saveQueryReport("query_report_equipo_" + teamToFind.replace(" ", "_"), reportContent);
        } else {
            System.out.printf("Resultado: No se encontró el equipo '%s' en la liga '%s'.\n", teamToFind, leagueToFind);
        }
    }

    private void saveQueryReport(String reportPrefix, String content) throws IOException {
        String storageDirectory = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        validate.utilDirectory(storageDirectory);  // mantengo tu utilidad
        String reportName = validate.nameArchiveGenerate(reportPrefix);
        String fullPath = storageDirectory + "/" + reportName + ".txt";
        validate.useArchive(content, fullPath, false);
        System.out.println("-> Reporte guardado en: " + fullPath);
    }

    // ========= BÚSQUEDA EN ARCHIVOS + PIPELINE (cola→pila→archivo) =========
    public void runFileSearchQueueStack(Scanner sc) throws IOException {
        this.scanner = sc;
        System.out.println("\n--- Buscador con estructuras (Cola→Pila→Archivo) ---");

        // 1) ENCOLAR resultados de búsquedas
        Queue<InventoryItem> queue = new Queue<InventoryItem>();
        boolean more = true;
        while (more) {
            String needle = validate.valName("Buscar (liga/equipo/jugador/serial/patrón de archivo): ", this.scanner);

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
                        InventoryItem.Kind.PLAYER, // puedes cambiar clasificación si lo deseas
                        path + " : " + (line + 1) + " -> " + lines.get(line)
                    ));
                }
            }

            System.out.print("¿Agregar otra búsqueda? (1=Sí / 0=No): ");
            more = validate.valInt("", this.scanner) == 1;
        }

        // 2) DESENCOLAR y mostrar; 3) APILAR lo mostrado
        System.out.println("\n--- Resultados (DESENCOLADOS) ---");
        Stack<InventoryItem> stack = new Stack<InventoryItem>();
        while (!queue.isEmpty()) {
            InventoryItem item = queue.dequeue();
            System.out.println(item);
            stack.push(item);
        }

        // 4) DESAPILAR y guardar
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

    // ========= Recursión de archivos =========
    // NO FINAL: directorios
    private void findFilesNonTail(File dir, String needle, Queue<String> results) throws IOException {
        if (dir == null || !dir.exists()) return;
        File[] entries = dir.listFiles();
        if (entries == null) return;

        for (int i = 0; i < entries.length; i++) {
            File f = entries[i];
            if (f.isDirectory()) {
                findFilesNonTail(f, needle, results); // NO final
            } else if (f.getName().toLowerCase().contains(needle.toLowerCase())) {
                results.enqueue(f.getAbsolutePath());
            }
        }
    }

    // FINAL (tail): líneas
    private void findLinesTail(List<String> lines, String needle, int i, Queue<Integer> results) {
        if (i >= lines.size()) return;
        if (lines.get(i).contains(needle)) results.enqueue(i);
        findLinesTail(lines, needle, i + 1, results); // tail call
    }

    private List<String> readAllLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }

    // ========= Recursión sobre inventario en memoria (tu parte original) =========
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
