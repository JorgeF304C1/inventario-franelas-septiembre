package arr.helpers;

/**
 * Esta clase es un simple "contenedor" o "transportador" de datos.
 * Su única responsabilidad es mantener todos los arreglos del inventario juntos
 * para poder pasarlos fácilmente entre diferentes partes del programa.
 */
public class InventoryData {

    // Se declaran como 'public final' para que sean de solo lectura desde fuera,
    // pero fáciles de acceder.
    public final String[] leaguesName;
    public final String[][] teamsName;
    public final String[][][] leaguesTeamsPlayers;
    public final int[][][] availability;

    /**
     * Constructor que recibe todos los arreglos del inventario y los guarda.
     * Este es el constructor que faltaba y que causaba el error.
     */
    public InventoryData(String[] leagues, String[][] teams, String[][][] players, int[][][] avail) {
        this.leaguesName = leagues;
        this.teamsName = teams;
        this.leaguesTeamsPlayers = players;
        this.availability = avail;
    }
}
