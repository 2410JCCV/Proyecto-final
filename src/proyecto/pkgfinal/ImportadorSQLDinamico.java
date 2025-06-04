package proyecto.pkgfinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector; // Para JComboBox de bases de datos
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference; // Para pasar valores de vuelta desde lambdas
import java.lang.reflect.InvocationTargetException; // Necesario para invokeAndWait excepciones
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Juan Carlos Carrillo Viveros (217o02237) y Marlleli Luna Zamora (217o00552)
 * `ImportadorSQLDinamico` es una clase que extiende `JFrame` y proporciona una interfaz gráfica para importar archivos SQL a una base de datos MySQL.
 *
 * Funciones clave y componentes principales:
 *
 * - `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASS`: Variables para configurar la conexión a la base de datos MySQL.
 * - `campoRutaArchivo`: Un `JTextField` que muestra la ruta del archivo SQL seleccionado.
 * - `logArea`: Un `JTextArea` para mostrar el progreso y los mensajes de error durante la importación.
 * - `btnSeleccionarArchivo`: Un `JButton` para abrir un selector de archivos y elegir el archivo `.sql`.
 * - `btnImportar`: Un `JButton` que inicia el proceso de importación del archivo SQL.
 *
 * Métodos importantes:
 *
 * - `importarSQL(File archivoSQL)`: Método central que lee el archivo SQL y ejecuta cada comando.
 * - `getConnection(String dbName)`: Establece y devuelve una conexión a la base de datos especificada.
 * - `getAvailableDatabases()`: Recupera una lista de bases de datos disponibles en el servidor MySQL.
 * - `selectDatabaseForCommand(String defaultDb)`: Muestra un cuadro de diálogo para que el usuario seleccione una base de datos.
 * - `abbreviate(String str, int maxWidth)`: Función auxiliar para abreviar cadenas largas para el log.
 * - `log(String mensaje)`: Añade mensajes al `logArea` de forma segura para Swing.
 */


public class ImportadorSQLDinamico extends JFrame {

    // --- CONFIGURACIÓN DE CONEXIÓN A MYSQL (AJUSTA ESTO) ---
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_USER = "root";
    // ¡¡¡ CAMBIA ESTO por tu contraseña REAL de MySQL. Si no tienes, déjalo como "" !!!
    private static final String DB_PASS = "aAWV1c10YuHTQkmp";
    // --- BASE DE DATOS POR DEFECTO SI EL SCRIPT NO ESPECIFICA NINGUNA ---
    private static final String DEFAULT_DB_NAME = "default_import_db";
    // --- FIN CONFIGURACIÓN ---

    private JTextArea logArea;
    private JButton btnSeleccionarArchivo;
    private JButton btnImportar;
    private JFileChooser fileChooser;
    private File archivoSQL;
    private Connection currentConnection; // Variable de instancia
    private JLabel lblArchivo;
    private JLabel lblCurrentDb;

    public ImportadorSQLDinamico() {
        setTitle("Importador SQL Dinámico (Interpreta Script)");
        setSize(750, 450);
       
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- INICIALIZACIÓN DE COMPONENTES DE LA UI ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logArea);
        add(scrollLog, BorderLayout.CENTER);

        lblArchivo = new JLabel("Archivo seleccionado: Ninguno");
        lblCurrentDb = new JLabel("Conectado a: Inicializando...");

