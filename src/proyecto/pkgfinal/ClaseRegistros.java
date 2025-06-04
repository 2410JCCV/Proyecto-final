package proyecto.pkgfinal;

/**
 * Carrillo Viveros Juan Carlos 217o02237 y Luna Zamora Marlleli 217o00552
 * La clase ClaseRegistros es una aplicación de escritorio que permite visualizar y gestionar información de bases de datos MySQL, como bases de datos y tablas. Sus funcionalidades principales son:
 * - **Interfaz de Usuario (GUI)**: Muestra una ventana con paneles para filtros, selección de modo de visualización, un reloj en tiempo real, un calendario y una tabla para mostrar los datos.
 * - **Conexión a MySQL**: Establece una conexión con un servidor MySQL para obtener metadatos y datos.
 * - **Visualización de Información**:
 * - **Bases de Datos**: Muestra el ID, nombre, número de tablas, y (placeholder) fecha y hora de creación de cada base de datos.
 * - **Tablas**: Muestra el ID, base de datos, nombre de tabla, número de columnas, número de filas, y (placeholder) fecha y hora de creación de cada tabla.
 * - **Filtros de Búsqueda y Selección**: Permite filtrar los datos por nombre y seleccionar una base de datos específica para la visualización.
 * - **Exportación de Datos**: Ofrece la funcionalidad de exportar la información mostrada en la tabla a varios formatos:
 * - **Excel (XLSX)**: Utiliza Apache POI para crear hojas de cálculo.
 * - **PDF**: Utiliza iText PDF para generar documentos PDF con formato de tabla.
 * - **Word (DOCX)**: Utiliza Apache POI para crear documentos de Word con formato de tabla.
 * - **SQL**: Genera un archivo `.sql` con sentencias `INSERT` para recrear los datos de la tabla.
 * - **Reloj en Tiempo Real**: Muestra la hora actual de la Ciudad de México.
 * - **Manejo de Errores**: Incluye diálogos de mensaje para informar al usuario sobre errores de conexión o problemas durante la carga o exportación de datos.
 */

import com.toedter.calendar.JCalendar;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter; // Importar para exportar a SQL
import java.io.PrintWriter; // Importar para exportar a SQL

// Para Excel (Apache POI)
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Para PDF (iText PDF)
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Para Word (Apache POI XWPF)
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import javax.swing.filechooser.FileNameExtensionFilter;

public class ClaseRegistros extends JFrame {

    private JLabel relojLabel;
    private JTable tablaRegistros;
    private DefaultTableModel modelo;
    private JTextField campoBusqueda;
    private JComboBox<String> comboBasesFiltro;
    private JComboBox<String> comboModoVisualizacion;

    private Connection conexion;

    public ClaseRegistros() {
        setTitle("Información de Bases de Datos MySQL");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelFiltros.add(new JLabel("Buscar por nombre:"));
        campoBusqueda = new JTextField(20);
        panelFiltros.add(campoBusqueda);

        panelFiltros.add(new JLabel("Seleccionar Base:"));
        comboBasesFiltro = new JComboBox<>();
        panelFiltros.add(comboBasesFiltro);

        JButton btnAplicarFiltros = new JButton("Aplicar Filtros");
        panelFiltros.add(btnAplicarFiltros);

        JPanel panelModoYExportar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelModoYExportar.add(new JLabel("Mostrar:"));
        comboModoVisualizacion = new JComboBox<>(new String[]{"Bases de Datos", "Tablas"});
        panelModoYExportar.add(comboModoVisualizacion);

        JButton btnExportarExcel = new JButton("Exportar a Excel");
        JButton btnExportarPDF = new JButton("Exportar a PDF");
        JButton btnExportarWord = new JButton("Exportar a Word");
        JButton btnExportarSQL = new JButton("Exportar a SQL"); // Nuevo botón para SQL

        panelModoYExportar.add(btnExportarExcel);
        panelModoYExportar.add(btnExportarPDF);
        panelModoYExportar.add(btnExportarWord);
        panelModoYExportar.add(btnExportarSQL); // Añadir el nuevo botón

        panelSuperior.add(panelFiltros);
        panelSuperior.add(panelModoYExportar);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        relojLabel = new JLabel("", SwingConstants.CENTER);
        relojLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        relojLabel.setBorder(BorderFactory.createTitledBorder("Hora Actual"));
        panelCentro.add(relojLabel);

        JCalendar calendario = new JCalendar();
        panelCentro.add(calendario);
        add(panelCentro, BorderLayout.CENTER);

        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRegistros = new JTable(modelo);
        tablaRegistros.setRowHeight(25);
        tablaRegistros.setAutoCreateRowSorter(true);
        // Habilitar el autoscroll horizontal
        tablaRegistros.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollTabla = new JScrollPane(tablaRegistros);
        scrollTabla.setPreferredSize(new Dimension(800, 300));
        // Asegurar que la barra horizontal aparezca si es necesaria
        scrollTabla.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollTabla, BorderLayout.SOUTH);

