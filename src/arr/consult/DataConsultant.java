package arr.consult;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import arr.helpers.validate;

public class DataConsultant {

    private String[] leaguesName;
    private String[][] teamsName;
    private String[][][] leaguesTeamsPlayers;
    private int[][][] availability;
    private Scanner scanner;

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

        if (!reportContent.equals("Liga no encontrada.")) {
            // Imprime en consola
            System.out.println("\n--- Reporte de Consulta para la Liga: " + leagueToFind + " ---");
            System.out.println(reportContent);
            System.out.println("--- Fin del Reporte en Consola ---");
            
            // CAMBIO: Ahora también guarda el reporte en un archivo
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
            // Imprime en consola
            System.out.println("Resultado: " + reportContent);

            // CAMBIO: Ahora también guarda el reporte en un archivo
            saveQueryReport("query_report_equipo_" + teamToFind.replace(" ", "_"), reportContent);

        } else {
            System.out.printf("Resultado: No se encontró el equipo '%s' en la liga '%s'.\n", teamToFind, leagueToFind);
        }
    }

    /**
     * CAMBIO: Nuevo método para guardar el resultado de una consulta en un archivo.
     */
    private void saveQueryReport(String reportPrefix, String content) throws IOException {
        String storageDirectory = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        validate.utilDirectory(storageDirectory);

        String reportName = validate.nameArchiveGenerate(reportPrefix);
        String reportPath = storageDirectory + "/" + reportName + ".txt";
        
        String finalContent = "--- INICIO REPORTE DE CONSULTA ---\n" + content + "\n--- FIN REPORTE DE CONSULTA ---";
        
        validate.useArchive(finalContent, reportPath, true);
        System.out.println("-> Se ha guardado un reporte detallado de esta consulta en: " + reportPath);
    }


    // --- Métodos de Búsqueda Recursiva (sin cambios) ---

    private String findLeagueDetailsRecursive(String leagueNameToFind, int currentLeagueIndex) {
        if (currentLeagueIndex >= this.leaguesName.length) {
            return "Liga no encontrada.";
        }
        if (this.leaguesName[currentLeagueIndex].equalsIgnoreCase(leagueNameToFind)) {
            StringBuilder details = new StringBuilder();
            for (int j = 0; j < this.teamsName[currentLeagueIndex].length; j++) {
                if(this.teamsName[currentLeagueIndex][j] == null || this.teamsName[currentLeagueIndex][j].isEmpty()) continue;
                details.append("  Equipo: ").append(this.teamsName[currentLeagueIndex][j]).append("\n");
                for (int k = 0; k < this.leaguesTeamsPlayers[currentLeagueIndex][j].length; k++) {
                    details.append("    - Jugador: ").append(this.leaguesTeamsPlayers[currentLeagueIndex][j][k]);
                    details.append(" (Stock: ").append(this.availability[currentLeagueIndex][j][k]).append(")\n");
                }
            }
            return details.toString();
        }
        return findLeagueDetailsRecursive(leagueNameToFind, currentLeagueIndex + 1);
    }
    
    private int findTeamStockRecursive(String leagueNameToFind, String teamNameToFind, int currentLeagueIndex, int currentTeamIndex) {
        if (currentLeagueIndex >= this.leaguesName.length) return -1;
        if (this.leaguesName[currentLeagueIndex].equalsIgnoreCase(leagueNameToFind)) {
            if (currentTeamIndex >= this.teamsName[currentLeagueIndex].length) return -1;
            if(this.teamsName[currentLeagueIndex][currentTeamIndex].equalsIgnoreCase(teamNameToFind)) {
                int totalStock = 0;
                for (int k = 0; k < this.availability[currentLeagueIndex][currentTeamIndex].length; k++) {
                    totalStock += this.availability[currentLeagueIndex][currentTeamIndex][k];
                }
                return totalStock;
            }
            return findTeamStockRecursive(leagueNameToFind, teamNameToFind, currentLeagueIndex, currentTeamIndex + 1);
        }
        return findTeamStockRecursive(leagueNameToFind, teamNameToFind, currentLeagueIndex + 1, 0);
    }
}
