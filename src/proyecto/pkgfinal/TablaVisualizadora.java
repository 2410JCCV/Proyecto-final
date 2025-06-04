package proyecto.pkgfinal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import proyecto.pkgfinal.ClaseGrafica;
import proyecto.pkgfinal.ClaseInforme;
import proyecto.pkgfinal.ClaseRegistros;
import proyecto.pkgfinal.ImportadorSQLDinamico;
import proyecto.pkgfinal.MySQLDirectDumperApp;
import proyecto.pkgfinal.Respaldo;
import proyecto.pkgfinal.RichTextEditorApp; // Importar RichTextEditorApp
import proyecto.pkgfinal.AdminGUI;


/**
 * La clase `TablaVisualizadora` es la interfaz principal de la aplicación,
 * extendiendo `JFrame` para proporcionar una visualización interactiva de
 * datos de bases de datos MySQL. Actúa como un hub central, integrando
 * diversas funcionalidades y herramientas.
 *
 * Funcionalidades clave:
 * - **Conexión a Base de Datos**: Establece y gestiona la conexión con un servidor MySQL.
 * - **Navegación de Bases de Datos y Tablas**: Permite al usuario seleccionar una base de datos
 * (`comboBasesDatos`) y una tabla dentro de ella (`comboTablas`).
 * - **Visualización de Datos en Tabla**: Muestra los datos de la tabla seleccionada en un `JTable`
 * (`tablaDatos`), permitiendo operaciones CRUD (Crear, Leer, Actualizar, Eliminar).
 * - **Operaciones CRUD**:
 * - **Agregar (`addButton`)**: Abre un diálogo para insertar nuevos registros.
 * - **Actualizar (`updateButton`)**: Permite modificar registros existentes.
 * - **Eliminar (`deleteButton`)**: Elimina registros seleccionados.
 * - **Integración de Herramientas Externas**:
 * - **Editor SQL (`btnEditorSQL`)**: Abre una ventana para ejecutar consultas SQL personalizadas.
 * - **Importador SQL (`btnImportadorSQL`)**: Facilita la importación de archivos SQL.
 * - **Exportador SQL (`btnExportadorSQL`)**: Permite exportar la base de datos a un archivo SQL.
 * - **Módulo de Respaldo (`btnRespaldo`)**: Accede a la funcionalidad de respaldo con `mysqldump`.
 * - **Administración de Usuarios (`btnAdminUsers`)**: Abre una interfaz para gestionar usuarios y privilegios de MySQL.
 * - **Generación de Gráficas (`btnGenerarGrafica`)**: Permite visualizar datos en forma de gráficas.
 * - **Generación de Informes (`btnGenerarInforme`)**: Crea informes de datos en varios formatos (PDF, Excel, SQL).
 * - **Módulo de Registros (`btnRegistros`)**: Accede a una interfaz para la gestión específica de registros.
 * - **Editor de Texto Enriquecido (`btnEditorRich`)**: Abre un editor de texto avanzado.
 * - **Menú de Opciones**: Proporciona acceso a las mismas funcionalidades a través de una `JMenuBar` para una navegación alternativa.
 * - **Manejo de Errores**: Implementa `mostrarError` para presentar mensajes claros al usuario en caso de fallos.
 */


public class TablaVisualizadora extends JFrame {

    JTable tabla;
    DefaultTableModel modelo;
    JTextField campoBuscar;
    JComboBox<String> comboBasesDatos;
    JComboBox<String> comboTablas;
    String baseDatosSeleccionada = null;
    String tablaSeleccionada = null;

    // Variable para mantener la instancia del editor de texto
    private static RichTextEditorApp richTextEditorAppInstance;
    private static boolean richTextEditorStarted = false; // Bandera para controlar la llamada a start()