        conectarBD();
        cargarBasesDeDatosParaCombo();

        btnAplicarFiltros.addActionListener(e -> {
            String modo = (String) comboModoVisualizacion.getSelectedItem();
            if ("Bases de Datos".equals(modo)) {
                cargarInformacionDeBasesDeDatos();
            } else {
                cargarInformacionDeTablas();
            }
        });

        comboBasesFiltro.addActionListener(e -> {
            String modo = (String) comboModoVisualizacion.getSelectedItem();
            if ("Tablas".equals(modo)) {
                cargarInformacionDeTablas();
            }
        });

        comboModoVisualizacion.addActionListener(e -> {
            String modoSeleccionado = (String) comboModoVisualizacion.getSelectedItem();
            if ("Bases de Datos".equals(modoSeleccionado)) {
                campoBusqueda.setText("");
                campoBusqueda.setToolTipText("Buscar por nombre de BD");
                cargarInformacionDeBasesDeDatos();
                comboBasesFiltro.setEnabled(true);
            } else {
                campoBusqueda.setText("");
                campoBusqueda.setToolTipText("Buscar por nombre de tabla");
                cargarInformacionDeTablas();
                comboBasesFiltro.setEnabled(true);
            }
        });

        btnExportarExcel.addActionListener(e -> exportarTabla(ExportFormat.EXCEL));
        btnExportarPDF.addActionListener(e -> exportarTabla(ExportFormat.PDF));
        btnExportarWord.addActionListener(e -> exportarTabla(ExportFormat.WORD));
        btnExportarSQL.addActionListener(e -> exportarTabla(ExportFormat.SQL)); // Listener para SQL

