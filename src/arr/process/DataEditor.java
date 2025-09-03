package arr.process;

import java.util.Scanner;
import arr.helpers.validate;

/**
 * Edita datos en runtime sobre los arreglos:
 * - Renombrar liga / equipo / jugador
 * - Ajustar stock de un jugador (valor absoluto)
 * No usa ArrayList. Trabaja directo sobre los arreglos.
 */
public final class DataEditor {

    private DataEditor() {}

    public static void runEditMenu(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, Scanner sc) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- EDITAR DATOS ---");
            System.out.println("1. Renombrar liga");
            System.out.println("2. Renombrar equipo");
            System.out.println("3. Renombrar jugador");
            System.out.println("4. Ajustar stock de un jugador");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            int op = validate.valInt("", sc);

            switch (op) {
                case 1: renameLeague(leagues, sc); break;
                case 2: renameTeam(leagues, teams, sc); break;
                case 3: renamePlayer(leagues, teams, players, sc); break;
                case 4: adjustPlayerStock(leagues, teams, players, availability, sc); break;
                case 0: exit = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private static void renameLeague(String[] leagues, Scanner sc) {
        String oldName = validate.valName("Liga a renombrar:", sc);
        int li = indexOf(leagues, oldName);
        if (li < 0) { System.out.println("No existe la liga."); return; }
        String newName = validate.valName("Nuevo nombre:", sc);
        leagues[li] = newName;
        System.out.println("Liga renombrada.");
    }

    private static void renameTeam(String[] leagues, String[][] teams, Scanner sc) {
        String league = validate.valName("Liga del equipo:", sc);
        int li = indexOf(leagues, league);
        if (li < 0) { System.out.println("No existe la liga."); return; }

        String team = validate.valName("Equipo a renombrar:", sc);
        int ti = indexOf(teams[li], team);
        if (ti < 0) { System.out.println("No existe el equipo."); return; }

        String newName = validate.valName("Nuevo nombre:", sc);
        teams[li][ti] = newName;
        System.out.println("Equipo renombrado.");
    }

    private static void renamePlayer(String[] leagues, String[][] teams, String[][][] players, Scanner sc) {
        String league = validate.valName("Liga del jugador:", sc);
        int li = indexOf(leagues, league);
        if (li < 0) { System.out.println("No existe la liga."); return; }

        String team = validate.valName("Equipo del jugador:", sc);
        int ti = indexOf(teams[li], team);
        if (ti < 0) { System.out.println("No existe el equipo."); return; }

        String player = validate.valName("Jugador a renombrar:", sc);
        int pi = indexOf(players[li][ti], player);
        if (pi < 0) { System.out.println("No existe el jugador."); return; }

        String newName = validate.valName("Nuevo nombre:", sc);
        players[li][ti][pi] = newName;
        System.out.println("Jugador renombrado.");
    }

    private static void adjustPlayerStock(String[] leagues, String[][] teams, String[][][] players, int[][][] availability, Scanner sc) {
        String league = validate.valName("Liga:", sc);
        int li = indexOf(leagues, league);
        if (li < 0) { System.out.println("No existe la liga."); return; }

        String team = validate.valName("Equipo:", sc);
        int ti = indexOf(teams[li], team);
        if (ti < 0) { System.out.println("No existe el equipo."); return; }

        String player = validate.valName("Jugador:", sc);
        int pi = indexOf(players[li][ti], player);
        if (pi < 0) { System.out.println("No existe el jugador."); return; }

        int newStock = validate.valInt("Nuevo stock (>=0): ", sc);
        if (newStock < 0) { System.out.println("Stock inválido."); return; }

        availability[li][ti][pi] = newStock;
        System.out.println("Stock actualizado.");
    }

    private static int indexOf(String[] arr, String target) {
        if (arr == null || target == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].equalsIgnoreCase(target)) return i;
        }
        return -1;
    }
}
