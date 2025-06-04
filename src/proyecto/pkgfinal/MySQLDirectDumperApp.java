package proyecto.pkgfinal;


/**
 * Juan Carlos Carrillo Viveros (217o02237) y Marlleli Luna Zamora (217o00552)
 * `MySQLDirectDumperApp` es una `JFrame` que permite exportar esquemas y datos de bases de datos MySQL a un archivo SQL.
 *
 * Características clave:
 * - **Conexión a BD**: Usa `DB_URL`, `DB_USER`, `DB_PASS` para conectar.
 * - **Selección de BD y Tablas**: `dbComboBox` para BD, `tablesList` (con `CheckBoxListCellRenderer`) para seleccionar tablas.
 * - **Salida de Archivo**: `destinationPathField` y `browseButton` definen la ruta del archivo SQL.
 * - **Opciones de Exportación**: `chkIncludeSchema` (CREATE TABLE), `chkIncludeData` (INSERT INTO), `chkAddDropTable` (DROP TABLE).
 * - **Exportación Asíncrona**: `exportButton` inicia un `SwingWorker` para no bloquear la UI.
 * - **Reporte de Estado**: `statusLabel` muestra el progreso.
 */


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
// import java.nio.charset.StandardCharsets; // YA NO SE NECESITA ESTA IMPORTACIÓN DIRECTAMENTE
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MySQLDirectDumperApp extends JFrame {

    // --- CONFIGURACIÓN DE BASE DE DATOS ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    // !!!!!!!!!!!! CRÍTICO: TU CONTRASEÑA REAL DE MYSQL !!!!!!!!!!!!
    // Si no tienes contraseña, déjala como una cadena vacía: ""
    private static final String DB_PASS = "aAWV1c10YuHTQkmp"; // <--- CAMBIA ESTO A TU CONTRASEÑA REAL !!!!!!!!

    // --- Componentes de la Interfaz Gráfica ---
    private JComboBox<String> dbComboBox;
    private JTextField destinationPathField;
    private JButton browseButton;
    private JButton exportButton;
    private JLabel statusLabel;
    private JList<JCheckBox> tablesList;
    private JScrollPane tablesScrollPane;

    public MySQLDirectDumperApp() {
        setTitle("Exportador de Bases de Datos MySQL (sin mysqldump)");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Selecciona Base de Datos:"));
        dbComboBox = new JComboBox<>();
        populateDatabaseComboBox();
        dbComboBox.addActionListener(e -> loadTablesForSelectedDatabase());
        topPanel.add(dbComboBox);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(new JLabel("Guardar en:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        destinationPathField = new JTextField(30);
        destinationPathField.setEditable(false);
        centerPanel.add(destinationPathField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        browseButton = new JButton("Examinar...");
        centerPanel.add(browseButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(new JLabel("Tablas a Excluir (marcar para NO exportar):"), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        tablesList = new JList<>();
        tablesList.setCellRenderer(new CheckBoxListCellRenderer());
        tablesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablesList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                JList<?> list = (JList<?>) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                if (index != -1) {
                    JCheckBox checkbox = (JCheckBox) list.getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    list.repaint();
                }
            }
        });
        tablesScrollPane = new JScrollPane(tablesList);
        centerPanel.add(tablesScrollPane, gbc);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        exportButton = new JButton("Exportar Base de Datos");
        statusLabel = new JLabel("Listo para exportar.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        bottomPanel.add(exportButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        browseButton.addActionListener(e -> selectDestinationFile());
        exportButton.addActionListener(e -> exportSelectedDatabase());

        loadTablesForSelectedDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private List<String> getDatabaseNames() {
        List<String> dbNames = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {

            while (rs.next()) {
                String dbName = rs.getString(1);
                if (!dbName.equalsIgnoreCase("information_schema") &&
                    !dbName.equalsIgnoreCase("mysql") &&
                    !dbName.equalsIgnoreCase("performance_schema") &&
                    !dbName.equalsIgnoreCase("sys") &&
                    !dbName.equalsIgnoreCase("phpmyadmin")
                ) {
                    dbNames.add(dbName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos o al obtener nombres de BD:\n" + e.getMessage() +
                    "\nPosibles causas:\n- MySQL no está ejecutándose.\n- Credenciales (usuario/contraseña) incorrectas.\n- El conector JDBC de MySQL no está en tu proyecto (ver pasos de NetBeans).",
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            dbComboBox.setEnabled(false);
            exportButton.setEnabled(false);
        }
        return dbNames;
    }

    private void populateDatabaseComboBox() {
        List<String> dbNames = getDatabaseNames();
        if (dbNames.isEmpty() && dbComboBox.isEnabled()) {
             JOptionPane.showMessageDialog(this,
                    "No se encontraron bases de datos o no se pudo conectar. " +
                    "Verifica que tu servidor MySQL esté en ejecución y tus credenciales.",
                    "Sin Bases de Datos",
                    JOptionPane.WARNING_MESSAGE);
            dbComboBox.setEnabled(false);
            exportButton.setEnabled(false);
            return;
        }
        for (String db : dbNames) {
            dbComboBox.addItem(db);
        }
    }

    private List<String> getTableNames(String dbName) {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("USE `" + dbName + "`");
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            while (rs.next()) {
                tableNames.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al obtener tablas para la base de datos '" + dbName + "':\n" + e.getMessage(),
                    "Error de Tablas", JOptionPane.ERROR_MESSAGE);
        }
        return tableNames;
    }

    private void loadTablesForSelectedDatabase() {
        String selectedDb = (String) dbComboBox.getSelectedItem();
        Vector<JCheckBox> checkboxes = new Vector<>();
        if (selectedDb != null && !selectedDb.isEmpty()) {
            List<String> tableNames = getTableNames(selectedDb);
            for (String tableName : tableNames) {
                checkboxes.add(new JCheckBox(tableName, false));
            }
        }
        tablesList.setListData(checkboxes);
    }

    private void selectDestinationFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Script SQL Como");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos SQL (*.sql)", "sql"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        String selectedDb = (String) dbComboBox.getSelectedItem();
        String defaultFileName = (selectedDb != null && !selectedDb.isEmpty()) ? selectedDb + ".sql" : "backup_database.sql";
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".sql")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".sql");
            }
            destinationPathField.setText(fileToSave.getAbsolutePath());
        }
    }

    private boolean exportDatabaseDirect(String dbName, File destinationFile) {
        // --- ESTA ES LA LÍNEA MODIFICADA ---
        // Se usa FileWriter(File, boolean append) que es compatible con Java 6.
        // false significa que el archivo será sobrescrito (no se añadirá al final).
        // La codificación de caracteres será la predeterminada del sistema.
        try (Connection conn = getConnection();
             PrintWriter writer = new PrintWriter(new FileWriter(destinationFile, false))) { // <<--- CAMBIO AQUÍ

            writer.println("-- SQL Export generado por MySQLDirectDumperApp");
            writer.println("-- Fecha: " + new java.util.Date());
            writer.println("-- Base de datos: `" + dbName + "`");
            writer.println();
            writer.println("SET NAMES utf8mb4;"); // Aunque el archivo no sea UTF-8, el SQL puede especificarlo
            writer.println("SET FOREIGN_KEY_CHECKS = 0;");
            writer.println("SET UNIQUE_CHECKS = 0;");
            writer.println("SET AUTOCOMMIT = 0;");
            writer.println();
            writer.println("CREATE DATABASE IF NOT EXISTS `" + dbName + "`;");
            writer.println("USE `" + dbName + "`;");
            writer.println();

            List<String> excludedTables = new ArrayList<>();
            for (int i = 0; i < tablesList.getModel().getSize(); i++) {
                JCheckBox checkbox = tablesList.getModel().getElementAt(i);
                if (checkbox.isSelected()) {
                    excludedTables.add(checkbox.getText());
                }
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("USE `" + dbName + "`");

                ResultSet rsTables = stmt.executeQuery("SHOW FULL TABLES WHERE Table_Type = 'BASE TABLE'");
                List<String> tableNamesToExport = new ArrayList<>();
                while (rsTables.next()) {
                    String tableName = rsTables.getString(1);
                    if (!excludedTables.contains(tableName)) {
                        tableNamesToExport.add(tableName);
                    } else {
                        writer.println("-- Tabla '" + tableName + "' excluida de la exportación.");
                    }
                }

                for (String tableName : tableNamesToExport) {
                    writer.println("-- Estructura para la tabla: `" + tableName + "`");
                    ResultSet rsCreateTable = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`");
                    if (rsCreateTable.next()) {
                        writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");
                        writer.println(rsCreateTable.getString(2) + ";");
                        writer.println();
                    }
                }

                for (String tableName : tableNamesToExport) {
                    writer.println("-- Volcando datos para la tabla: `" + tableName + "`");
                    ResultSet rsData = stmt.executeQuery("SELECT * FROM `" + tableName + "`");
                    ResultSetMetaData rsmd = rsData.getMetaData();
                    int columnCount = rsmd.getColumnCount();

                    StringBuilder insertHeader = new StringBuilder();
                    insertHeader.append("INSERT INTO `").append(tableName).append("` (");
                    for (int i = 1; i <= columnCount; i++) {
                        insertHeader.append("`").append(rsmd.getColumnName(i)).append("`");
                        if (i < columnCount) {
                            insertHeader.append(", ");
                        }
                    }
                    insertHeader.append(") VALUES\n");

                    final int BATCH_SIZE = 1000;
                    int rowCount = 0;
                    StringBuilder currentInsertBatch = new StringBuilder();

                    while (rsData.next()) {
                        if (rowCount % BATCH_SIZE == 0) {
                            if (rowCount > 0) {
                                currentInsertBatch.append(";\n");
                                writer.print(currentInsertBatch.toString());
                                currentInsertBatch = new StringBuilder();
                            }
                            currentInsertBatch.append(insertHeader);
                        } else {
                            currentInsertBatch.append(",\n");
                        }

                        currentInsertBatch.append("(");
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rsData.getObject(i);
                            int columnType = rsmd.getColumnType(i);

                            if (value == null) {
                                currentInsertBatch.append("NULL");
                            } else {
                                switch (columnType) {
                                    case Types.TINYINT: case Types.SMALLINT: case Types.INTEGER:
                                    case Types.BIGINT: case Types.FLOAT: case Types.DOUBLE:
                                    case Types.DECIMAL: case Types.NUMERIC:
                                        currentInsertBatch.append(value.toString());
                                        break;
                                    case Types.BOOLEAN:
                                        currentInsertBatch.append((Boolean) value ? "1" : "0");
                                        break;
                                    default:
                                        String strValue = value.toString();
                                        strValue = strValue.replace("\\", "\\\\");
                                        strValue = strValue.replace("'", "''");
                                        currentInsertBatch.append("'").append(strValue).append("'");
                                        break;
                                }
                            }
                            if (i < columnCount) {
                                currentInsertBatch.append(", ");
                            }
                        }
                        currentInsertBatch.append(")");
                        rowCount++;
                    }

                    if (rowCount > 0) {
                        currentInsertBatch.append(";\n");
                        writer.print(currentInsertBatch.toString());
                    } else {
                        writer.println(" -- No hay datos en esta tabla.");
                    }
                    writer.println();
                }
            }

            writer.println("COMMIT;");
            writer.println("SET AUTOCOMMIT = 1;");
            writer.println("SET UNIQUE_CHECKS = 1;");
            writer.println("SET FOREIGN_KEY_CHECKS = 1;");
            writer.println("-- Exportación completada.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos durante la exportación:\n" + e.getMessage() +
                    "\nAsegúrate de tener permisos para SHOW CREATE TABLE y SELECT en las tablas.\n" +
                    "Revisa la consola de NetBeans para más detalles.",
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al escribir el archivo SQL:\n" + e.getMessage() +
                    "\nVerifica que tienes permisos para escribir en la ubicación de destino.",
                    "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error inesperado durante la exportación directa:\n" + e.getMessage() +
                    "\nRevisa la consola de NetBeans para más detalles.",
                    "Error General", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void exportSelectedDatabase() {
        String selectedDb = (String) dbComboBox.getSelectedItem();
        String destinationPathStr = destinationPathField.getText();

        if (selectedDb == null || selectedDb.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona una base de datos de la lista.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (destinationPathStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona una ruta de destino para el archivo SQL.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File destinationFile = new File(destinationPathStr);

        statusLabel.setText("Exportando '" + selectedDb + "' (directo)...");
        exportButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return exportDatabaseDirect(selectedDb, destinationFile);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        statusLabel.setText("Exportación de '" + selectedDb + "' completada exitosamente.");
                        JOptionPane.showMessageDialog(MySQLDirectDumperApp.this,
                                "La base de datos '" + selectedDb + "' ha sido exportada con éxito a:\n" + destinationFile.getAbsolutePath(),
                                "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        statusLabel.setText("Falló la exportación de '" + selectedDb + "'. Revisa los mensajes de error.");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error interno durante la exportación: " + ex.getMessage());
                    JOptionPane.showMessageDialog(MySQLDirectDumperApp.this,
                            "Un error inesperado ocurrió al finalizar la exportación: " + ex.getMessage(),
                            "Error Interno", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    exportButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private static class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<JCheckBox> {
        @Override
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setComponentOrientation(list.getComponentOrientation());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setSelected(value.isSelected());
            setText(value.getText());
            setEnabled(list.isEnabled());
            return this;
        }
    }
}