    public TablaVisualizadora() {
        setTitle("Gestión de Bases de Datos");
        setSize(1050, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // *** NUEVO: Inicialización "silenciosa" del JavaFX Toolkit usando JFXPanel ***
        // Crear un JFXPanel inicializa el JavaFX Toolkit si aún no lo ha hecho.
        // No necesitamos añadirlo visualmente si solo es para inicializar.
        new JFXPanel(); // Esta línea es clave para evitar "Toolkit not initialized"
        // *** FIN NUEVO ***

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoBuscar = new JTextField(20);
        JButton botonBuscar = new JButton("Buscar");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonEditar = new JButton("Editar");
        JButton botonAgregar = new JButton("Agregar");
        JButton botonSQL = new JButton("SQL");

        JMenuBar menuBarOpciones = new JMenuBar();
        JMenu menuOpciones = new JMenu("Opciones");

        JMenuItem itemGraficas = new JMenuItem("Gráficas");
        JMenuItem itemRegistros = new JMenuItem("Registros");
        JMenuItem itemInforme = new JMenuItem("Informe");
        JMenuItem itemimportador=new JMenuItem("Importador SQL");
        JMenuItem itemsqlcode=new JMenuItem("Extraer SQL");
        JMenuItem itemrespaldo=new JMenuItem("Respaldo");
        JMenuItem itemRichTextEditor = new JMenuItem("Editor de Texto");
        JMenuItem itemPermisos = new JMenuItem("permisos");

        itemsqlcode.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                proyecto.pkgfinal.MySQLDirectDumperApp ventana = new proyecto.pkgfinal.MySQLDirectDumperApp();
                ventana.setVisible(true);
            });
        });

        itemimportador.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                proyecto.pkgfinal.ImportadorSQLDinamico ventana = new proyecto.pkgfinal.ImportadorSQLDinamico();
                ventana.setVisible(true);
            });
        });

        itemGraficas.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                proyecto.pkgfinal.ClaseGrafica.mostrarSelectorYGrafica();
            });
        });

        itemRegistros.addActionListener(e -> {
            proyecto.pkgfinal.ClaseRegistros ventana = new proyecto.pkgfinal.ClaseRegistros();
            ventana.setVisible(true);
        });

        itemInforme.addActionListener(e -> {
            proyecto.pkgfinal.ClaseInforme ventana = new proyecto.pkgfinal.ClaseInforme();
            ventana.setVisible(true);
        });

        itemrespaldo.addActionListener(e -> {
            proyecto.pkgfinal.Respaldo ventana = new proyecto.pkgfinal.Respaldo();
            ventana.setVisible(true);
        });

        // Listener para el Editor de Texto
        itemRichTextEditor.addActionListener(e -> {
            // Asegura que el código JavaFX se ejecute en el hilo de la aplicación JavaFX
            Platform.runLater(() -> {
                if (richTextEditorAppInstance == null) { // Solo crear una nueva instancia si no existe
                    richTextEditorAppInstance = new RichTextEditorApp();
                    Stage stage = new Stage();
                    try {
                        richTextEditorAppInstance.start(stage);
                        richTextEditorStarted = true; // No es estrictamente necesario, pero mantiene la lógica
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al iniciar el Editor de Texto: " + ex.getMessage(), "Error JavaFX", JOptionPane.ERROR_MESSAGE);
                        richTextEditorStarted = false;
                    }
                }
                // Si la instancia existe (nueva o ya existente), muestra su Stage
                if (richTextEditorAppInstance != null) {
                    richTextEditorAppInstance.showStage(); // Llama al nuevo método para mostrar/restaurar la ventana
                }
            });
        });

        itemPermisos.addActionListener(e -> {
            proyecto.pkgfinal.AdminGUI ventana = new proyecto.pkgfinal.AdminGUI();
            ventana.setVisible(true);
        });
        
        
        
        menuOpciones.add(itemGraficas);
        menuOpciones.add(itemRegistros);
        menuOpciones.add(itemInforme);
        menuOpciones.add(itemimportador);
        menuOpciones.add(itemsqlcode);
        menuOpciones.add(itemrespaldo);
        menuOpciones.add(new JSeparator());
        menuOpciones.add(itemRichTextEditor);
        menuOpciones.add(itemPermisos);
        menuBarOpciones.add(menuOpciones);

        comboBasesDatos = new JComboBox<>();
        comboTablas = new JComboBox<>();
        JButton botonSeleccionarBD = new JButton("Seleccionar");

        modelo = new DefaultTableModel();
        tabla = new JTable(modelo);

        actualizarListaBasesDatos();

        panelSuperior.add(new JLabel("Base de datos:"));
        panelSuperior.add(comboBasesDatos);
        panelSuperior.add(new JLabel("Tabla:"));
        panelSuperior.add(comboTablas);
        panelSuperior.add(botonSeleccionarBD);
        panelSuperior.add(new JLabel("Buscar:"));
        panelSuperior.add(campoBuscar);
        panelSuperior.add(botonBuscar);
        panelSuperior.add(botonActualizar);
        panelSuperior.add(botonEliminar);
        panelSuperior.add(botonEditar);
        panelSuperior.add(botonAgregar);
        panelSuperior.add(botonSQL);
        panelSuperior.add(menuBarOpciones);

        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);

        botonBuscar.addActionListener(e -> buscarPorNombre());
        botonActualizar.addActionListener(e -> cargarDatos());
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        botonEditar.addActionListener(e -> editarSeleccionado());
        botonAgregar.addActionListener(e -> agregarRegistro());
        botonSeleccionarBD.addActionListener(e -> seleccionarBaseDatosYTabla());
        botonSQL.addActionListener(e -> abrirEditorSQL());

        comboBasesDatos.addActionListener(e -> {
            if (comboBasesDatos.getSelectedItem() != null &&
               !comboBasesDatos.getSelectedItem().equals(baseDatosSeleccionada)) {
                baseDatosSeleccionada = (String) comboBasesDatos.getSelectedItem();
                actualizarListaTablas();
            }
        });
    }

    private void abrirEditorSQL() {
        EditorSQLPanel editorSQL = new EditorSQLPanel(() -> {
            actualizarListaBasesDatos();
            if (comboBasesDatos.getSelectedItem() != null) {
                baseDatosSeleccionada = (String) comboBasesDatos.getSelectedItem();
                actualizarListaTablas();
            }
        });

        if (baseDatosSeleccionada != null) {
            editorSQL.comboBasesDatos.setSelectedItem(baseDatosSeleccionada);

            if (tablaSeleccionada != null) {
                editorSQL.comboTablas.setSelectedItem(tablaSeleccionada);
                editorSQL.mostrarEstructuraTabla();
            }
        }

        JFrame frameEditor = new JFrame("Editor SQL");
        frameEditor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameEditor.setContentPane(editorSQL);
        frameEditor.pack();
        frameEditor.setLocationRelativeTo(this);
        frameEditor.setVisible(true);
    }

    private void actualizarListaBasesDatos() {
        comboBasesDatos.removeAllItems();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "aAWV1c10YuHTQkmp");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {

            while (rs.next()) {
                String dbName = rs.getString(1);
                if (!dbName.equalsIgnoreCase("information_schema") &&
                    !dbName.equalsIgnoreCase("mysql") &&
                    !dbName.equalsIgnoreCase("performance_schema") &&
                    !dbName.equalsIgnoreCase("sys")) {
                    comboBasesDatos.addItem(dbName);
                }
            }

            if(comboBasesDatos.getItemCount() > 0) {
                comboBasesDatos.setSelectedIndex(0);
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar bases de datos", e);
        }
    }

    private void actualizarListaTablas() {
        comboTablas.removeAllItems();
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        campoBuscar.setText(""); // Limpiar el campo de búsqueda al cambiar de tabla/BD

        if (baseDatosSeleccionada == null) return;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/" + baseDatosSeleccionada,
                "root", "aAWV1c10YuHTQkmp");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            while (rs.next()) {
                comboTablas.addItem(rs.getString(1));
            }

            if(comboTablas.getItemCount() == 1) {
                comboTablas.setSelectedIndex(0);
                tablaSeleccionada = (String) comboTablas.getSelectedItem();
                cargarDatos();
            } else if(comboTablas.getItemCount() > 1) {
                tablaSeleccionada = null;
                modelo.setRowCount(0);
                modelo.setColumnCount(0);
            } else {
                tablaSeleccionada = null;
                JOptionPane.showMessageDialog(this,
                    "La base de datos seleccionada no contiene tablas",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar tablas de " + baseDatosSeleccionada, e);
        }
    }

    private void seleccionarBaseDatosYTabla() {
        String nuevaTabla = (String) comboTablas.getSelectedItem();

        if(nuevaTabla != null) {
            tablaSeleccionada = nuevaTabla;
            cargarDatos();
        }
    }

    private Connection conectar() throws SQLException {
        if(baseDatosSeleccionada == null) {
            throw new SQLException("No se ha seleccionado ninguna base de datos");
        }
        String url = "jdbc:mysql://localhost/" + baseDatosSeleccionada;
        return DriverManager.getConnection(url, "root", "aAWV1c10YuHTQkmp");
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        campoBuscar.setText(""); // Limpiar el campo de búsqueda al recargar datos

        if (tablaSeleccionada == null) {
            modelo.setColumnCount(0);
            return;
        }

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablaSeleccionada)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i-1] = metaData.getColumnName(i);
            }

            modelo.setColumnIdentifiers(columnNames);

            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i-1] = rs.getObject(i);
                }
                modelo.addRow(rowData);
            }

        } catch (SQLException e) {
            if(e.getMessage().contains("doesn't exist")) {
                JOptionPane.showMessageDialog(this,
                    "La tabla '" + tablaSeleccionada + "' no existe en la base de datos '" + baseDatosSeleccionada + "'",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                mostrarError("Error al cargar datos", e);
            }
            modelo.setRowCount(0);
            modelo.setColumnCount(0);
        }
    }

    private void buscarPorNombre() {
        String valorBuscar = campoBuscar.getText().trim();
        modelo.setRowCount(0); // Limpiar filas existentes para mostrar resultados de búsqueda

        if (tablaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una tabla para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (valorBuscar.isEmpty()) {
            cargarDatos(); // Si el campo de búsqueda está vacío, recargar todos los datos
            return;
        }

        try (Connection conn = conectar()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, tablaSeleccionada, null);

            List<String> columnNames = new ArrayList<>();
            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
            }
            columns.close(); 

            if (columnNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron columnas en la tabla para buscar.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder query = new StringBuilder("SELECT * FROM " + tablaSeleccionada + " WHERE ");
            for (int i = 0; i < columnNames.size(); i++) {
                if (i > 0) query.append(" OR ");
                query.append(columnNames.get(i)).append(" LIKE ?");
            }

            // Debugging: Imprimir la consulta generada
            System.out.println("Consulta de búsqueda: " + query.toString());

            PreparedStatement ps = conn.prepareStatement(query.toString());
            for (int i = 0; i < columnNames.size(); i++) {
                ps.setString(i+1, "%" + valorBuscar + "%");
            }

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            // Actualizar encabezados de columna si no están ya establecidos o si la tabla cambió
            if (modelo.getColumnCount() == 0 || !modelo.getColumnName(0).equals(rsMetaData.getColumnName(1))) {
                 String[] currentColumnNames = new String[columnCount];
                 for (int i = 1; i <= columnCount; i++) {
                     currentColumnNames[i-1] = rsMetaData.getColumnName(i);
                 }
                 modelo.setColumnIdentifiers(currentColumnNames);
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i-1] = rs.getObject(i); 
                }
                modelo.addRow(rowData);
            }

            rs.close(); 
            ps.close(); 
            // campoBuscar.setText(""); // No se limpia aquí para permitir refinar la búsqueda
        } catch (SQLException e) {
            mostrarError("Error al buscar", e);
        }
    }

    private void editarSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un registro para editar.",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = conectar()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            String columnaId = null;
            if (modelo.getColumnCount() > 0) {
                // Intenta encontrar la PK primero, si no, usa la primera columna
                ResultSet pkCols = meta.getPrimaryKeys(null, null, tablaSeleccionada);
                if (pkCols.next()) {
                    columnaId = pkCols.getString("COLUMN_NAME");
                } else {
                    columnaId = modelo.getColumnName(0); 
                }
                pkCols.close();
            } else {
                JOptionPane.showMessageDialog(this, "No hay columnas cargadas para editar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Encuentra el índice de la columna ID en el modelo
            int idColumnIndex = -1;
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                if (modelo.getColumnName(i).equalsIgnoreCase(columnaId)) {
                    idColumnIndex = i;
                    break;
                }
            }

            if (idColumnIndex == -1) {
                JOptionPane.showMessageDialog(this, "No se pudo identificar la columna ID para editar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object valorId = modelo.getValueAt(filaSeleccionada, idColumnIndex); 

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            List<JTextField> camposEditables = new ArrayList<>();
            List<String> nombresColumnasDB = new ArrayList<>(); 

            // Iterar sobre las columnas de la tabla del modelo, excluyendo la columna de la clave primaria
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                String nombreColumnaVista = modelo.getColumnName(i); 
                if (nombreColumnaVista.equalsIgnoreCase(columnaId)) {
                    continue; // No permitir editar la columna de la clave primaria
                }

                Object valorActual = modelo.getValueAt(filaSeleccionada, i);

                panel.add(new JLabel(nombreColumnaVista + ":"));

                JTextField campo = new JTextField(valorActual != null ? valorActual.toString() : "");
                camposEditables.add(campo);
                panel.add(campo);
                nombresColumnasDB.add(nombreColumnaVista); 
            }

            if (camposEditables.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron columnas editables para este registro.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int resultado = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Editar Registro - " + tablaSeleccionada,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (resultado == JOptionPane.OK_OPTION) {
                StringBuilder sql = new StringBuilder("UPDATE " + tablaSeleccionada + " SET ");
                List<Object> valores = new ArrayList<>();
                int camposActualizados = 0;

                for (int i = 0; i < camposEditables.size(); i++) {
                    String nombreColumnaDB = nombresColumnasDB.get(i); 
                    if (camposActualizados > 0) {
                        sql.append(", ");
                    }
                    sql.append(nombreColumnaDB).append(" = ?");
                    valores.add(camposEditables.get(i).getText());
                    camposActualizados++;
                }

                sql.append(" WHERE ").append(columnaId).append(" = ?"); 
                valores.add(valorId);

                if (camposActualizados > 0) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                        for (int i = 0; i < valores.size(); i++) {
                            // Manejar posibles conversiones de tipo si es necesario (ej. enteros, fechas)
                            // Por ahora, asumimos que JDBC manejará la conversión de String a tipo de DB.
                            pstmt.setObject(i + 1, valores.get(i));
                        }

                        int filasAfectadas = pstmt.executeUpdate();
                        if (filasAfectadas > 0) {
                            JOptionPane.showMessageDialog(this,
                                "Registro actualizado correctamente.",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            cargarDatos();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "El registro no fue encontrado o no se realizaron cambios.",
                                "Información", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se encontraron columnas válidas para actualizar.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al editar el registro", e);
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un registro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de eliminar este registro?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection conn = conectar()) {
                String columnaId = null;
                Object valorId = null;

                // Intentar obtener la clave primaria
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet pkCols = meta.getPrimaryKeys(null, null, tablaSeleccionada);
                if (pkCols.next()) {
                    columnaId = pkCols.getString("COLUMN_NAME");
                    // Buscar el índice de la columna PK en el modelo de la tabla
                    for (int i = 0; i < modelo.getColumnCount(); i++) {
                        if (modelo.getColumnName(i).equalsIgnoreCase(columnaId)) {
                            valorId = modelo.getValueAt(fila, i);
                            break;
                        }
                    }
                } else {
                    // Si no hay clave primaria, usar la primera columna visible en el modelo
                    columnaId = tabla.getColumnName(0);
                    valorId = modelo.getValueAt(fila, 0);
                }
                pkCols.close();

                if (columnaId == null || valorId == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo identificar una columna para eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "DELETE FROM " + tablaSeleccionada + " WHERE " + columnaId + " = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setObject(1, valorId);
                    int filasAfectadas = pstmt.executeUpdate();

                    if (filasAfectadas > 0) {
                        modelo.removeRow(fila);
                        JOptionPane.showMessageDialog(this, "Registro eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                         JOptionPane.showMessageDialog(this, "El registro no fue encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                mostrarError("Error al eliminar el registro", e);
            }
        }
    }

   private void agregarRegistro() {
        try (Connection conn = conectar()) {
            // Obtener metadatos de la tabla
            DatabaseMetaData meta = conn.getMetaData();

            // 1. Identificar la clave primaria (si existe)
            ResultSet primaryKeys = meta.getPrimaryKeys(null, null, tablaSeleccionada);
            String pkColumnName = null;
            if (primaryKeys.next()) {
                pkColumnName = primaryKeys.getString("COLUMN_NAME");
            }
            primaryKeys.close();

            // 2. Obtener columnas excluyendo la PK autoincremental
            ResultSet columnas = meta.getColumns(null, null, tablaSeleccionada, null);

            // Lista para almacenar columnas únicas
            List<String> columnasUnicas = new ArrayList<>();
            List<Boolean> esAutoincrementalList = new ArrayList<>();

            while (columnas.next()) {
                String nombreColumna = columnas.getString("COLUMN_NAME");
                String esAutoincremental = columnas.getString("IS_AUTOINCREMENT");

                // Verificar si la columna ya fue agregada
                if (!columnasUnicas.contains(nombreColumna)) {
                    columnasUnicas.add(nombreColumna);
                    esAutoincrementalList.add("YES".equals(esAutoincremental));
                }
            }
            columnas.close();

            // 3. Crear panel de inserción dinámico
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            List<JTextField> campos = new ArrayList<>();
            List<String> nombresColumnasInsertables = new ArrayList<>();

            for (int i = 0; i < columnasUnicas.size(); i++) {
                String nombreColumna = columnasUnicas.get(i);
                boolean esPKAutoincremental = nombreColumna.equals(pkColumnName) && esAutoincrementalList.get(i);

                // Saltar columna si es PK autoincremental
                if (esPKAutoincremental) {
                    continue;
                }

                // Agregar campo al diálogo
                panel.add(new JLabel(nombreColumna + ":"));
                JTextField campo = new JTextField();
                campos.add(campo);
                panel.add(campo);
                nombresColumnasInsertables.add(nombreColumna);
            }

            // 4. Mostrar diálogo de inserción
            int resultado = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Agregar Nuevo Registro - " + tablaSeleccionada,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (resultado == JOptionPane.OK_OPTION) {
                // 5. Construir consulta INSERT
                StringBuilder sql = new StringBuilder("INSERT INTO " + tablaSeleccionada + " (");
                StringBuilder placeholders = new StringBuilder("VALUES (");
                List<Object> valores = new ArrayList<>();

                for (int i = 0; i < nombresColumnasInsertables.size(); i++) {
                    if (i > 0) {
                        sql.append(", ");
                        placeholders.append(", ");
                    }
                    sql.append(nombresColumnasInsertables.get(i));
                    placeholders.append("?");

                    // Manejar campos vacíos como NULL
                    String valor = campos.get(i).getText().trim();
                    valores.add(valor.isEmpty() ? null : valor);
                }

                sql.append(") ").append(placeholders).append(")");

                // 6. Ejecutar la inserción
                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < valores.size(); i++) {
                        pstmt.setObject(i + 1, valores.get(i));
                    }

                    int filasAfectadas = pstmt.executeUpdate();

                    if (filasAfectadas > 0) {
                        // Mostrar ID generado si existe
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys != null && generatedKeys.next()) {
                                JOptionPane.showMessageDialog(this,
                                    "Registro agregado correctamente. ID generado: " + generatedKeys.getLong(1),
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this,
                                    "Registro agregado correctamente.",
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        cargarDatos(); // Refrescar los datos
                    }
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al agregar el registro", e);
        }
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(
            this,
            mensaje + ":\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();
    }
}