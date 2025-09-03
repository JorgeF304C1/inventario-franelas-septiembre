package arr.process;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import arr.helpers.InventoryData;
import arr.helpers.validate;

public class DataCollector {

    /**
     * Recolecta todos los datos para un inventario completamente nuevo.
     */
    public void gatherAllData(String[] leaguesName, String[][] teamsName, String[][][] leaguesTeamsPlayers, int[][][] availability, Scanner scanner) throws IOException {
        for (int i = 0; i < leaguesName.length; i++) {
            leaguesName[i] = validate.valName("Ingrese el nombre de la LIGA " + (i + 1) + ":", scanner);
            for (int j = 0; j < teamsName[i].length; j++) {
                teamsName[i][j] = validate.valName("Ingrese el nombre del EQUIPO " + (j + 1) + " (Liga: " + leaguesName[i] + "):", scanner);
                for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                    leaguesTeamsPlayers[i][j][k] = validate.valName("Ingrese el nombre del JUGADOR " + (k + 1) + " (Equipo: " + teamsName[i][j] + "):", scanner);
                    String stockText = "¿Cuántas camisetas tiene el jugador " + leaguesTeamsPlayers[i][j][k] + "?";
                    availability[i][j][k] = validate.valInt(stockText, 100, scanner);
                }
            }
        }
    }

    /**
     * Agrega una nueva liga completa a un inventario existente y devuelve los datos actualizados.
     */
    public InventoryData addLeague(String[] oldLeaguesName, String[][] oldTeamsName, String[][][] oldLeaguesTeamsPlayers, int[][][] oldAvailability, Scanner scanner) throws IOException {
        System.out.println("\n--- Agregando Nueva Liga ---");
        
        // --- 1. Redimensionar los arreglos ---
        int newNumLeagues = oldLeaguesName.length + 1;
        int numTeams = oldTeamsName[0].length;
        int numPlayers = oldLeaguesTeamsPlayers[0][0].length;

        String[] newLeaguesName = Arrays.copyOf(oldLeaguesName, newNumLeagues);
        String[][] newTeamsName = Arrays.copyOf(oldTeamsName, newNumLeagues);
        String[][][] newLeaguesTeamsPlayers = Arrays.copyOf(oldLeaguesTeamsPlayers, newNumLeagues);
        int[][][] newAvailability = Arrays.copyOf(oldAvailability, newNumLeagues);

        // Se necesita crear las nuevas "filas" para la nueva liga
        newTeamsName[newNumLeagues - 1] = new String[numTeams];
        newLeaguesTeamsPlayers[newNumLeagues - 1] = new String[numTeams][numPlayers];
        newAvailability[newNumLeagues - 1] = new int[numTeams][numPlayers];


        // --- 2. Recolectar datos solo para la nueva liga ---
        int newLeagueIndex = newNumLeagues - 1;
        newLeaguesName[newLeagueIndex] = validate.valName("Ingrese el nombre de la NUEVA LIGA " + newNumLeagues + ":", scanner);

        for (int j = 0; j < numTeams; j++) {
            newTeamsName[newLeagueIndex][j] = validate.valName("Ingrese el nombre del EQUIPO " + (j + 1) + " (Liga: " + newLeaguesName[newLeagueIndex] + "):", scanner);
            newLeaguesTeamsPlayers[newLeagueIndex][j] = new String[numPlayers];
            newAvailability[newLeagueIndex][j] = new int[numPlayers];

            for (int k = 0; k < numPlayers; k++) {
                newLeaguesTeamsPlayers[newLeagueIndex][j][k] = validate.valName("Ingrese el nombre del JUGADOR " + (k + 1) + " (Equipo: " + newTeamsName[newLeagueIndex][j] + "):", scanner);
                String stockText = "¿Cuántas camisetas tiene el jugador " + newLeaguesTeamsPlayers[newLeagueIndex][j][k] + "?";
                newAvailability[newLeagueIndex][j][k] = validate.valInt(stockText, 100, scanner);
            }
        }
        
        System.out.println("Nueva liga agregada exitosamente.");

        // --- 3. Devolver todos los arreglos actualizados ---
        // Se crea el objeto InventoryData con los 4 arreglos como argumentos.
        return new InventoryData(newLeaguesName, newTeamsName, newLeaguesTeamsPlayers, newAvailability);
    }
}
