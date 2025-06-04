package proyecto.pkgfinal;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL; // Aún se usa para el QR, si se genera.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase `Respaldo` es una aplicación de escritorio Swing que permite al usuario
 * realizar copias de seguridad (dumps) de bases de datos MySQL utilizando el comando `mysqldump`.
 * También incluye funcionalidades de inicio de sesión simuladas y una interfaz para seleccionar
 * bases de datos para exportar.
 *
 * Funcionalidades clave:
 * - **Configuración de Conexión**: Define los campos `txtLoginHost`, `txtLoginPort`, `txtLoginUser`,
 * `pwdLoginPass` para que el usuario configure la conexión a MySQL.
 * - **Selección de Bases de Datos**: `dbComboBox` para elegir una base de datos específica o la opción "TODAS".
 * - **Acciones de Respaldo**:
 * - `backupButton`: Inicia el proceso de exportación.
 * - `exportSelectedDatabase()`: Exporta la base de datos seleccionada.
 * - `exportAllDatabases()`: Exporta todas las bases de datos no del sistema.
 * - **Ejecución de `mysqldump`**: Construye y ejecuta el comando `mysqldump` a través de `ProcessBuilder`.
 * - **Registro de Eventos**: `textAreaLog` muestra el progreso y los errores de las operaciones.
 * - **Interacción con Google Authenticator**: Incluye un método `showQRCode()` para generar y mostrar
 * códigos QR, lo que sugiere una posible integración futura o incompleta con 2FA (aunque no directamente
 * ligada a la funcionalidad de respaldo en este fragmento).
 */

public class Respaldo extends JFrame {

    // --- Componentes de la Interfaz Gráfica (Login y Respaldo Comparten la UI) ---
    private JTextField txtLoginHost;      // Usado para el login (simula host)
    private JTextField txtLoginPort;      // Usado para el login (simula puerto)
    private JTextField txtLoginUser;      // **Campo de usuario para login de la aplicación**
    private JPasswordField txtLoginPassword; // **Campo de contraseña para login de la aplicación**
    private JButton btnLoginConnect;     // **Botón para iniciar el login de la aplicación**

    private JList<String> listBasesDeDatos;
    private DefaultListModel<String> dbListModel;
    private JTextField txtRutaDestinoLocal;
    private JButton btnSeleccionarCarpeta;
    private JButton btnExportar;
    private JTextArea textAreaLog;

    // --- Variables de Lógica de la Aplicación ---
    // private Connection authConnection; // No necesitamos mantener una conexión de autenticación persistente.
    private Connection mysqlBackupConnection; // Conexión a MySQL para los respaldos

    // --- Credenciales para la conexión de autenticación (¡Configúralas aquí!) ---
    private static final String AUTH_DB_URL = "jdbc:mysql://localhost:3306/escuela2";
    private static final String AUTH_DB_USER = "root"; // Usuario de MySQL con acceso a la BD escuela2
    // ¡ADVERTENCIA! Cambia "aAWV1c10YuHTQkmp" por tu contraseña real de root de MySQL para la base de datos escuela2
    private static final String AUTH_DB_PASS = "aAWV1c10YuHTQkmp";

    // --- Credenciales para la conexión de respaldo a MySQL (¡HARDCODEADAS, OJO!) ---
    // Estas credenciales se usarán *después* del login exitoso para conectar a MySQL y listar/respaldar las BDs.
    private static final String BACKUP_MYSQL_HOST = "localhost";
    private static final String BACKUP_MYSQL_PORT = "3306";
    private static final String BACKUP_MYSQL_USER = "root"; // Usuario con permisos para mysqldump
    // ¡ADVERTENCIA! Cambia "tu_contraseña_de_root_de_mysql" por la contraseña del usuario root (o el usuario de respaldo) de MySQL
    private static final String BACKUP_MYSQL_PASSWORD = "aAWV1c10YuHTQkmp"; 

    private String localBackupPath = "";

    public Respaldo() {
        // Se eliminó la configuración de FlatLaf y los estilos personalizados.
        // Se mantendrá el Look and Feel por defecto del sistema o Java.

        setTitle("Herramienta de Respaldo MySQL Flexible");
        setSize(700, 600); // Tamaño para ajustarse al estilo por defecto
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
        // Inicialmente, deshabilitar los componentes de respaldo hasta el login exitoso
        setBackupComponentsEnabled(false);
    }

