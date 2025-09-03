// CAMBIO: La declaración del paquete sigue siendo la misma.
package arr;

import java.io.IOException;
// CAMBIO: Se importan las clases necesarias de los otros paquetes.
import arr.process.ProcessMain;
import arr.helpers.validate;

// El diseño Top-Down se hace evidente aquí. Main es el punto más alto
// y delega toda la lógica a un módulo inferior (ProcessManager).
public class Main {
    public static void main(String[] args) {
        try {
            // 1. Creamos una instancia del gestor de procesos.
            ProcessMain manager = new ProcessMain();
            
            // 2. Ejecutamos el programa.
            manager.run();

        } catch (IOException e) {
            // Un manejo de errores básico en el punto más alto.
            System.err.println("Ha ocurrido un error fatal de entrada/salida.");
            // Se llama al método addError de la clase validate, que está en otro paquete.
            validate.addError("Error fatal en Main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