        // Establecer una conexión inicial
        establecerConexionInicial();

        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 10, 10));

        btnSeleccionarArchivo = new JButton("Seleccionar archivo SQL");
        btnSeleccionarArchivo.addActionListener(e -> {
            seleccionarArchivo();
            if (archivoSQL != null) {
                lblArchivo.setText("Archivo seleccionado: " + archivoSQL.getName());
                btnImportar.setEnabled(true);
            } else {
                lblArchivo.setText("Archivo seleccionado: Ninguno");
                btnImportar.setEnabled(false);
            }
        });
        panelSuperior.add(btnSeleccionarArchivo);

        panelSuperior.add(lblArchivo);

        lblCurrentDb.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelSuperior.add(lblCurrentDb);
        panelSuperior.add(new JLabel(""));

        add(panelSuperior, BorderLayout.NORTH);

        btnImportar = new JButton("Importar SQL");
        btnImportar.addActionListener(e -> importarSQL());
        btnImportar.setEnabled(false);
        add(btnImportar, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".sql") || f.isDirectory();
            }
            public String getDescription() {
                return "Archivos SQL (*.sql)";
            }
        });
    }

    /**
     * Establece una conexión inicial a MySQL sin una base de datos específica.
     * Esta conexión se usará para ejecutar comandos que cambian de DB o crean DBs.
     */
    private void establecerConexionInicial() {
        try {
            String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&serverTimezone=UTC";
            currentConnection = DriverManager.getConnection(url, DB_USER, DB_PASS);
            log("Conexión inicial exitosa a MySQL.");
            SwingUtilities.invokeLater(() -> lblCurrentDb.setText("Conectado a: Ninguna DB específica (inicial)"));
        } catch (SQLException e) {
            log("Error crítico al conectar a MySQL: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "No se pudo conectar a MySQL. Verifica que el servicio esté corriendo y las credenciales sean correctas.\n" +
                "Detalles: " + e.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void seleccionarArchivo() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            archivoSQL = fileChooser.getSelectedFile();
            log("Archivo SQL seleccionado: " + archivoSQL.getAbsolutePath());
        } else {
            archivoSQL = null;
        }
    }

    /**
     * Procesa y ejecuta el archivo SQL, manejando los comandos USE y CREATE DATABASE.
     */
    private void importarSQL() {
        if (archivoSQL == null || !archivoSQL.exists()) {
            log("Error: No se ha seleccionado un archivo SQL válido.");
            return;
        }

        // Utilizaremos un hilo para la importación para no bloquear la UI
        new Thread(() -> {
            // Asegurarse de que la conexión inicial esté activa
            try {
                if (currentConnection == null || currentConnection.isClosed()) {
                    establecerConexionInicial();
                    if (currentConnection == null || currentConnection.isClosed()) {
                         throw new SQLException("No se pudo establecer la conexión inicial con MySQL.");
                    }
                }
                currentConnection.setAutoCommit(true); // Cada sentencia se comete automáticamente

                // Abrir el archivo y leerlo línea por línea
                try (BufferedReader reader = new BufferedReader(new FileReader(archivoSQL))) {
                    StringBuilder currentCommand = new StringBuilder();
                    String line;
                    AtomicInteger successfulCommands = new AtomicInteger(0); // Contadores atómicos
                    AtomicInteger failedCommands = new AtomicInteger(0);     // Contadores atómicos
                    
                    long totalLines = Files.lines(archivoSQL.toPath()).count();
                    long processedLines = 0;

                    JProgressBar progressBar = new JProgressBar(0, (int) totalLines);
                    progressBar.setStringPainted(true);
                    progressBar.setValue(0);
                    JDialog progressDialog = new JDialog(this, "Importando SQL...", true);
                    progressDialog.setLayout(new BorderLayout(10, 10));
                    progressDialog.add(new JLabel("Procesando script SQL:"), BorderLayout.NORTH);
                    progressDialog.add(progressBar, BorderLayout.CENTER);
                    progressDialog.add(new JLabel("Progreso: "), BorderLayout.WEST);
                    progressDialog.add(new JLabel(" "), BorderLayout.EAST);
                    progressDialog.setSize(350, 120);
                    progressDialog.setLocationRelativeTo(this);
                    SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));


                    while ((line = reader.readLine()) != null) {
                        final long currentProcessedLine = ++processedLines;
                        SwingUtilities.invokeLater(() -> progressBar.setValue((int) currentProcessedLine));

                        line = line.trim();
                        // Ignorar líneas vacías y comentarios de una sola línea (MySQL y Bash style)
                        if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                            continue;
                        }

                        currentCommand.append(line).append("\n");

                        // Detectar el final de un comando (;) que no esté dentro de un DELIMITER block
                        if (line.endsWith(";") && !currentCommand.toString().toUpperCase().contains("DELIMITER")) {
                            String sql = currentCommand.toString().trim();
                            sql = sql.substring(0, sql.length() - 1); // Remover el punto y coma final

                            if (sql.isEmpty()) {
                                currentCommand.setLength(0);
                                continue;
                            }

                            // Llamar al nuevo método para procesar el comando
                            processSingleCommand(sql, successfulCommands, failedCommands);
                            currentCommand.setLength(0); // Limpiar el StringBuilder para el próximo comando
                        }
                    }

                    // Después de leer todo el archivo, ejecutar cualquier comando restante
                    if (currentCommand.length() > 0) {
                        String sql = currentCommand.toString().trim();
                         if (!sql.isEmpty()) {
                            processSingleCommand(sql, successfulCommands, failedCommands);
                        }
                    }

                    // Mostrar resumen final al usuario y actualizar UI
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        log("Importación finalizada. Éxitos: " + successfulCommands.get() + ", Fallidos: " + failedCommands.get());
                        JOptionPane.showMessageDialog(this,
                            "Importación completada.\nComandos exitosos: " + successfulCommands.get() +
                            "\nComandos fallidos: " + failedCommands.get(),
                            "Resultado de Importación", JOptionPane.INFORMATION_MESSAGE);
                    });

                } // Fin try-with-resources para BufferedReader
            } catch (IOException e) {
                log("Error al leer el archivo SQL: " + e.getMessage());
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error al leer el archivo SQL: " + e.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE));
            } catch (SQLException e) {
                log("Error de conexión o SQL fatal durante la importación: " + e.getMessage());
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error fatal de conexión/SQL durante la importación: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE));
            } finally {
                // Asegurarse de cerrar la conexión activa.
                if (currentConnection != null) {
                    try {
                        if (!currentConnection.isClosed()) {
                            currentConnection.close();
                            log("Conexión a MySQL cerrada.");
                        }
                    } catch (SQLException e) {
                        log("Error al cerrar la conexión final: " + e.getMessage());
                    }
                }
            }
        }).start(); // Iniciar el hilo de importación
    }

    /**
     * Procesa un único comando SQL, manejando comandos especiales como USE, CREATE DATABASE, DROP DATABASE,
     * y solicita interacción del usuario cuando es necesario.
     * @param sql El comando SQL a procesar.
     * @param successfulCommands Contador de comandos exitosos.
     * @param failedCommands Contador de comandos fallidos.
     */
    private void processSingleCommand(String sql, AtomicInteger successfulCommands, AtomicInteger failedCommands) {
        // Patrones de regex para detectar comandos SQL
        Pattern usePattern = Pattern.compile("^\\s*USE\\s+`?([a-zA-Z0-9_]+)`?;?\\s*$", Pattern.CASE_INSENSITIVE);
        Pattern createDbPattern = Pattern.compile("^\\s*CREATE\\s+DATABASE(?:\\s+IF\\s+NOT\\s+EXISTS)?\\s+`?([a-zA-Z0-9_]+)`?;?\\s*$", Pattern.CASE_INSENSITIVE);
        // NOTA: Para CREATE TABLE, Pattern.DOTALL es importante si el comando abarca varias líneas con paréntesis.
        Pattern createTablePattern = Pattern.compile("^\\s*CREATE\\s+TABLE(?:\\s+IF\\s+NOT\\s+EXISTS)?\\s+`?([a-zA-Z0-9_]+)`?\\s*\\(", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        // Captura el tipo de DML/DDL para mensajes
        Pattern dataManipulationPattern = Pattern.compile("^\\s*(INSERT\\s+INTO|UPDATE|DELETE\\s+FROM|SELECT|DROP\\s+TABLE)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern dropDatabasePattern = Pattern.compile("^\\s*DROP\\s+DATABASE(?:\\s+IF\\s+EXISTS)?\\s+`?([a-zA-Z0-9_]+)`?;?\\s*$", Pattern.CASE_INSENSITIVE);

        Matcher useMatcher = usePattern.matcher(sql);
        Matcher createDbMatcher = createDbPattern.matcher(sql);
        Matcher createTableMatcher = createTablePattern.matcher(sql);
        Matcher dataManipulationMatcher = dataManipulationPattern.matcher(sql);
        Matcher dropDatabaseMatcher = dropDatabasePattern.matcher(sql);

        boolean commandHandled = false;

        try {
            if (createDbMatcher.find()) {
                // CREATE DATABASE: Se ejecuta directamente.
                String dbName = createDbMatcher.group(1);
                log("Comando 'CREATE DATABASE' detectado: " + dbName);
                try (Statement stmt = currentConnection.createStatement()) {
                    stmt.execute(sql);
                    log("Base de datos '" + dbName + "' creada/asegurada.");
                    successfulCommands.incrementAndGet();
                } catch (SQLException e) {
                    failedCommands.incrementAndGet();
                    log("Error al crear base de datos '" + dbName + "': " + e.getMessage());
                    log("Comando fallido (CREATE DATABASE): " + sql);
                }
                commandHandled = true;
            } else if (dropDatabaseMatcher.find()) {
                // DROP DATABASE: Pedir confirmación y ejecutar.
                String dbName = dropDatabaseMatcher.group(1);
                // Usamos AtomicReference para capturar el resultado del diálogo de SwingUtilities.invokeAndWait
                AtomicReference<Boolean> confirmedRef = new AtomicReference<>();
                SwingUtilities.invokeAndWait(() -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "¡ADVERTENCIA! Estás a punto de ELIMINAR la base de datos '" + dbName + "'.\n" +
                        "Comando: " + abbreviate(sql, 100) + "\n¿Estás seguro de continuar?",
                        "Confirmar Eliminación de Base de Datos", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    confirmedRef.set(confirm == JOptionPane.YES_OPTION);
                });
                
                if (confirmedRef.get()) { // Obtener el resultado del diálogo
                    try (Statement stmt = currentConnection.createStatement()) {
                        stmt.execute(sql);
                        log("Base de datos '" + dbName + "' eliminada exitosamente.");
                        successfulCommands.incrementAndGet();
                        // Si la DB actual era la eliminada, resetear la conexión
                        if (currentConnection.getCatalog() != null && currentConnection.getCatalog().equalsIgnoreCase(dbName)) {
                            currentConnection.close();
                            establecerConexionInicial(); // Reconectar a sin DB específica
                        }
                    } catch (SQLException e) {
                        failedCommands.incrementAndGet();
                        log("Error al eliminar base de datos '" + dbName + "': " + e.getMessage());
                        log("Comando fallido (DROP DATABASE): " + sql);
                    }
                } else {
                    log("Eliminación de base de datos '" + dbName + "' cancelada por el usuario.");
                    failedCommands.incrementAndGet();
                }
                commandHandled = true;
            } else if (useMatcher.find()) {
                // USE: Preguntar si el usuario quiere cambiar la conexión.
                String detectedDbName = useMatcher.group(1); // Esta variable es local, pero su valor se copia.
                // Usamos AtomicReference para capturar el resultado del diálogo de SwingUtilities.invokeAndWait
                AtomicReference<Boolean> confirmedRef = new AtomicReference<>();
                SwingUtilities.invokeAndWait(() -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "El script contiene 'USE `" + detectedDbName + "`'.\n" +
                        "¿Deseas conectar a esta base de datos para los comandos subsiguientes?",
                        "Cambiar Base de Datos Activa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    confirmedRef.set(confirm == JOptionPane.YES_OPTION);
                });
                
                if (confirmedRef.get()) { // Obtener el resultado del diálogo
                    try {
                        String currentCatalog = currentConnection.getCatalog();
                        if (currentCatalog == null || !currentCatalog.equalsIgnoreCase(detectedDbName)) {
                            if (currentConnection != null && !currentConnection.isClosed()) {
                                currentConnection.close(); // Cerrar la conexión anterior
                            }
                            // Crea una nueva conexión y asigna a currentConnection
                            Connection tempConnection = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + detectedDbName + "?useSSL=false&serverTimezone=UTC", DB_USER, DB_PASS);
                            currentConnection = tempConnection; // Se reasigna la variable de instancia
                            log("Conexión cambiada a base de datos: " + detectedDbName);
                            // La variable local para la lambda debe ser "effectively final"
                            final String nameForLambda = detectedDbName;
                            SwingUtilities.invokeLater(() -> lblCurrentDb.setText("Conectado a: " + nameForLambda));
                            successfulCommands.incrementAndGet();
                        } else {
                            log("Ya conectado a la base de datos: " + detectedDbName);
                            successfulCommands.incrementAndGet();
                        }
                    } catch (SQLException e) {
                        failedCommands.incrementAndGet();
                        log("Error al cambiar conexión a '" + detectedDbName + "': " + e.getMessage());
                        log("Comando fallido (USE): " + sql);
                    }
                } else {
                    log("Cambio a base de datos '" + detectedDbName + "' cancelado por el usuario.");
                    failedCommands.incrementAndGet();
                }
                commandHandled = true;
            } else if (createTableMatcher.find() || dataManipulationMatcher.find()) {
                // CREATE TABLE, INSERT, UPDATE, DELETE, SELECT, DROP TABLE: Requieren una DB activa
                // Determinar la descripción de la acción. NOTA: Los Matchers ya avanzaron, por lo que
                // necesitaríamos reiniciarlos o usar Pattern.matches si solo queremos un único intento.
                // Para simplificar y dado que solo necesitamos la descripción, podemos volver a evaluar los matchers.
                // Es importante si se usa find() que el matcher se resetee o se use matches() para una coincidencia completa desde el inicio.
                // Aquí, asumo que solo queremos la descripción para el log/diálogo.
                
                String actionDescription = "Comando SQL"; // Valor por defecto
                
                // Re-inicializar los matchers si vamos a usar .find() o .matches() para asegurar el estado
                // Es mejor usar .matches() para coincidencia de cadena completa si la regex cubre todo el comando.
                // Si el comando es un prefijo, .find() podría ser más flexible pero requiere manejo de estado.
                // Aquí, dado que los patrones son para el inicio del comando, matches() podría ser más adecuado.
                
                // Reiniciar los matchers para determinar la descripción si ya se usaron
                createTableMatcher.reset(); 
                dataManipulationMatcher.reset(); 

                if (createTableMatcher.matches()) { // Usar matches() para coincidencia completa del patrón
                    actionDescription = "Creación de Tabla";
                } else if (dataManipulationMatcher.matches()) { // Usar matches() aquí también
                    actionDescription = "Operación de Datos (" + dataManipulationMatcher.group(1).toUpperCase() + ")";
                }

                String currentCatalog = null;
                try {
                    currentCatalog = currentConnection.getCatalog();
                } catch (SQLException ex) {
                    log("Error al obtener catálogo actual: " + ex.getMessage());
                    // Decide si quieres fallar aquí o continuar con currentCatalog = null
                }

                // *** USAR AtomicReference PARA OBTENER EL RESULTADO DEL DIÁLOGO ***
                AtomicReference<String> selectedDbRef = new AtomicReference<>();
                
                // *** CORRECCIÓN CLAVE AQUÍ: Hacer copias finales para la lambda ***
                final String finalActionDescription = actionDescription;
                final String finalCurrentCatalog = currentCatalog;

                SwingUtilities.invokeAndWait(() -> {
                    String db = showDatabaseSelectionDialog(sql, finalActionDescription, finalCurrentCatalog);
                    selectedDbRef.set(db); // Establecer el valor en el AtomicReference
                });
                String dbForCommand = selectedDbRef.get(); // Obtener el valor después de que el diálogo haya terminado

                if (dbForCommand != null) {
                    // Si se seleccionó una DB, intentar cambiar la conexión y ejecutar el comando
                    try {
                        // Solo cambiar la conexión si no estamos ya conectados a la DB correcta
                        if (currentConnection == null || currentConnection.isClosed() || 
                            (currentConnection.getCatalog() == null || !currentConnection.getCatalog().equalsIgnoreCase(dbForCommand))) {
                            
                            if (currentConnection != null && !currentConnection.isClosed()) {
                                currentConnection.close();
                            }
                            Connection tempConnection = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + dbForCommand + "?useSSL=false&serverTimezone=UTC", DB_USER, DB_PASS);
                            currentConnection = tempConnection;
                            log("Conexión cambiada a base de datos: " + dbForCommand);
                            final String nameForLambda = dbForCommand;
                            SwingUtilities.invokeLater(() -> lblCurrentDb.setText("Conectado a: " + nameForLambda));
                        } else {
                            log("Ya conectado a la base de datos: " + dbForCommand);
                        }

                        // Manejo especial para DELETE sin WHERE para confirmación
                        if (sql.toUpperCase().startsWith("DELETE FROM") && !sql.toUpperCase().contains("WHERE")) {
                            AtomicReference<Boolean> deleteConfirmedRef = new AtomicReference<>();
                            SwingUtilities.invokeAndWait(() -> {
                                int confirm = JOptionPane.showConfirmDialog(this,
                                    "¡ADVERTENCIA! Este comando eliminará TODOS los registros de la tabla en '" + dbForCommand + "'.\n" +
                                    "Comando: " + abbreviate(sql, 100) + "\n¿Estás seguro de continuar?",
                                    "Confirmar Eliminación Masiva", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                deleteConfirmedRef.set(confirm == JOptionPane.YES_OPTION);
                            });
                            
                            if (deleteConfirmedRef.get()) {
                                performSqlExecution(sql, finalActionDescription, successfulCommands, failedCommands);
                            } else {
                                log("Eliminación masiva cancelada por el usuario.");
                                failedCommands.incrementAndGet();
                            }
                        } else {
                            performSqlExecution(sql, finalActionDescription, successfulCommands, failedCommands);
                        }

                    } catch (SQLException e) {
                        failedCommands.incrementAndGet();
                        log("Error al establecer/cambiar conexión para " + finalActionDescription + " en DB '" + dbForCommand + "': " + e.getMessage());
                        log("Comando fallido: " + sql);
                    }
                } else {
                    log(finalActionDescription + " cancelada por el usuario (no se seleccionó DB).");
                    failedCommands.incrementAndGet();
                }
                commandHandled = true;
            }
            
            if (!commandHandled) { // Si no fue ninguno de los comandos especiales o ya manejados
                performSqlExecution(sql, "Comando General", successfulCommands, failedCommands);
            }
        } catch (InterruptedException | InvocationTargetException e) {
            log("Error al interactuar con la UI para el comando: " + e.getMessage());
            e.printStackTrace(); // Para depuración
            failedCommands.incrementAndGet();
        }
    }

    /**
     * Ejecuta una sentencia SQL simple y actualiza los contadores.
     * @param sql El comando SQL a ejecutar.
     * @param actionDescription Descripción de la acción para el log.
     * @param successfulCommands Contador de éxitos.
     * @param failedCommands Contador de fallos.
     */
    private void performSqlExecution(String sql, String actionDescription, AtomicInteger successfulCommands, AtomicInteger failedCommands) {
        try (Statement stmt = currentConnection.createStatement()) {
            stmt.execute(sql);
            successfulCommands.incrementAndGet();
            log("Éxito (" + actionDescription + "): " + abbreviate(sql, 70));
        } catch (SQLException e) {
            failedCommands.incrementAndGet();
            log("Error (" + actionDescription + "): " + e.getMessage());
            log("Comando fallido: " + abbreviate(sql, 70));
        }
    }


    /**
     * Muestra un diálogo para que el usuario seleccione una base de datos de las existentes.
     * @param sql El comando SQL que requiere una selección de DB.
     * @param action La acción que se va a realizar (ej. "Creación de Tabla", "Operación de Datos").
     * @param currentCatalog La base de datos actualmente conectada (puede ser null).
     * @return El nombre de la base de datos seleccionada, o null si el usuario cancela.
     */
    private String showDatabaseSelectionDialog(String sql, String action, String currentCatalog) {
        List<String> databases = new ArrayList<>();
        // Obtener la lista de bases de datos desde MySQL
        try (Statement stmt = currentConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
            while (rs.next()) {
                databases.add(rs.getString(1));
            }
        } catch (SQLException e) {
            log("Error al listar bases de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "No se pudieron listar las bases de datos.\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (databases.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron bases de datos en MySQL.\nPor favor, crea una primero.", "No hay Bases de Datos", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        JComboBox<String> dbComboBox = new JComboBox<>(new Vector<>(databases));
        if (currentCatalog != null && databases.contains(currentCatalog)) {
            dbComboBox.setSelectedItem(currentCatalog);
        } else if (databases.contains(DEFAULT_DB_NAME)) {
            dbComboBox.setSelectedItem(DEFAULT_DB_NAME);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("El siguiente comando requiere una base de datos activa para '" + action + "':"));
        panel.add(new JLabel("Comando: " + abbreviate(sql, 100)));
        panel.add(new JLabel("Selecciona la base de datos a utilizar:"));
        panel.add(dbComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Seleccionar Base de Datos para Comando",
                                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return (String) dbComboBox.getSelectedItem();
        } else {
            return null;
        }
    }

    /**
     * Abbrevia una cadena para mostrarla en el log.
     */
    private String abbreviate(String str, int maxWidth) {
        if (str == null || str.length() <= maxWidth) {
            return str;
        }
        return str.substring(0, Math.min(str.length(), maxWidth - 3)) + "...";
    }

    /**
     * Añade un mensaje al área de log.
     * Se asegura de que la actualización de la UI se haga en el Event Dispatch Thread (EDT).
     */
    private void log(String mensaje) {
        if (logArea != null) {
            // Usa SwingUtilities.invokeLater para asegurar que la actualización de la UI se haga en el EDT
            SwingUtilities.invokeLater(() -> {
                logArea.append(mensaje + "\n");
                // Mueve el cursor al final para ver los mensajes más recientes
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        } else {
            System.err.println("ERROR: logArea es null. Mensaje: " + mensaje);
        }
    }
}