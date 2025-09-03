package arr.process;

import arr.consult.DataConsultant;
import arr.helpers.DataManager;
import arr.helpers.InventoryData;
import arr.helpers.validate;
import java.io.IOException;
import java.util.Scanner;

public class ProcessMain {

    // --- Variables de Instancia ---
    private String inventoryName;
    private String[] leaguesName;
    private String[][] teamsName;
    private int[][] teamStats;
    private String[][][] leaguesTeamsPlayers;
    private int[][][] availability;

    private Scanner scanner;
    private DataManager dataManager;

    // --- Constructor ---
    public ProcessMain() {
        this.scanner = new Scanner(System.in);
        this.dataManager = new DataManager();
    }

    // --- Flujo Principal del Programa ---
    public void run() throws IOException {
        boolean exitProgram = false;
        while (!exitProgram) {
            System.out.println("\n===== MENÚ PRINCIPAL DEL SISTEMA DE INVENTARIO =====");
            System.out.println("1. Crear un Nuevo Inventario");
            System.out.println("2. Cargar y Gestionar un Inventario Existente");
            System.out.println("0. Salir del Programa");
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
        System.out.println("\n--- Creación de Nuevo Inventario ---");
        String newInventoryName = validate.valName("Por favor, ingrese un nombre para este nuevo inventario (ej: 'liga_espanola_2025'):", this.scanner);
        this.inventoryName = newInventoryName;

        System.out.println("\n--- Configuración de Dimensiones ---");
        int numLeagues = validate.valInt("¿Cuántas ligas de fútbol deseas registrar?", scanner);
        int numTeams = validate.valInt("¿Cuántos equipos por liga como máximo?", scanner);
        int numPlayers = validate.valInt("¿Cuántos jugadores por equipo como máximo?", scanner);

        this.leaguesName = new String[numLeagues];
        this.teamsName = new String[numLeagues][numTeams];
        this.leaguesTeamsPlayers = new String[numLeagues][numTeams][numPlayers];
        this.availability = new int[numLeagues][numTeams][numPlayers];
        this.teamStats = new int[numLeagues][numTeams];

        DataInitializer initializer = new DataInitializer();
        DataCollector collector = new DataCollector();
        initializer.initializeAllArrays(leaguesName, teamsName, teamStats, leaguesTeamsPlayers, availability);
        collector.gatherAllData(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);

        System.out.println("\nGuardando nuevo inventario...");
        dataManager.saveInventoryState(this.inventoryName, leaguesName, teamsName, leaguesTeamsPlayers, availability);

        runPostLoadMenu();
    }

    private void loadAndManageInventory() throws IOException {
        System.out.println("\n--- Cargar Inventario Existente ---");
        String[] availableInventories = dataManager.listAvailableInventories();

        if (availableInventories.length == 0) {
            System.out.println("No se encontraron inventarios guardados. Por favor, cree uno nuevo.");
            return;
        }

        System.out.println("Inventarios disponibles:");
        for (int i = 0; i < availableInventories.length; i++) {
            System.out.printf("%d. %s\n", i + 1, availableInventories[i]);
        }
        System.out.println("0. Cancelar");
        System.out.print("Seleccione el inventario que desea cargar: ");
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
        System.out.printf("\n--- Gestionando Inventario: '%s' ---\n", this.inventoryName);
        StatsCalculator calculator = new StatsCalculator();
        calculator.calculateAllStats(this.availability, this.teamStats);

        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("\n--- Menú de Gestión ---");
            System.out.println("1. Consultar Datos (Búsqueda Recursiva en memoria)");
            System.out.println("2. Agregar Nueva Liga");
            System.out.println("3. Generar Reporte Completo del Inventario");
            System.out.println("4. Guardar Cambios en este Inventario");
            System.out.println("5. Buscador con estructuras (Archivos → Cola→Pila→Archivo)");
            System.out.println("0. Volver al Menú Principal");
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
                    // No necesita el inventario para el file-search, pero reuso scanner del programa
                    consultant.runFileSearchQueueStack(this.scanner);
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