    private void initComponents() {
        // --- Campos de Login (que visualmente son los campos de conexión) ---
        txtLoginHost = new JTextField("localhost");
        txtLoginPort = new JTextField("3306");
        txtLoginUser = new JTextField("luis"); // Sugerencia de usuario de login
        txtLoginPassword = new JPasswordField("123456"); // Sugerencia de contraseña de login
        btnLoginConnect = new JButton("Conectar a MySQL"); // Texto original del botón de la imagen

        // --- Componentes de Respaldo (inicialmente deshabilitados) ---
        dbListModel = new DefaultListModel<>();
        listBasesDeDatos = new JList<>(dbListModel);

        txtRutaDestinoLocal = new JTextField();
        txtRutaDestinoLocal.setEditable(false);

        btnSeleccionarCarpeta = new JButton("Seleccionar Carpeta Destino");
        btnExportar = new JButton("Exportar Bases de Datos Seleccionadas"); // Texto original del botón de la imagen
                                                                           // Aunque exportará todas, el texto es el de la UI.
        
        textAreaLog = new JTextArea();
        textAreaLog.setEditable(false);
        textAreaLog.setLineWrap(true);
        textAreaLog.setWrapStyleWord(true);

        // --- Acciones de los botones ---
        btnLoginConnect.addActionListener(e -> verifyLoginCredentials()); // El botón "Conectar a MySQL" ahora hace el login
        txtLoginPassword.addActionListener(e -> verifyLoginCredentials()); // Enter en el campo de contraseña
        btnSeleccionarCarpeta.addActionListener(e -> selectLocalBackupFolder());
        btnExportar.addActionListener(e -> exportAllDatabases());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Panel de Login (visualemnte como "Configuración de Conexión a MySQL")
        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Configuración de Conexión a MySQL")); // Texto original de la imagen
        loginPanel.add(new JLabel("Host:"));
        loginPanel.add(txtLoginHost);
        loginPanel.add(new JLabel("Puerto:"));
        loginPanel.add(txtLoginPort);
        loginPanel.add(new JLabel("Usuario:"));
        loginPanel.add(txtLoginUser);
        loginPanel.add(new JLabel("Contraseña:"));
        loginPanel.add(txtLoginPassword);
        loginPanel.add(new JLabel(""));
        loginPanel.add(btnLoginConnect);

        // Panel Principal para el resto de la interfaz (deshabilitado hasta el login)
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        // Se quitó el BorderFactory.createEmptyBorder para que se vea como en la imagen.

        // Panel de Ruta de Destino
        JPanel destinationPanel = new JPanel(new BorderLayout(5, 5));
        destinationPanel.setBorder(BorderFactory.createTitledBorder("Carpeta de Destino de Respaldos"));
        destinationPanel.add(txtRutaDestinoLocal, BorderLayout.CENTER);
        destinationPanel.add(btnSeleccionarCarpeta, BorderLayout.EAST);

        // Panel de Bases de Datos Encontradas
        JPanel dbListPanel = new JPanel(new BorderLayout());
        dbListPanel.setBorder(BorderFactory.createTitledBorder("Bases de Datos de Usuario Encontradas"));
        dbListPanel.add(new JScrollPane(listBasesDeDatos), BorderLayout.CENTER);

        // Panel para el botón de Exportar
        JPanel exportButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportButtonPanel.add(btnExportar);

        // Área de Log
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Registro de Actividad"));
        logPanel.add(new JScrollPane(textAreaLog), BorderLayout.CENTER);

        // Ensamblar el panel principal de contenido
        mainContentPanel.add(destinationPanel, BorderLayout.NORTH);
        mainContentPanel.add(dbListPanel, BorderLayout.CENTER);
        mainContentPanel.add(exportButtonPanel, BorderLayout.SOUTH);

        // Añadir los paneles al JFrame
        add(loginPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
    }

    // --- Habilitar/Deshabilitar componentes después del login ---
    private void setLoginComponentsEnabled(boolean enabled) {
        txtLoginHost.setEnabled(enabled);
        txtLoginPort.setEnabled(enabled);
        txtLoginUser.setEnabled(enabled);
        txtLoginPassword.setEnabled(enabled);
        btnLoginConnect.setEnabled(enabled);
    }

    private void setBackupComponentsEnabled(boolean enabled) {
        listBasesDeDatos.setEnabled(enabled);
        txtRutaDestinoLocal.setEnabled(enabled);
        btnSeleccionarCarpeta.setEnabled(enabled);
        // El botón de exportar solo se habilita si hay conexión y ruta seleccionada
        btnExportar.setEnabled(enabled && mysqlBackupConnection != null && !localBackupPath.isEmpty());
        textAreaLog.setEnabled(enabled);
    }

    // --- LÓGICA DE AUTENTICACIÓN (LOGIN) ---
    private void verifyLoginCredentials() {
        String usuario = txtLoginUser.getText().trim();
        String contraseña = new String(txtLoginPassword.getPassword()).trim();

        if (usuario.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña para acceder a la herramienta.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        textAreaLog.append("Intentando autenticar usuario '" + usuario + "'...\n");
        try (Connection conn = DriverManager.getConnection(AUTH_DB_URL, AUTH_DB_USER, AUTH_DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE username=? AND password=?")) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, contraseña);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String secret = rs.getString("secret");
                if (secret != null && !secret.isEmpty()) {
                    String input = JOptionPane.showInputDialog(this, "Ingresa el código de 6 dígitos de Google Authenticator:");
                    if (input == null || input.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Debe ingresar un código válido para 2FA.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        int codigo = Integer.parseInt(input);
                        GoogleAuthenticator gAuth = new GoogleAuthenticator();
                        if (gAuth.authorize(secret, codigo)) {
                            JOptionPane.showMessageDialog(this, "✅ Autenticación exitosa", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            onLoginSuccess(); // Llama a la función para activar el panel de respaldo
                        } else {
                            JOptionPane.showMessageDialog(this, "❌ Código de Google Authenticator incorrecto.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "El código 2FA debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso. No se requiere 2FA para este usuario.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    onLoginSuccess(); // Llama a la función para activar el panel de respaldo
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al conectar con la base de datos de autenticación.\nDetalle: " + e.getMessage() + "\n" +
                "Verifica las credenciales de conexión para 'escuela2' en el código fuente.", 
                "Error Crítico de Conexión (Login)", JOptionPane.ERROR_MESSAGE);
            textAreaLog.append("Error en conexión de autenticación: " + e.getMessage() + "\n");
        }
    }

    private void onLoginSuccess() {
        textAreaLog.append("Login exitoso. Iniciando conexión automática a MySQL para respaldo...\n");
        setLoginComponentsEnabled(false); // Deshabilitar campos de login
        setBackupComponentsEnabled(true); // Habilitar componentes de respaldo
        
        // Conectar automáticamente a MySQL para respaldo con las credenciales predefinidas
        connectToMySQLForBackupAutomatically();
    }

    // --- LÓGICA DE CONEXIÓN Y RESPALDO DE MySQL ---
    private void connectToMySQLForBackupAutomatically() {
        String host = BACKUP_MYSQL_HOST;
        String port = BACKUP_MYSQL_PORT;
        String user = BACKUP_MYSQL_USER;
        String password = BACKUP_MYSQL_PASSWORD;
        String url = "jdbc:mysql://" + host + ":" + port + "/";

        textAreaLog.append("Conectando a MySQL para obtener bases de datos de respaldo (Host: " + host + ", User: " + user + ")...\n");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            mysqlBackupConnection = DriverManager.getConnection(url, user, password);
            textAreaLog.append("Conexión a MySQL para respaldo exitosa.\n");
            loadDatabasesForBackup();
            // Re-evaluar el estado del botón de exportar
            btnExportar.setEnabled(!localBackupPath.isEmpty());
        } catch (ClassNotFoundException ex) {
            textAreaLog.append("Error: Driver JDBC de MySQL no encontrado. Asegúrate de añadir el JAR a las librerías del proyecto.\n");
            JOptionPane.showMessageDialog(this, "Driver JDBC de MySQL no encontrado. Por favor, añádelo a las librerías del proyecto.", "Error de Conexión MySQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            textAreaLog.append("Error al conectar a MySQL para respaldo: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, 
                "Error al conectar a MySQL para respaldo: " + ex.getMessage() + "\n" +
                "Verifica las credenciales de respaldo (Host, Puerto, Usuario, Contraseña de MySQL) en el código fuente.", 
                "Error de Conexión MySQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadDatabasesForBackup() {
        dbListModel.clear();
        try (Statement stmt = mysqlBackupConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
            while (rs.next()) {
                String dbName = rs.getString(1);
                if (!dbName.equals("information_schema") && !dbName.equals("mysql") &&
                    !dbName.equals("performance_schema") && !dbName.equals("sys")) {
                    dbListModel.addElement(dbName);
                }
            }
            textAreaLog.append("Bases de datos de usuario cargadas para respaldo.\n");
        } catch (SQLException ex) {
            textAreaLog.append("Error al cargar bases de datos para respaldo: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "Error al cargar bases de datos para respaldo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void selectLocalBackupFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (!localBackupPath.isEmpty()) {
            File currentDir = new File(localBackupPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                fileChooser.setCurrentDirectory(currentDir);
            }
        }

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            localBackupPath = selectedFolder.getAbsolutePath();
            txtRutaDestinoLocal.setText(localBackupPath);
            textAreaLog.append("Carpeta de destino local seleccionada: " + localBackupPath + "\n");
            if (mysqlBackupConnection != null) {
                btnExportar.setEnabled(true);
            }
        }
    }

    private void exportAllDatabases() { 
        if (mysqlBackupConnection == null) {
            JOptionPane.showMessageDialog(this, "No hay conexión activa a MySQL para respaldo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (localBackupPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una carpeta de destino local para guardar los respaldos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> databasesToExport = new ArrayList<>();
        for (int i = 0; i < dbListModel.getSize(); i++) {
            databasesToExport.add(dbListModel.getElementAt(i));
        }
        
        if (databasesToExport.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron bases de datos de usuario para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File destFolder = new File(localBackupPath);
        if (!destFolder.exists()) {
            boolean created = destFolder.mkdirs();
            if (created) {
                textAreaLog.append("Carpeta de destino '" + localBackupPath + "' creada exitosamente.\n");
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo crear la carpeta de destino: " + localBackupPath + "\nAsegúrate de tener permisos de escritura.", "Error de Carpeta", JOptionPane.ERROR_MESSAGE);
                textAreaLog.append("Error: No se pudo crear la carpeta de destino: " + localBackupPath + "\n");
                return;
            }
        }
        if (!destFolder.isDirectory() || !destFolder.canWrite()) {
             JOptionPane.showMessageDialog(this, "Error: La ruta de destino no es una carpeta válida o no tengo permisos de escritura: " + localBackupPath, "Error de Carpeta", JOptionPane.ERROR_MESSAGE);
             textAreaLog.append("Error: La ruta de destino no es una carpeta válida o no tengo permisos de escritura: " + localBackupPath + "\n");
             return;
        }

        // Usar las credenciales hardcodeadas para el mysqldump
        String mysqlHost = BACKUP_MYSQL_HOST;
        String mysqlUser = BACKUP_MYSQL_USER;
        String mysqlPassword = BACKUP_MYSQL_PASSWORD;

        textAreaLog.append("\nIniciando proceso de exportación de TODAS las bases de datos...\n");

        for (String dbName : databasesToExport) {
            String outputPath = localBackupPath + File.separator + dbName + ".sql";
            String command;

            if (mysqlPassword.isEmpty()) {
                command = String.format("mysqldump -h %s -u %s %s", mysqlHost, mysqlUser, dbName);
            } else {
                command = String.format("mysqldump -h %s -u %s -p%s %s", mysqlHost, mysqlUser, mysqlPassword, dbName);
            }

            try {
                textAreaLog.append("Exportando '" + dbName + "' a " + outputPath + "...\n");

                ProcessBuilder pb;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    pb = new ProcessBuilder("cmd.exe", "/c", command + " > \"" + outputPath + "\"");
                } else {
                    pb = new ProcessBuilder("/bin/sh", "-c", command + " > \"" + outputPath + "\"");
                }

                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textAreaLog.append("mysqldump: " + line + "\n");
                    }
                }

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    textAreaLog.append("Copia de seguridad de '" + dbName + "' creada exitosamente.\n");
                } else {
                    textAreaLog.append("Error al crear copia de seguridad de '" + dbName + "'. Código de salida: " + exitCode + "\n");
                }
            } catch (IOException | InterruptedException ex) {
                textAreaLog.append("Error al ejecutar mysqldump para '" + dbName + "': " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, "Proceso de exportación de TODAS las bases de datos finalizado. Revisa el log para más detalles.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Método para mostrar el código QR (necesario para el registro de 2FA si se añade un botón de registro)
    private void showQRCode(String otpUrl) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(otpUrl, BarcodeFormat.QR_CODE, 250, 250);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        ImageIcon icon = new ImageIcon(image);
        JOptionPane.showMessageDialog(this, new JLabel(icon), 
            "Escanea este código QR con Google Authenticator", JOptionPane.PLAIN_MESSAGE);
    }
}