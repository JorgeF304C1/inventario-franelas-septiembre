package arr.helpers;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private final String STORAGE_PATH;

    public DataManager() {
        String path = "";
        try {
            path = Paths.get("").toRealPath().toString() + "/src/arr/storage";
        } catch (IOException e) {
            System.err.println("Error crítico al obtener la ruta del proyecto.");
            path = "storage"; // Fallback a una ruta relativa
        }
        this.STORAGE_PATH = path;
        validate.utilDirectory(this.STORAGE_PATH);
    }

    /**
     * Escanea el directorio de almacenamiento y devuelve una lista de los nombres
     * de los archivos de inventario (.dat) disponibles.
     */
    public List<String> listAvailableInventories() {
        List<String> inventoryFiles = new ArrayList<>();
        File storageDir = new File(STORAGE_PATH);
        File[] files = storageDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));

        if (files != null) {
            for (File file : files) {
                inventoryFiles.add(file.getName());
            }
        }
        return inventoryFiles;
    }

    /**
     * Guarda el estado completo del inventario en un archivo .dat específico.
     * @param inventoryName El nombre del archivo (ej. "liga_2025.dat")
     */
    public void saveInventoryState(String inventoryName, String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability) throws IOException {
        File file = new File(STORAGE_PATH + "/" + inventoryName.replace(".dat", "") + ".dat");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Guarda las dimensiones primero
            writer.write(leaguesName.length + "," + teamsName[0].length + "," + leaguesTeamsPlayers[0][0].length);
            writer.newLine();

            // Guarda los datos
            for (int i = 0; i < leaguesName.length; i++) {
                writer.write("LIGA:" + leaguesName[i]);
                writer.newLine();
                for (int j = 0; j < teamsName[i].length; j++) {
                    writer.write("EQUIPO:" + teamsName[i][j]);
                    writer.newLine();
                    for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                        writer.write("JUGADOR:" + leaguesTeamsPlayers[i][j][k] + "::" + availability[i][j][k]);
                        writer.newLine();
                    }
                }
            }
            System.out.println("-> Inventario '" + file.getName() + "' guardado exitosamente.");
        }
    }

    /**
     * Carga el estado completo de un inventario desde un archivo .dat específico.
     * @param inventoryName El nombre del archivo a cargar.
     * @return Un objeto InventoryData con todos los arreglos cargados, o null si falla.
     */
    public InventoryData loadInventoryState(String inventoryName) throws IOException {
        File file = new File(STORAGE_PATH + "/" + inventoryName);

        if (!file.exists()) {
            System.out.println("Error: El archivo de inventario '" + inventoryName + "' no existe.");
            return null;
        }

        System.out.println("Cargando inventario '" + inventoryName + "'...");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Lee las dimensiones
            String line = reader.readLine();
            String[] dimensions = line.split(",");
            int numLeagues = Integer.parseInt(dimensions[0]);
            int numTeams = Integer.parseInt(dimensions[1]);
            int numPlayers = Integer.parseInt(dimensions[2]);

            // Crea los arreglos con las dimensiones leídas
            String[] leaguesName = new String[numLeagues];
            String[][] teamsName = new String[numLeagues][numTeams];
            String[][][] leaguesTeamsPlayers = new String[numLeagues][numTeams][numPlayers];
            int[][][] availability = new int[numLeagues][numTeams][numPlayers];

            // Lee y llena los arreglos
            for (int i = 0; i < numLeagues; i++) {
                leaguesName[i] = reader.readLine().split(":")[1];
                for (int j = 0; j < numTeams; j++) {
                    teamsName[i][j] = reader.readLine().split(":")[1];
                    for (int k = 0; k < numPlayers; k++) {
                        String[] playerData = reader.readLine().split("::");
                        leaguesTeamsPlayers[i][j][k] = playerData[0].split(":")[1];
                        availability[i][j][k] = Integer.parseInt(playerData[1]);
                    }
                }
            }
            System.out.println("-> Inventario cargado exitosamente.");
            return new InventoryData(leaguesName, teamsName, leaguesTeamsPlayers, availability);
        }
    }
}
