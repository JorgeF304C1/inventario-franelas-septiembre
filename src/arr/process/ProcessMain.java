package arr.process;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import arr.helpers.validate;
import arr.consult.DataConsultant;
import arr.helpers.DataManager;
import arr.helpers.InventoryData;

public class ProcessMain {

    // --- Variables de Instancia ---
    // Estas variables ahora guardan el estado del inventario ACTIVO.
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
        String newInventoryName = validate.valName("Por favor, ingrese un nombre para este nuevo inventario (ej: 'liga_española_2025'):", this.scanner);
        this.inventoryName = newInventoryName;

        // --- CORRECCIÓN: Se pide la configuración y se instancian los arreglos aquí ---
        System.out.println("\n--- Configuración de Dimensiones ---");
        int numLeagues = validate.valInt("¿Cuántas ligas de fútbol deseas registrar?", scanner);
        int numTeams = validate.valInt("¿Cuántos equipos por liga como máximo?", scanner);
        int numPlayers = validate.valInt("¿Cuántos jugadores por equipo como máximo?", scanner);

        // --- CORRECCIÓN: Este es el paso crucial que faltaba ---
        // Se instancian los arreglos con las dimensiones dadas
        this.leaguesName = new String[numLeagues];
        this.teamsName = new String[numLeagues][numTeams];
        this.leaguesTeamsPlayers = new String[numLeagues][numTeams][numPlayers];
        this.availability = new int[numLeagues][numTeams][numPlayers];
        this.teamStats = new int[numLeagues][numTeams];
        // --- FIN DE LA CORRECCIÓN ---

        // Inicializa y recolecta los datos
        DataInitializer initializer = new DataInitializer();
        DataCollector collector = new DataCollector();
        initializer.initializeAllArrays(leaguesName, teamsName, teamStats, leaguesTeamsPlayers, availability);
        collector.gatherAllData(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);

        // Guarda el nuevo inventario inmediatamente
        System.out.println("\nGuardando nuevo inventario...");
        dataManager.saveInventoryState(this.inventoryName, leaguesName, teamsName, leaguesTeamsPlayers, availability);

        // Permite gestionarlo de inmediato
        runPostLoadMenu();
    }

    private void loadAndManageInventory() throws IOException {
        System.out.println("\n--- Cargar Inventario Existente ---");
        List<String> availableInventories = dataManager.listAvailableInventories();

        if (availableInventories.isEmpty()) {
            System.out.println("No se encontraron inventarios guardados. Por favor, cree uno nuevo.");
            return;
        }

        System.out.println("Inventarios disponibles:");
        for (int i = 0; i < availableInventories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, availableInventories.get(i));
        }
        System.out.println("0. Cancelar");
        System.out.print("Seleccione el inventario que desea cargar: ");
        int choice = validate.valInt("", this.scanner);

        if (choice > 0 && choice <= availableInventories.size()) {
            this.inventoryName = availableInventories.get(choice - 1);
            InventoryData loadedData = dataManager.loadInventoryState(this.inventoryName);
            if (loadedData != null) {
                // Carga los datos en las variables de instancia de esta clase
                this.leaguesName = loadedData.leaguesName;
                this.teamsName = loadedData.teamsName;
                this.leaguesTeamsPlayers = loadedData.leaguesTeamsPlayers;
                this.availability = loadedData.availability;
                this.teamStats = new int[this.leaguesName.length][this.teamsName[0].length];

                // Entra al menú de gestión para este inventario
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
            System.out.println("1. Consultar Datos (Búsqueda Recursiva)");
            System.out.println("2. Agregar Nueva Liga");
            System.out.println("3. Generar Reporte Completo del Inventario");
            System.out.println("4. Guardar Cambios en este Inventario");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            int choice = validate.valInt("", this.scanner);

            switch (choice) {
                case 1:
                    DataConsultant consultant = new DataConsultant();
                    consultant.runConsultMenu(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);
                    break;
                case 2:
                    DataCollector collector = new DataCollector();
                    InventoryData updatedData = collector.addLeague(leaguesName, teamsName, leaguesTeamsPlayers, availability, scanner);
                    // Actualiza los datos en memoria
                    updateLocalData(updatedData);
                    calculator.calculateAllStats(this.availability, this.teamStats);
                    break;
                case 3:
                    CompleteReportGenerator completeReporter = new CompleteReportGenerator();
                    completeReporter.generate(leaguesName, teamsName, teamStats, leaguesTeamsPlayers, availability);
                    break;
                case 4:
                    dataManager.saveInventoryState(this.inventoryName, leaguesName, teamsName, leaguesTeamsPlayers, availability);
                    break;
                case 0:
                    exitMenu = true;
                    // Limpia los datos en memoria al salir del menú de gestión
                    clearLocalData();
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void setupInitialConfig() {
        //... (Este método no cambia)
    }

    private void updateLocalData(InventoryData data) {
        this.leaguesName = data.leaguesName;
        this.teamsName = data.teamsName;
        this.leaguesTeamsPlayers = data.leaguesTeamsPlayers;
        this.availability = data.availability;
        // Se redimensiona stats porque la cantidad de equipos pudo haber cambiado
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
        //... (Este método no cambia)
    }
}
