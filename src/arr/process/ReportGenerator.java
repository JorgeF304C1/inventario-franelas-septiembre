package arr.process;

import java.io.IOException;
import java.nio.file.Paths;
import arr.helpers.validate;

// Principio de Responsabilidad Única: Esta clase SOLO genera reportes.
public class ReportGenerator {

    /**
     * MÉTODO CORREGIDO: Genera múltiples reportes, cada uno en su propio archivo
     * con un nombre único (fecha, hora y serial).
     */
    public void generateAllReports(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, int[][] teamStats) throws IOException {
        if (leaguesName == null || teamsName == null || leaguesTeamsPlayers == null || availability == null || teamStats == null) {
            validate.addError("Error Crítico: Datos nulos al intentar generar reportes.");
            return;
        }

        // 1. Define y crea el directorio base para todos los reportes.
        String reportsDirectory = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        validate.utilDirectory(reportsDirectory);

        // --- 2. Generar Reporte en formato de Tabla ---
        String tableReportName = validate.nameArchiveGenerate("inventory_table_report");
        String tableReportPath = reportsDirectory + "/" + tableReportName + ".txt";
        System.out.println("\nGenerando reporte en formato de tabla...");
        showInventoryTable(leaguesName, teamsName, leaguesTeamsPlayers, availability, teamStats, tableReportPath);
        System.out.println("-> Reporte de tabla guardado en: " + tableReportPath);

        // --- 3. Generar Reporte con estructura Recursiva ---
        String recursiveReportName = validate.nameArchiveGenerate("inventory_recursive_report");
        String recursiveReportPath = reportsDirectory + "/" + recursiveReportName + ".txt";
        System.out.println("Generando reporte con estructura recursiva...");
        displayInventoryRecursively(leaguesName, teamsName, leaguesTeamsPlayers, availability, recursiveReportPath);
        System.out.println("-> Reporte recursivo guardado en: " + recursiveReportPath);
    }

    private void showInventoryTable(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, int[][] teamStats, String route) throws IOException {
        // (El código interno de este método no cambia, ya que recibe la ruta completa y única)
        int leagueWidth = 20, teamWidth = 25, playerWidth = 25, stockWidth = 8;
        int totalWidth = leagueWidth + teamWidth + playerWidth + stockWidth + 5 + 8;
        String border = "=".repeat(totalWidth);
        String subBorder = "-".repeat(totalWidth);

        validate.useArchive(border, route, true);
        String title = "REPORTE DE INVENTARIO DE CAMISETAS (TABLA)";
        int padding = (totalWidth - title.length()) / 2;
        validate.useArchive(String.format("%" + padding + "s%s", "", title), route, true);
        validate.useArchive(border, route, true);

        String headerFormat = "| %-" + leagueWidth + "s | %-" + teamWidth + "s | %-" + playerWidth + "s | %-" + stockWidth + "s |";
        validate.useArchive(String.format(headerFormat, "LIGA", "EQUIPO", "JUGADOR", "STOCK"), route, true);
        validate.useArchive(subBorder, route, true);
        
        String contentFormat = "| %-" + leagueWidth + "s | %-" + teamWidth + "s | %-" + playerWidth + "s | %" + stockWidth + "s |";
        for (int i = 0; i < leaguesName.length; i++) {
            validate.useArchive(String.format(contentFormat, leaguesName[i].toUpperCase(), "", "", ""), route, true);
            if (teamsName[i] == null) continue;
            for (int j = 0; j < teamsName[i].length; j++) {
                validate.useArchive(String.format(contentFormat, "", teamsName[i][j], "", ""), route, true);
                if (leaguesTeamsPlayers[i][j] == null) continue;
                for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                    String player = leaguesTeamsPlayers[i][j][k];
                    String stock = String.valueOf(availability[i][j][k]);
                    validate.useArchive(String.format(contentFormat, "", "", player, stock), route, true);
                }
                if (teamsName[i][j] != null && !teamsName[i][j].isEmpty()) {
                    String summaryFormat = "| %-" + (leagueWidth + teamWidth + playerWidth + 6) + "s | %" + stockWidth + "s |";
                    String totalLabel = "TOTAL EQUIPO ->";
                    String totalStock = String.valueOf(teamStats[i][j]);
                    validate.useArchive(String.format(summaryFormat, totalLabel, totalStock), route, true);
                    validate.useArchive(subBorder, route, true);
                }
            }
        }
    }

    private void displayInventoryRecursively(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, String route) throws IOException {
        // (El código interno de este método no cambia)
        validate.useArchive("\n\n--- REPORTE RECURSIVO ---", route, true);
        displayLeaguesRecursive(0, leaguesName, teamsName, leaguesTeamsPlayers, availability, route);
        validate.useArchive("--- FIN REPORTE RECURSIVO ---", route, true);
    }

    private void displayLeaguesRecursive(int leagueIndex, String[] leagues, String[][] teams, String[][][] players, int[][][] availability, String route) throws IOException {
        if (leagueIndex >= leagues.length) return;
        validate.useArchive("LIGA: " + leagues[leagueIndex], route, true);
        displayTeamsRecursive(leagueIndex, 0, teams, players, availability, route);
        displayLeaguesRecursive(leagueIndex + 1, leagues, teams, players, availability, route);
    }

    private void displayTeamsRecursive(int leagueIndex, int teamIndex, String[][] teams, String[][][] players, int[][][] availability, String route) throws IOException {
        if (teams[leagueIndex] == null || teamIndex >= teams[leagueIndex].length) return;
        validate.useArchive("\tEQUIPO: " + teams[leagueIndex][teamIndex], route, true);
        displayPlayersRecursive(leagueIndex, teamIndex, 0, players, availability, route);
        displayTeamsRecursive(leagueIndex, teamIndex + 1, teams, players, availability, route);
    }

    private void displayPlayersRecursive(int leagueIndex, int teamIndex, int playerIndex, String[][][] players, int[][][] availability, String route) throws IOException {
        if (players[leagueIndex][teamIndex] == null || playerIndex >= players[leagueIndex][teamIndex].length) return;
        String playerInfo = String.format("\t\t- Jugador: %s (Stock: %d)", 
            players[leagueIndex][teamIndex][playerIndex], 
            availability[leagueIndex][teamIndex][playerIndex]);
        validate.useArchive(playerInfo, route, true);
        displayPlayersRecursive(leagueIndex, teamIndex, playerIndex + 1, players, availability, route);
    }
}
