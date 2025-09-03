package arr.process;

// Principio de Responsabilidad Única: Esta clase SOLO realiza cálculos.
public class StatsCalculator {

    public void calculateAllStats(int[][][] availability, int[][] teamStats) {
        if (availability == null || teamStats == null) return;
        
        for (int i = 0; i < availability.length; i++) {
            // Comprobación de seguridad
            if (availability[i] == null || teamStats[i] == null) continue;

            for (int j = 0; j < availability[i].length; j++) {
                int totalStock = 0;
                // Comprobación de seguridad
                if (availability[i][j] != null) {
                    for (int k = 0; k < availability[i][j].length; k++) {
                        totalStock += availability[i][j][k];
                    }
                }
                teamStats[i][j] = totalStock;
            }
        }
    }
}
