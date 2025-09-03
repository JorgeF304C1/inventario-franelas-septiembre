package arr.process;

import java.io.IOException;
import java.nio.file.Paths;

import arr.helpers.validate;
import arr.io.ArchiveUtil;

public class ReportGenerator {

    private static String rep(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    public void generateAllReports(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, int[][] teamStats) throws IOException {
        if (leaguesName == null || teamsName == null || leaguesTeamsPlayers == null || availability == null || teamStats == null) {
            validate.addError("Error Crítico: Datos nulos al intentar generar reportes.");
            return;
        }

        String reportsDirectory = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        ArchiveUtil.ensureDirectory(reportsDirectory);

        String tableReportName = ArchiveUtil.safeName("inventory_table_report", serial());
        String tableReportPath = reportsDirectory + "/" + tableReportName;
        System.out.println("\nGenerando reporte en formato de tabla...");
        showInventoryTable(leaguesName, teamsName, leaguesTeamsPlayers, availability, teamStats, tableReportPath);
        System.out.println("-> Reporte de tabla guardado en: " + tableReportPath);

        String recursiveReportName = ArchiveUtil.safeName("inventory_recursive_report", serial());
        String recursiveReportPath = reportsDirectory + "/" + recursiveReportName;
        System.out.println("Generando reporte con estructura recursiva...");
        displayInventoryRecursively(leaguesName, teamsName, leaguesTeamsPlayers, availability, recursiveReportPath);
        System.out.println("-> Reporte recursivo guardado en: " + recursiveReportPath);
    }

    private static String serial() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    private void showInventoryTable(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, int[][] teamStats, String route) throws IOException {
        int leagueWidth = 20, teamWidth = 25, playerWidth = 25, stockWidth = 8;
        int totalWidth = leagueWidth + teamWidth + playerWidth + stockWidth + 5 + 8;
        String border = rep('=', totalWidth);
        String subBorder = rep('-', totalWidth);

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
                if (teamsName[i][j] == null || teamsName[i][j].isEmpty()) continue;
                validate.useArchive(String.format(contentFormat, "", teamsName[i][j], "", ""), route, true);
                if (leaguesTeamsPlayers[i][j] == null) continue;
                int total = 0;
                for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                    String player = leaguesTeamsPlayers[i][j][k];
                    String stock = String.valueOf(availability[i][j][k]);
                    total += availability[i][j][k];
                    validate.useArchive(String.format(contentFormat, "", "", player, stock), route, true);
                }
                String summaryFormat = "| %-" + (leagueWidth + teamWidth + playerWidth + 6) + "s | %" + stockWidth + "s |";
                String totalLabel = "TOTAL EQUIPO ->";
                String totalStock = String.valueOf(total);
                validate.useArchive(String.format(summaryFormat, totalLabel, totalStock), route, true);
                validate.useArchive(subBorder, route, true);
            }
        }
    }

    private void displayInventoryRecursively(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, String route) throws IOException {
        writeRecursive(leaguesName, teamsName, leaguesTeamsPlayers, availability, 0, 0, 0, route);
    }

    private void writeRecursive(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, int i, int j, int k, String route) throws IOException {
        if (i >= leagues.length) return;
        if (j >= teams[i].length) { writeRecursive(leagues, teams, players, availability, i + 1, 0, 0, route); return; }
        if (k >= players[i][j].length) { writeRecursive(leagues, teams, players, availability, i, j + 1, 0, route); return; }

        if (k == 0 && j == 0) {
            validate.useArchive("\nLIGA: " + leagues[i], route, true);
        }
        if (k == 0) {
            validate.useArchive("  EQUIPO: " + teams[i][j], route, true);
        }
        validate.useArchive("    JUGADOR: " + players[i][j][k] + " | STOCK: " + availability[i][j][k], route, true);

        writeRecursive(leagues, teams, players, availability, i, j, k + 1, route);
    }
}
