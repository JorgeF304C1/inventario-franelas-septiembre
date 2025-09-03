package arr.process;

import java.io.IOException;
import java.nio.file.Paths;
import arr.helpers.validate;

// Principio de Responsabilidad Única: Esta clase SOLO genera el reporte completo.
public class CompleteReportGenerator {

    /**
     * Genera un único reporte completo en formato de tabla,
     * guardado en un archivo individual con nombre único.
     */
    public void generate(String[] leaguesName, String[][] teamsName, int[][] teamStats, String[][][] leaguesTeamsPlayers, int[][][] availability) throws IOException {
        if (leaguesName == null) {
            System.out.println("No hay datos cargados para generar un reporte.");
            return;
        }

        // 1. Define y crea el directorio base para todos los reportes.
        String reportsDirectory = Paths.get("").toRealPath().toString() + "/src/arr/reports";
        validate.utilDirectory(reportsDirectory);

        // 2. Generar el nombre y la ruta del archivo.
        String reportName = validate.nameArchiveGenerate("complete_inventory_report");
        String reportPath = reportsDirectory + "/" + reportName + ".txt";
        System.out.println("\nGenerando reporte completo...");

        // 3. Escribir el contenido en el archivo.
        writeInventoryTable(leaguesName, teamsName, teamStats, leaguesTeamsPlayers, availability, reportPath);
        System.out.println("-> Reporte completo guardado en: " + reportPath);
    }

    private void writeInventoryTable(String[] leaguesName, String[][] teamsName, int[][] teamStats, String[][][] leaguesTeamsPlayers, int[][][] availability, String route) throws IOException {
        int leagueWidth = 20, teamWidth = 25, playerWidth = 25, stockWidth = 8;
        int totalWidth = leagueWidth + teamWidth + playerWidth + stockWidth + 5 + 8;
        String border = "=".repeat(totalWidth);
        String subBorder = "-".repeat(totalWidth);

        validate.useArchive(border, route, true);
        String title = "REPORTE DE INVENTARIO COMPLETO";
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
                if(teamsName[i][j] == null || teamsName[i][j].isEmpty()) continue;
                validate.useArchive(String.format(contentFormat, "", teamsName[i][j], "", ""), route, true);
                if (leaguesTeamsPlayers[i][j] == null) continue;
                for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                    String player = leaguesTeamsPlayers[i][j][k];
                    String stock = String.valueOf(availability[i][j][k]);
                    validate.useArchive(String.format(contentFormat, "", "", player, stock), route, true);
                }
                
                String summaryFormat = "| %-" + (leagueWidth + teamWidth + playerWidth + 6) + "s | %" + stockWidth + "s |";
                String totalLabel = "TOTAL EQUIPO ->";
                String totalStock = String.valueOf(teamStats[i][j]);
                validate.useArchive(String.format(summaryFormat, totalLabel, totalStock), route, true);
                validate.useArchive(subBorder, route, true);
            }
        }
    }
}
