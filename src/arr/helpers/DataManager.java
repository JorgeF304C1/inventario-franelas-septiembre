package arr.helpers;

import arr.io.ArchiveUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Maneja persistencia del inventario.
 * - Guarda estado con formato: inventory_state_YYYY-MM-DD:HHmmss_serial.txt
 * - Lee tanto .txt nuevos como .dat antiguos (compatibilidad).
 */
public class DataManager {

    private final String STORAGE_PATH;

    public DataManager() {
        String path;
        try {
            path = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        } catch (IOException e) {
            System.err.println("Error crítico al obtener la ruta del proyecto.");
            path = "storage";
        }
        this.STORAGE_PATH = path;
        ArchiveUtil.ensureDirectory(this.STORAGE_PATH);
    }

    public String[] listAvailableInventories() {
        File storageDir = new File(STORAGE_PATH);
        File[] files = storageDir.listFiles();
        if (files == null || files.length == 0) return new String[0];

        // contamos válidos
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName().toLowerCase();
            if (name.endsWith(".dat") || (name.startsWith("inventory_state_") && name.endsWith(".txt"))) count++;
        }
        String[] inventoryFiles = new String[count];
        int idx = 0;
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName().toLowerCase();
            if (name.endsWith(".dat") || (name.startsWith("inventory_state_") && name.endsWith(".txt"))) {
                inventoryFiles[idx++] = files[i].getName();
            }
        }
        return inventoryFiles;
    }

    public void saveInventoryState(String inventoryName, String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability) throws IOException {
        // Generamos nombre con formato exigido
        String serial = java.util.UUID.randomUUID().toString().substring(0, 8);
        String fileName = ArchiveUtil.safeName("inventory_state", serial); // .txt
        File file = new File(STORAGE_PATH + "/" + fileName);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            // cabecera de dimensiones
            writer.write(leaguesName.length + "," + teamsName[0].length + "," + leaguesTeamsPlayers[0][0].length);
            writer.newLine();

            for (int i = 0; i < leaguesName.length; i++) {
                String league = leaguesName[i] == null ? "" : leaguesName[i];
                writer.write("LIGA:" + league); writer.newLine();
                for (int j = 0; j < teamsName[i].length; j++) {
                    String team = teamsName[i][j] == null ? "" : teamsName[i][j];
                    writer.write("EQUIPO:" + team); writer.newLine();
                    for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                        String player = (leaguesTeamsPlayers[i][j][k] == null) ? "" : leaguesTeamsPlayers[i][j][k];
                        int stock = availability[i][j][k];
                        writer.write("JUGADOR:" + player + "::" + stock);
                        writer.newLine();
                    }
                }
            }
            System.out.println("-> Inventario guardado: " + file.getName());
        } finally {
            if (writer != null) try { writer.close(); } catch (IOException e) { /* ignore */ }
        }
    }

    public InventoryData loadInventoryState(String inventoryName) throws IOException {
        File file = new File(STORAGE_PATH + "/" + inventoryName);
        if (!file.exists()) {
            System.out.println("Error: El archivo de inventario '" + inventoryName + "' no existe.");
            return null;
        }

        System.out.println("Cargando inventario '" + inventoryName + "'...");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            String[] dimensions = line.split(",");
            int numLeagues = Integer.parseInt(dimensions[0]);
            int numTeams = Integer.parseInt(dimensions[1]);
            int numPlayers = Integer.parseInt(dimensions[2]);

            String[] leaguesName = new String[numLeagues];
            String[][] teamsName = new String[numLeagues][numTeams];
            String[][][] leaguesTeamsPlayers = new String[numLeagues][numTeams][numPlayers];
            int[][][] availability = new int[numLeagues][numTeams][numPlayers];

            for (int i = 0; i < numLeagues; i++) {
                String l = reader.readLine();
                leaguesName[i] = (l == null) ? "" : safeAfterColon(l);
                for (int j = 0; j < numTeams; j++) {
                    String t = reader.readLine();
                    teamsName[i][j] = (t == null) ? "" : safeAfterColon(t);
                    for (int k = 0; k < numPlayers; k++) {
                        String p = reader.readLine();
                        if (p == null) { leaguesTeamsPlayers[i][j][k] = ""; availability[i][j][k] = 0; continue; }
                        String[] parts = p.split("::");
                        String player = safeAfterColon(parts[0]);
                        int stock = 0;
                        if (parts.length > 1) {
                            try { stock = Integer.parseInt(parts[1]); } catch (NumberFormatException e) { stock = 0; }
                        }
                        leaguesTeamsPlayers[i][j][k] = player;
                        availability[i][j][k] = stock;
                    }
                }
            }
            System.out.println("-> Inventario cargado.");
            return new InventoryData(leaguesName, teamsName, leaguesTeamsPlayers, availability);
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException e) { /* ignore */ }
        }
    }

    private static String safeAfterColon(String s) {
        int idx = (s == null) ? -1 : s.indexOf(':');
        if (idx < 0) return "";
        return s.substring(idx + 1);
    }
}
