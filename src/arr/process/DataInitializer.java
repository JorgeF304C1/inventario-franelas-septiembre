package arr.process;

// Principio de Responsabilidad Única: Esta clase SOLO inicializa arreglos.
public class DataInitializer {

    public void initializeAllArrays(String[] leagues, String[][] teams, int[][] stats, String[][][] players, int[][][] availability) {
        initleaguesName(leagues);
        initTeamsName(teams);
        initTeamStats(stats);
        initLeaguesTeamsPlayers(players);
        initAvailability(availability);
    }

    private void initleaguesName(String[] leaguesName) {
        if (leaguesName != null) {
            for (int i = 0; i < leaguesName.length; i++) {
                leaguesName[i] = "";
            }
        }
    }

    private void initTeamsName(String[][] teamsName) {
        if (teamsName != null) {
            for (int i = 0; i < teamsName.length; i++) {
                if (teamsName[i] != null) {
                    for (int j = 0; j < teamsName[i].length; j++) {
                        teamsName[i][j] = "";
                    }
                }
            }
        }
    }

    private void initLeaguesTeamsPlayers(String[][][] leaguesTeamsPlayers) {
        if (leaguesTeamsPlayers != null) {
            for (int i = 0; i < leaguesTeamsPlayers.length; i++) {
                if (leaguesTeamsPlayers[i] != null) {
                    for (int j = 0; j < leaguesTeamsPlayers[i].length; j++) {
                        if (leaguesTeamsPlayers[i][j] != null) {
                            for (int k = 0; k < leaguesTeamsPlayers[i][j].length; k++) {
                                leaguesTeamsPlayers[i][j][k] = "";
                            }
                        }
                    }
                }
            }
        }
    }

    private void initAvailability(int[][][] availability) {
        if (availability != null) {
            for (int i = 0; i < availability.length; i++) {
                if (availability[i] != null) {
                    for (int j = 0; j < availability[i].length; j++) {
                        if (availability[i][j] != null) {
                            for (int k = 0; k < availability[i][j].length; k++) {
                                availability[i][j][k] = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    private void initTeamStats(int[][] teamStats) {
        if (teamStats != null) {
            for (int i = 0; i < teamStats.length; i++) {
                if (teamStats[i] != null) {
                    for (int j = 0; j < teamStats[i].length; j++) {
                        teamStats[i][j] = 0;
                    }
                }
            }
        }
    }
}
