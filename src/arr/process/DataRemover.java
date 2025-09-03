package arr.process;

import java.util.Scanner;
import arr.helpers.validate;

/**
 * Elimina datos en runtime sobre los arreglos:
 * - Eliminar jugador (limpia nombre y stock=0)
 * - Eliminar equipo (limpia nombre y todos sus jugadores)
 * - Eliminar liga  (limpia nombre y todo el subárbol)
 * No usa ArrayList. Mantiene dimensiones; deja huecos como ""/0.
 */
public final class DataRemover {

    private DataRemover() {}

    public static void runDeleteMenu(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, Scanner sc) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- ELIMINAR DATOS ---");
            System.out.println("1. Eliminar jugador");
            System.out.println("2. Eliminar equipo (en cascada)");
            System.out.println("3. Eliminar liga (en cascada)");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            int op = validate.valInt("", sc);

            switch (op) {
                case 1: deletePlayer(leagues, teams, players, availability, sc); break;
                case 2: deleteTeam(leagues, teams, players, availability, sc); break;
                case 3: deleteLeague(leagues, teams, players, availability, sc); break;
                case 0: exit = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private static void deletePlayer(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, Scanner sc) {
        String league = validate.valName("Liga:", sc);
        int li = indexOf(leagues, league);
        if (li < 0) { System.out.println("No existe la liga."); return; }

        String team = validate.valName("Equipo:", sc);
        int ti = indexOf(teams[li], team);
        if (ti < 0) { System.out.println("No existe el equipo."); return; }

        String player = validate.valName("Jugador a eliminar:", sc);
        int pi = indexOf(players[li][ti], player);
        if (pi < 0) { System.out.println("No existe el jugador."); return; }

        players[li][ti][pi] = "";
        availability[li][ti][pi] = 0;
        System.out.println("Jugador eliminado (espacio liberado).");
    }

    private static void deleteTeam(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, Scanner sc) {
        String league = validate.valName("Liga del equipo:", sc);
        int li = indexOf(leagues, league);
        if (li < 0) { System.out.println("No existe la liga."); return; }

        String team = validate.valName("Equipo a eliminar:", sc);
        int ti = indexOf(teams[li], team);
        if (ti < 0) { System.out.println("No existe el equipo."); return; }

        teams[li][ti] = "";
        for (int k = 0; k < players[li][ti].length; k++) {
            players[li][ti][k] = "";
            availability[li][ti][k] = 0;
        }
        System.out.println("Equipo eliminado (jugadores y stock limpiados).");
    }

    private static void deleteLeague(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, Scanner sc) {
        String league = validate.valName("Liga a eliminar:", sc);
        int li = indexOf(leagues, league);
        if (li < 0) { System.out.println("No existe la liga."); return; }

        leagues[li] = "";
        for (int j = 0; j < teams[li].length; j++) {
            teams[li][j] = "";
            for (int k = 0; k < players[li][j].length; k++) {
                players[li][j][k] = "";
                availability[li][j][k] = 0;
            }
        }
        System.out.println("Liga eliminada (equipos y jugadores limpiados).");
    }

    private static int indexOf(String[] arr, String target) {
        if (arr == null || target == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].equalsIgnoreCase(target)) return i;
        }
        return -1;
    }
}
