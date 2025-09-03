# Sistema de Gestión de Inventario para Camisetas de Fútbol

## 1. Descripción del Proyecto

Este proyecto es una aplicación de consola desarrollada en Java, diseñada para registrar y gestionar de forma sistemática un inventario de camisetas de fútbol. El sistema permite organizar la información de manera jerárquica, comenzando por las ligas, seguido de los equipos pertenecientes a cada liga, y finalmente los jugadores de cada equipo, junto con el stock de camisetas disponible para cada uno.

El principal objetivo es ofrecer una herramienta robusta y fiable que no solo capture los datos, sino que también los procese para generar estadísticas útiles y presente un reporte final claro y profesional.

---

## 2. Características Principales

* **Entrada de Datos Interactiva:** El sistema guía al usuario a través de la consola para registrar toda la información paso a paso.

* **Validación Exhaustiva:** Todas las entradas del usuario son validadas para garantizar la integridad de los datos (ej. no permite texto en campos numéricos, nombres vacíos, etc.).

* **Estructura de Datos Multidimensional:** Utiliza arreglos de 1, 2 y 3 dimensiones para almacenar y gestionar eficientemente la relación entre ligas, equipos, jugadores y su inventario.

* **Cálculo de Estadísticas:** Procesa los datos para calcular y mostrar el stock total de camisetas por equipo.

* **Generación de Reportes Automáticos:** Crea un archivo `.txt` con el inventario completo, formateado en una tabla clara y fácil de leer.

* **Registro de Errores:** Implementa un sistema de logging (`ErrorSystem.log`) que registra cualquier excepción o entrada inválida del usuario, facilitando la depuración.

* **Programación Defensiva:** Incluye precondiciones y comprobaciones de nulidad para prevenir errores fatales (`NullPointerException`) y asegurar la estabilidad del programa.

* **Gestión de Memoria:** Anula las referencias a los objetos al final de la ejecución para sugerir al Recolector de Basura de Java que libere la memoria.

---

## 3. Estructura del Proyecto

El código fuente está organizado de forma modular en tres archivos principales:

* **`Main.java`**: Es el punto de entrada de la aplicación. Se encarga de orquestar el flujo del programa, declarar las estructuras de datos principales e invocar a los métodos de las otras clases en el orden correcto.

* **`process.java`**: Contiene el núcleo lógico del sistema. Aquí se encuentran los métodos para inicializar los arreglos, recolectar los datos del usuario, realizar los cálculos de estadísticas y generar el reporte final.

* **`validate.java`**: Centraliza todas las funciones de validación y utilidades. Incluye métodos para validar enteros, cadenas de texto, así como para gestionar la creación de archivos y el registro de errores.

---

## 4. Tecnologías y Flujo de Trabajo

* **Lenguaje:** Java (JDK 8+)

* **Entorno de Desarrollo:** Visual Studio Code

* **Control de Versiones:** Git / GitHub

El proyecto sigue la metodología de flujo de trabajo **Git Flow**, utilizando las siguientes ramas principales:

* `master`: Contiene la versión final, estable y probada del producto.

* `development`: Es la rama principal de desarrollo donde se integran las nuevas características.

* `testing`: Rama utilizada para probar la estabilidad de una versión antes de su lanzamiento a `master`.

* `production`: Refleja la versión que está "en producción" o entregada.

Se utilizan **Ramas de Feature** para el desarrollo de nuevas funcionalidades de forma aislada y segura, y los mensajes de commit siguen la especificación de **Conventional Commits**.

---

## 5. Miembros del Equipo

* Steven Alcalá | 31.542.054

* Jorge Fattal | 31.505.044

* Ricardo Elbazi | 31.541.609