        cargarInformacionDeBasesDeDatos();
        actualizarReloj();
    }

    private void conectarBD() {
        try {
            conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost/",
                "root",
                "aAWV1c10YuHTQkmp");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al conectar con MySQL: " + e.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarBasesDeDatosParaCombo() {
        comboBasesFiltro.removeAllItems();
        comboBasesFiltro.addItem("Todas las Bases");
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {

            while (rs.next()) {
                String db = rs.getString(1);
                if (!db.equals("information_schema") && !db.equals("mysql") &&
                    !db.equals("performance_schema") && !db.equals("sys")) {
                    comboBasesFiltro.addItem(db);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarInformacionDeBasesDeDatos() {
        // --- Nombres de columna actualizados para incluir Fecha y Hora de Creación ---
        String[] columnas = {"ID", "Base de Datos", "Número de Tablas", "Fecha Creación", "Hora Creación"};
        modelo.setColumnIdentifiers(columnas);
        modelo.setRowCount(0);

        String busquedaTexto = campoBusqueda.getText();
        String baseSeleccionada = (String) comboBasesFiltro.getSelectedItem();

        try (Statement stmtBases = conexion.createStatement();
             ResultSet rsBases = stmtBases.executeQuery("SHOW DATABASES")) {

            int idCounter = 1;
            while (rsBases.next()) {
                String dbName = rsBases.getString(1);

                if (!dbName.equals("information_schema") && !dbName.equals("mysql") &&
                    !dbName.equals("performance_schema") && !dbName.equals("sys")) {

                    if (busquedaTexto != null && !busquedaTexto.isEmpty() &&
                        !dbName.toLowerCase().contains(busquedaTexto.toLowerCase())) {
                        continue;
                    }

                    if (baseSeleccionada != null && !baseSeleccionada.equals("Todas las Bases") &&
                        !dbName.equals(baseSeleccionada)) {
                        continue;
                    }

                    int numTables = 0;
                    try (Statement stmtTables = conexion.createStatement();
                         ResultSet rsTables = stmtTables.executeQuery("SHOW TABLES FROM `" + dbName + "`")) {
                        while (rsTables.next()) {
                            numTables++;
                        }
                    } catch (SQLException e) {
                        numTables = -1; // Error al contar tablas
                    }

                    // --- Información de Fecha y Hora de Creación (se muestra "N/A" por ahora) ---
                    // MySQL no almacena directamente la fecha/hora de creación de una base de datos en information_schema.
                    // Obtener esta información implicaría revisar logs o metadatos del sistema operativo, lo cual es complejo.
                    String fechaCreacion = "N/A";
                    String horaCreacion = "N/A";

                    Object[] fila = {
                        idCounter++,
                        dbName,
                        (numTables == -1) ? "Error" : numTables,
                        fechaCreacion,
                        horaCreacion
                    };
                    modelo.addRow(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar información de bases de datos: " + e.getMessage(),
                "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarInformacionDeTablas() {
        // --- Nombres de columna actualizados para incluir Fecha y Hora de Creación de la tabla ---
        String[] columnas = {"ID", "Base de Datos", "Nombre de Tabla", "Número de Columnas", "Número de Filas", "Fecha Creación", "Hora Creación"};
        modelo.setColumnIdentifiers(columnas);
        modelo.setRowCount(0);

        String busquedaTexto = campoBusqueda.getText();
        String baseSeleccionada = (String) comboBasesFiltro.getSelectedItem();

        try (Statement stmtDatabases = conexion.createStatement();
             ResultSet rsDatabases = stmtDatabases.executeQuery("SHOW DATABASES")) {

            int idCounter = 1;
            while (rsDatabases.next()) {
                String dbName = rsDatabases.getString(1);

                if (dbName.equals("information_schema") || dbName.equals("mysql") ||
                    dbName.equals("performance_schema") || dbName.equals("sys")) {
                    continue;
                }

                if (baseSeleccionada != null && !baseSeleccionada.equals("Todas las Bases") &&
                    !dbName.equals(baseSeleccionada)) {
                    continue;
                }

                try (Statement stmtTables = conexion.createStatement();
                     ResultSet rsTables = stmtTables.executeQuery("SHOW TABLES FROM `" + dbName + "`")) {

                    while (rsTables.next()) {
                        String tableName = rsTables.getString(1);

                        if (busquedaTexto != null && !busquedaTexto.isEmpty() &&
                            !tableName.toLowerCase().contains(busquedaTexto.toLowerCase())) {
                            continue;
                        }

                        int numColumns = 0;
                        int numRows = 0;
                        String fechaCreacion = "N/A";
                        String horaCreacion = "N/A";

                        // Obtener número de columnas
                        try (ResultSet rsColumns = conexion.createStatement().executeQuery(
                             "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = '" + dbName + "' AND table_name = '" + tableName + "'")) {
                            if (rsColumns.next()) {
                                numColumns = rsColumns.getInt(1);
                            }
                        } catch (SQLException e) {
                            numColumns = -1; // Error al contar columnas
                        }

                        // Obtener número de filas
                        try (ResultSet rsRows = conexion.createStatement().executeQuery("SELECT COUNT(*) FROM `" + dbName + "`.`" + tableName + "`")) {
                            if (rsRows.next()) {
                                numRows = rsRows.getInt(1);
                            }
                        } catch (SQLException e) {
                            numRows = -1; // Error al contar filas
                        }

                        // Obtener fecha y hora de creación de la tabla desde information_schema.tables
                        try (ResultSet rsTableInfo = conexion.createStatement().executeQuery(
                            "SELECT CREATE_TIME FROM information_schema.tables WHERE table_schema = '" + dbName + "' AND table_name = '" + tableName + "'")) {
                            if (rsTableInfo.next()) {
                                Timestamp createTime = rsTableInfo.getTimestamp("CREATE_TIME");
                                if (createTime != null) {
                                    LocalDateTime ldt = createTime.toLocalDateTime();
                                    fechaCreacion = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    horaCreacion = ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                }
                            }
                        } catch (SQLException e) {
                            System.err.println("WARN: No se pudo obtener la fecha/hora de creación para la tabla '" + tableName + "' en la BD '" + dbName + "': " + e.getMessage());
                        }

                        Object[] fila = {
                            idCounter++,
                            dbName,
                            tableName,
                            (numColumns == -1) ? "Error" : numColumns,
                            (numRows == -1) ? "Error" : numRows,
                            fechaCreacion,
                            horaCreacion
                        };
                        modelo.addRow(fila);
                    }
                } catch (SQLException e) {
                    System.err.println("WARN: No se pudieron obtener tablas para la BD '" + dbName + "': " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar información de tablas: " + e.getMessage(),
                "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private enum ExportFormat {
        EXCEL, PDF, WORD, SQL // Añadido SQL al enum
    }

    private void exportarTabla(ExportFormat format) {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        String extension = "";
        String description = "";
        switch (format) {
            case EXCEL:
                extension = "xlsx";
                description = "Archivos de Excel (*.xlsx)";
                break;
            case PDF:
                extension = "pdf";
                description = "Archivos PDF (*.pdf)";
                break;
            case WORD:
                extension = "docx";
                description = "Archivos de Word (*.docx)";
                break;
            case SQL: // Caso para SQL
                extension = "sql";
                description = "Archivos SQL (*.sql)";
                break;
        }

        fileChooser.setFileFilter(new FileNameExtensionFilter(description, extension));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith("." + extension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + "." + extension);
            }

            try {
                // Para SQL, necesitamos el nombre de la BD y la tabla seleccionada.
                // Esta parte es crítica: ¿De dónde obtenemos la tabla a exportar a SQL?
                // El modo "Tablas" muestra todas las tablas de todas las bases.
                // Para una exportación SQL, el usuario debería seleccionar una fila de la tabla JTable.

                if (format == ExportFormat.SQL) {
                    // Verificar si estamos en el modo de visualización de tablas
                    if (!"Tablas".equals(comboModoVisualizacion.getSelectedItem())) {
                        JOptionPane.showMessageDialog(this, "La exportación a SQL solo es posible cuando se muestran 'Tablas'.\nPor favor, cambie el modo de visualización.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Verificar si hay una fila seleccionada en la JTable
                    int selectedRow = tablaRegistros.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(this, "Por favor, seleccione una tabla de la lista para exportar a SQL.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Obtener el nombre de la base de datos y la tabla de la fila seleccionada
                    String dbForSqlExport = (String) modelo.getValueAt(selectedRow, 1); // Columna "Base de Datos"
                    String tableForSqlExport = (String) modelo.getValueAt(selectedRow, 2); // Columna "Nombre de Tabla"

                    if (dbForSqlExport == null || tableForSqlExport == null || dbForSqlExport.isEmpty() || tableForSqlExport.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No se pudo determinar la base de datos o la tabla para exportar a SQL.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    exportarSQLDesdeBD(fileToSave, dbForSqlExport, tableForSqlExport);

                } else { // Para Excel, PDF, Word, se exporta el contenido visible de la JTable
                    switch (format) {
                        case EXCEL:
                            exportToExcel(fileToSave);
                            break;
                        case PDF:
                            exportToPdf(fileToSave);
                            break;
                        case WORD:
                            exportToWord(fileToSave);
                            break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Datos exportados exitosamente a:\n" + fileToSave.getAbsolutePath(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar los datos: " + ex.getMessage() + "\nPor favor, asegúrese de que todas las librerías (JARs) necesarias estén correctamente añadidas a su proyecto y que sean compatibles.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void exportToExcel(File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Datos");

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < modelo.getColumnCount(); col++) {
                headerRow.createCell(col).setCellValue(modelo.getColumnName(col));
            }

            for (int row = 0; row < modelo.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < modelo.getColumnCount(); col++) {
                    Object value = modelo.getValueAt(row, col);
                    if (value != null) {
                        dataRow.createCell(col).setCellValue(value.toString());
                    } else {
                        dataRow.createCell(col).setCellValue("");
                    }
                }
            }

            for (int i = 0; i < modelo.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
        }
    }

    private void exportToPdf(File file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();

            // Título del PDF
            com.itextpdf.text.Font fontTitle = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16, com.itextpdf.text.BaseColor.BLACK);
            Paragraph title = new Paragraph("Informe de Datos - " + getTitle(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable pdfTable = new PdfPTable(modelo.getColumnCount());
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);

            // Añadir encabezados
            com.itextpdf.text.Font fontHeader = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10, com.itextpdf.text.BaseColor.WHITE);
            for (int col = 0; col < modelo.getColumnCount(); col++) {
                PdfPCell cell = new PdfPCell(new Phrase(modelo.getColumnName(col), fontHeader));
                cell.setBackgroundColor(com.itextpdf.text.BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            // Añadir filas
            com.itextpdf.text.Font fontData = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 9, com.itextpdf.text.BaseColor.BLACK);
            for (int row = 0; row < modelo.getRowCount(); row++) {
                for (int col = 0; col < modelo.getColumnCount(); col++) {
                    Object value = modelo.getValueAt(row, col);
                    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", fontData));
                    cell.setPadding(4);
                    pdfTable.addCell(cell);
                }
            }
            document.add(pdfTable);
            document.close();
        }
    }

    private void exportToWord(File file) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream fos = new FileOutputStream(file)) {

            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Informe de Datos - " + getTitle());
            titleRun.setFontSize(16);
            titleRun.setBold(true);
            titleRun.addBreak();

            XWPFTable table = document.createTable(modelo.getRowCount() + 1, modelo.getColumnCount());

            XWPFTableRow headerRow = table.getRow(0);
            for (int col = 0; col < modelo.getColumnCount(); col++) {
                if (headerRow.getCell(col) == null) {
                    headerRow.addNewTableCell();
                }
                headerRow.getCell(col).setText(modelo.getColumnName(col));
                headerRow.getCell(col).getCTTc().addNewTcPr().addNewShd().setFill("C0C0C0");
                headerRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewTop().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                headerRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewBottom().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                headerRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewLeft().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                headerRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewRight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
            }

            for (int row = 0; row < modelo.getRowCount(); row++) {
                XWPFTableRow dataRow = table.getRow(row + 1);
                for (int col = 0; col < modelo.getColumnCount(); col++) {
                    if (dataRow.getCell(col) == null) {
                        dataRow.addNewTableCell();
                    }
                    Object value = modelo.getValueAt(row, col);
                    dataRow.getCell(col).setText(value != null ? value.toString() : "");
                    dataRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewTop().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                    dataRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewBottom().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                    dataRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewLeft().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                    dataRow.getCell(col).getCTTc().addNewTcPr().addNewTcBorders().addNewRight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
                }
            }
            document.write(fos);
        }
    }

    // --- Función para exportar a SQL, similar a la de ClaseInforme.java ---
    private void exportarSQLDesdeBD(File file, String dbName, String tableName) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName, "root", "aAWV1c10YuHTQkmp");
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`")) { // Usa el nombre de la tabla de la base de datos

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("-- SQL Export from MySQL Database: " + dbName);
                writer.println("-- Table: " + tableName);
                writer.println("-- Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println();

                // Opcional: Agregar sentencia DROP TABLE e CREATE TABLE
                writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");
                writer.println("CREATE TABLE `" + tableName + "` (");
                for (int i = 1; i <= columnCount; i++) {
                    writer.print("  `" + metaData.getColumnName(i) + "` " + metaData.getColumnTypeName(i));
                    // Intenta obtener información de nulabilidad y auto_increment
                    try {
                        if (metaData.isNullable(i) == ResultSetMetaData.columnNoNulls) {
                            writer.print(" NOT NULL");
                        }
                    } catch (SQLException e) {
                        // Ignorar si no se puede obtener la información de nulabilidad (algunos drivers lo implementan mal)
                    }
                    try {
                        if (metaData.isAutoIncrement(i)) {
                            writer.print(" AUTO_INCREMENT");
                        }
                    } catch (SQLException e) {
                        // Ignorar si no se puede obtener la información de auto_increment
                    }

                    if (i < columnCount) {
                        writer.println(",");
                    } else {
                        writer.println(""); // Última columna no tiene coma
                    }
                }
                // Aquí podrías agregar PRIMARY KEY, UNIQUE, etc., si los obtuvieras de INFORMATION_SCHEMA
                // Esto requiere una consulta adicional a information_schema.KEY_COLUMN_USAGE y information_schema.TABLE_CONSTRAINTS
                writer.println(");");
                writer.println();


                // Generar sentencias INSERT
                while (rs.next()) {
                    StringBuilder insertStatement = new StringBuilder("INSERT INTO ");
                    insertStatement.append("`").append(tableName).append("` (");

                    // Añadir nombres de columnas
                    for (int i = 1; i <= columnCount; i++) {
                        insertStatement.append("`").append(metaData.getColumnName(i)).append("`");
                        if (i < columnCount) {
                            insertStatement.append(", ");
                        }
                    }
                    insertStatement.append(") VALUES (");

                    // Añadir valores de columnas
                    for (int i = 1; i <= columnCount; i++) {
                        String value = rs.getString(i);
                        if (value == null) {
                            insertStatement.append("NULL");
                        } else {
                            // Escapar comillas simples para valores de cadena
                            insertStatement.append("'").append(value.replace("'", "''")).append("'");
                        }
                        if (i < columnCount) {
                            insertStatement.append(", ");
                        }
                    }
                    insertStatement.append(");");
                    writer.println(insertStatement.toString());
                }
            }
        }
    }


    private void actualizarReloj() {
        javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Aquí corregí el "void void"
                LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Mexico_City"));
                now = now.minusHours(1); // Esta línea resta una hora
                relojLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        });
        timer.start();
    }
}