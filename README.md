# Documentación de la Aplicación MySQLDirectDumper

Este proyecto es una aplicación de escritorio desarrollada en Java Swing y JavaFX (para el editor de texto enriquecido) que proporciona un conjunto de herramientas para la gestión y manipulación de bases de datos MySQL. La aplicación busca ofrecer una interfaz de usuario intuitiva para tareas como la ejecución de consultas SQL, la administración de usuarios y permisos, la visualización de metadatos, la generación de informes, la creación de gráficos, la importación/exportación de SQL, la realización de respaldos, y un editor de texto enriquecido.

## Arquitectura de Clases (Patrón Modelo-Vista-Controlador - MVC)

La aplicación está diseñada con un enfoque en el patrón Modelo-Vista-Controlador (MVC), aunque en algunas clases, la Vista y el Controlador pueden estar más acoplados para simplificar la implementación.

* **Modelo**: Representa los datos y la lógica de negocio. Esto incluye la interacción directa con la base de datos MySQL (conexión, ejecución de consultas CRUD, administración de permisos, obtención de metadatos) y la lógica para operaciones como la generación de informes o la comunicación con servicios externos (IA).
* **Vista**: Constituye la interfaz de usuario gráfica. Son las ventanas (`JFrame`) y paneles (`JPanel`) que el usuario ve y con las que interactúa, mostrando los datos y recibiendo las acciones del usuario.
* **Controlador**: Actúa como intermediario entre el Modelo y la Vista. Recibe las interacciones del usuario de la Vista, las procesa, invoca la lógica de negocio en el Modelo y luego actualiza la Vista con los nuevos datos o resultados.

## Clases Principales y su Rol

A continuación, se describen las clases clave de la aplicación y su rol dentro de la arquitectura:

### 1. `Main.java`
* **Rol**: Inicializador de la aplicación.
* **Descripción**: Contiene el método `main` que arranca la aplicación, instanciando y mostrando la ventana de `Login`.

### 2. `Login.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Ventana de inicio de sesión y registro de usuarios, con soporte para autenticación de dos factores (2FA) mediante Google Authenticator. Gestiona la validación de credenciales contra la base de datos.
* **Interacción**: Autentica usuarios y, en caso de éxito, abre `TablaVisualizadora`.

### 3. `TablaVisualizadora.java`
* **Rol**: Principalmente **Vista** y **Controlador** (con fuerte interacción con el Modelo).
* **Descripción**: La ventana principal de la aplicación después del inicio de sesión. Permite seleccionar bases de datos y tablas, visualizar datos en una `JTable`, y realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) directamente sobre los registros. Actúa como un *hub* para acceder a otras funcionalidades de la aplicación.
* **Interacción**:
    * **Modelo**: Ejecuta consultas SQL para cargar, agregar, modificar y eliminar datos.
    * **Controlador de otras Vistas**: Lanza y coordina con `EditorSQLPanel`, `AdminGUI`, `ClaseRegistros`, `ClaseGrafica`, `ClaseInforme`, `ImportadorSQLDinamico`, `MySQLDirectDumperApp`, `Respaldo` y `RichTextEditorApp`.

### 4. `EditorSQLPanel.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Un editor de consultas SQL avanzado con resaltado de sintaxis, historial de comandos, y visualización de resultados en una tabla. Permite seleccionar bases de datos y tablas para facilitar la escritura de consultas.
* **Interacción**:
    * **Modelo**: Ejecuta directamente las consultas SQL.
    * **Controlador (indirecto)**: Se integra con `AsistenteIA` para obtener ayuda en las consultas.

### 5. `AsistenteIA.java`
* **Rol**: **Modelo**.
* **Descripción**: Proporciona asistencia inteligente para consultas SQL mediante la integración con la API de OpenAI.
* **Interacción**: Recibe consultas de `EditorSQLPanel`, las envía a la API de IA y devuelve las explicaciones o sugerencias.

### 6. `AdminGUI.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Interfaz para la administración de usuarios y privilegios en MySQL. Permite crear, eliminar usuarios y otorgar/revocar permisos sobre bases de datos y tablas.
* **Interacción**:
    * **Modelo**: Genera y ejecuta sentencias SQL de administración (`CREATE USER`, `DROP USER`, `GRANT`, `REVOKE`).

### 7. `ClaseRegistros.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Visualizador de metadatos de bases de datos y tablas MySQL. Muestra información como ID, nombre, número de tablas/columnas/filas, etc., y permite la exportación a varios formatos.
* **Interacción**:
    * **Modelo**: Consulta metadatos de MySQL.
    * **Controlador**: Invoca a `ClaseInforme` para la exportación.

### 8. `ClaseInforme.java`
* **Rol**: **Modelo**.
* **Descripción**: Gestiona la exportación de datos de tablas MySQL a diversos formatos de archivo como Excel (XLSX), PDF, Word (DOCX) y SQL.
* **Interacción**: Recibe datos y genera archivos utilizando librerías externas (Apache POI, iText PDF).

### 9. `ClaseGrafica.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Genera gráficas de barras a partir de datos numéricos de una tabla MySQL. Permite seleccionar la base de datos, tabla y columnas a graficar.
* **Interacción**:
    * **Modelo**: Obtiene datos de MySQL para la gráfica.
    * **Vista**: Utiliza JFreeChart para mostrar la visualización.

### 10. `ImportadorSQLDinamico.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Proporciona una interfaz para importar archivos SQL a una base de datos MySQL, mostrando el progreso de la importación.
* **Interacción**:
    * **Modelo**: Lee el archivo SQL y ejecuta sus sentencias en la base de datos.

