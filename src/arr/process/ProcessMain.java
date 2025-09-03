package arr.process;

import java.io.IOException;
import java.util.Scanner;

import arr.helpers.validate;
import arr.consult.DataConsultant;
import arr.helpers.DataManager;
import arr.helpers.InventoryData;

public class ProcessMain {

    private String inventoryName;
    private String[] leaguesName;
    private String[][] teamsName;
    private int[][] teamStats;
    private String[][][] leaguesTeamsPlayers;
    private int[][][] availability;

    private Scanner scanner;
    private DataManager dataManager;

    public ProcessMain() {
        this.scanner = new Scanner(System.in);
        this.dataManager = new DataManager();
    }

    public void run() throws IOException {
        boolean exitProgram = false;
        while (!exitProgram) {
            System.out.println("\n===== MENÚ PRINCIPAL =====");
            System.out.println("1. Crear nuevo inventario");
            System.out.println("2. Cargar y gestionar inventario existente");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            int choice = validate.valInt("", this.scanner);

            switch (choice) {
                case 1:
                    createNewInventory();
                    break;
                case 2:
                    loadAndManageInventory();
                    break;
                case 0:
                    exitProgram = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        cleanup();
    }

    private void createNewInventory() throws IOException {
        System.out.println("\n--- Creación de inventario ---");
        String newInventoryName = validate.valName("Nombre del inventario (ej: 'liga_espanola_2025'):", this.scanner);
        this.inventoryName = newInventoryName;

        System.out.println("\n--- Dimensiones ---");
        int numLeagues = validate.valInt("¿Cuántas ligas registrar?", scanner);
        int numTeams = validate.valInt("¿Cuántos equipos por liga (máx)?", scanner);
        int numPlayers = validate.valInt("¿Cuántos jugadores por equipo (máx)?", scanner);

        this.leaguesName = new String[numLeagues];
        this.teamsName = new String[numLeagues][numTeams];
        this.leaguesTeamsPlayers = new String[numLeagues][numTeams][numPlayers];
        this.availability = new int[numLeagues][numTeams][numPlayers];
        this.teamStats = new int[numLeagues][numTeams];

        DataInitializer initializer = new DataInitializer();
        DataCollector collector = new DataCollector();
        initializer.initializeAllArrays(leaguesName, teamsName, teamStats, leaguesTeamsPlayers, availability);
        collector.gatherAllData(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);

        System.out.println("\nGuardando inventario...");
        dataManager.saveInventoryState(this.inventoryName, leaguesName, teamsName, leaguesTeamsPlayers, availability);

        runPostLoadMenu();
    }

    private void loadAndManageInventory() throws IOException {
        System.out.println("\n--- Cargar inventario ---");
        String[] availableInventories = dataManager.listAvailableInventories();

        if (availableInventories.length == 0) {
            System.out.println("No se encontraron inventarios guardados. Cree uno nuevo.");
            return;
        }

        System.out.println("Inventarios disponibles:");
        for (int i = 0; i < availableInventories.length; i++) {
            System.out.printf("%d. %s\n", i + 1, availableInventories[i]);
        }
        System.out.println("0. Cancelar");
        System.out.print("Seleccione: ");
        int choice = validate.valInt("", this.scanner);

        if (choice > 0 && choice <= availableInventories.length) {
            this.inventoryName = availableInventories[choice - 1];
            InventoryData loadedData = dataManager.loadInventoryState(this.inventoryName);
            if (loadedData != null) {
                this.leaguesName = loadedData.leaguesName;
                this.teamsName = loadedData.teamsName;
                this.leaguesTeamsPlayers = loadedData.leaguesTeamsPlayers;
                this.availability = loadedData.availability;
                this.teamStats = new int[this.leaguesName.length][this.teamsName[0].length];

                runPostLoadMenu();
            }
        }
    }

    private void runPostLoadMenu() throws IOException {
        System.out.printf("\n--- Gestionando inventario: '%s' ---\n", this.inventoryName);
        StatsCalculator calculator = new StatsCalculator();
        calculator.calculateAllStats(this.availability, this.teamStats);

        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("\n--- Menú de Gestión ---");
            System.out.println("1. Consultar datos del inventario (recursivo en memoria)");
            System.out.println("2. Agregar nueva liga");
            System.out.println("3. Generar reporte completo");
            System.out.println("4. Guardar cambios en este inventario");
            System.out.println("5. Buscador por archivos (Archivos → Cola→Pila→Archivo)");
            System.out.println("6. Editar datos (liga/equipo/jugador/stock)");
            System.out.println("7. Eliminar datos (liga/equipo/jugador)");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            int choice = validate.valInt("", this.scanner);

            switch (choice) {
                case 1: {
                    DataConsultant consultant = new DataConsultant();
                    consultant.runConsultMenu(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);
                    break;
                }
                case 2: {
                    DataCollector collector = new DataCollector();
                    InventoryData updatedData = collector.addLeague(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);
                    updateLocalData(updatedData);
                    calculator.calculateAllStats(this.availability, this.teamStats);
                    break;
                }
                case 3: {
                    CompleteReportGenerator completeReporter = new CompleteReportGenerator();
                    completeReporter.generate(leaguesName, teamsName, teamStats, leaguesTeamsPlayers, availability);
                    break;
                }
                case 4: {
                    dataManager.saveInventoryState(this.inventoryName, leaguesName, teamsName, leaguesTeamsPlayers, availability);
                    break;
                }
                case 5: {
                    DataConsultant consultant = new DataConsultant();
                    consultant.runFileSearchQueueStack(this.scanner);
                    break;
                }
                case 6: {
                    DataEditor.runEditMenu(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);
                    calculator.calculateAllStats(this.availability, this.teamStats);
                    break;
                }
                case 7: {
                    DataRemover.runDeleteMenu(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);
                    calculator.calculateAllStats(this.availability, this.teamStats);
                    break;
                }
                case 0: {
                    exitMenu = true;
                    clearLocalData();
                    break;
                }
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void updateLocalData(InventoryData data) {
        this.leaguesName = data.leaguesName;
        this.teamsName = data.teamsName;
        this.leaguesTeamsPlayers = data.leaguesTeamsPlayers;
        this.availability = data.availability;
        this.teamStats = new int[this.leaguesName.length][this.teamsName[0].length];
    }

    private void clearLocalData() {
        this.inventoryName = null;
        this.leaguesName = null;
        this.teamsName = null;
        this.teamStats = null;
        this.leaguesTeamsPlayers = null;
        this.availability = null;
    }

    private void cleanup() {
        // Limpieza final si aplica.
    }
}