### 11. `MySQLDirectDumperApp.java`
* **Rol**: **Vista** y **Controlador**.
* **Descripción**: Herramienta para exportar esquemas y datos de bases de datos MySQL a un archivo SQL (`.sql`). Permite seleccionar bases de datos, tablas y opciones de exportación.
* **Interacción**:
    * **Modelo**: Se conecta a MySQL para generar y escribir el script SQL de exportación.

### 12. `Respaldo.java`
* **Rol**: **Vista** y **Controlador** (con lógica de Modelo para la ejecución de `mysqldump`).
* **Descripción**: Interfaz para realizar copias de seguridad de bases de datos MySQL utilizando el comando externo `mysqldump`. Muestra un log del proceso.
* **Interacción**:
    * **Modelo**: Ejecuta el comando `mysqldump` como un proceso externo.

### 13. `RichTextEditorApp.java`
* **Rol**: **Vista** y **Controlador** (con lógica de Modelo para la exportación).
* **Descripción**: Un editor de texto enriquecido independiente, implementado en JavaFX, que permite crear, abrir, guardar y exportar documentos a formatos como PDF y DOCX.
* **Interacción**:
    * **Modelo**: Realiza la conversión y exportación del contenido HTML a otros formatos.

---

## Configuración y Ejecución

Para ejecutar esta aplicación, necesitarás lo siguiente:

### Requisitos Previos

* **Java Development Kit (JDK) 8 o superior**: Asegúrate de tener un JDK instalado y configurado correctamente.
* **MySQL Server**: La aplicación interactúa con una base de datos MySQL. Debes tener una instancia de MySQL en ejecución (local o remota).
* **MySQL Connector/J**: El controlador JDBC para MySQL. Este JAR debe estar en el classpath de tu proyecto.
* **Librerías externas (JARs)**:
    * `FlatLaf` (para el look and feel de la interfaz).
    * `RSyntaxTextArea` y `RTextScrollPane` (para el editor SQL).
    * `JFreeChart` (para la generación de gráficos).
    * `iText PDF` (para la exportación a PDF).
    * `Apache POI` (para la exportación a Excel y Word).
    * `Google Authenticator Library` (para 2FA en el login).
    * `OkHttp` y `Gson` (para la integración con la API de OpenAI en `AsistenteIA`).
    * `ZXing` (para la generación de códigos QR).

### Configuración de la Base de Datos

1.  **Credenciales de MySQL**:
    * Asegúrate de que las credenciales de conexión en las clases (`Login.java`, `EditorSQLPanel.java`, `AdminGUI.java`, etc.) sean correctas.
    * Por defecto, suelen ser `DB_URL = "jdbc:mysql://localhost:3306/mysql"`, `DB_USER = "root"`, `DB_PASS = "aAW_A-n2002-AZ-l920"`. **Se recomienda cambiar la contraseña y no usar el usuario 'root' con privilegios globales en un entorno de producción.**

2.  **Permisos de Usuario**:
    * El usuario de la base de datos (`DB_USER`) debe tener los permisos necesarios para realizar las operaciones (SELECT, INSERT, UPDATE, DELETE, CREATE USER, DROP USER, GRANT, REVOKE, SHOW DATABASES, SHOW TABLES, etc.).

3.  **Comando `mysqldump`**:
    * Para la funcionalidad de respaldo (`Respaldo.java`), asegúrate de que el comando `mysqldump` esté accesible desde la línea de comandos de tu sistema (es decir, que la ruta a `mysqldump.exe` o `mysqldump` esté en la variable de entorno PATH o que se especifique la ruta completa en el código).

### Configuración de la API de IA (AsistenteIA.java)

1.  **Clave de API de OpenAI**:
    * En la clase `AsistenteIA.java`, reemplaza el placeholder `API_KEY` con tu clave API real de OpenAI.
    * **¡Advertencia de Seguridad!**: Almacenar la clave API directamente en el código fuente no es una práctica segura para producción. Considera usar variables de entorno o un mecanismo de configuración más robusto.

### Compilación y Ejecución

1.  **IDE (IntelliJ IDEA, Eclipse, NetBeans)**:
    * Abre el proyecto en tu IDE preferido.
    * Asegúrate de que todas las librerías externas (.jar) estén añadidas como dependencias del proyecto (en el classpath).
    * Ejecuta la clase `Main.java`.

2.  **Línea de Comandos (compilación manual)**:
    * Navega a la raíz del proyecto en tu terminal.
    * Compila los archivos Java:
        ```bash
        javac -cp "ruta/a/mysql-connector-j.jar:ruta/a/flatlaf.jar:ruta/a/rsyntaxtextarea.jar:..." proyecto/pkgfinal/*.java
        ```
        (Asegúrate de incluir todos los JARs necesarios en el classpath).
    * Ejecuta la aplicación:
        ```bash
        java -cp ".:ruta/a/mysql-connector-j.jar:ruta/a/flatlaf.jar:..." proyecto.pkgfinal.Main
        ```
        (En Windows, usa `;` en lugar de `:` para separar las rutas del classpath).

### Notas Adicionales

* **Seguridad**: La aplicación, en su estado actual, utiliza credenciales de base de datos directamente en el código. Para entornos de producción, se recomienda encarecidamente implementar un sistema de gestión de configuraciones seguro para las credenciales.
* **Error Handling**: La aplicación incluye manejo básico de errores, pero se puede mejorar para proporcionar una experiencia de usuario más robusta y mensajes de error más detallados.
* **Rendimiento**: Para bases de datos muy grandes, algunas operaciones (como la carga de tablas completas o la exportación) pueden ser lentas. Se podrían considerar optimizaciones de rendimiento y paginación.
* **UI/UX**: Aunque funcional, la interfaz de usuario puede beneficiarse de mejoras en la experiencia de usuario y el diseño visual.
