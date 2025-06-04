package proyecto.pkgfinal;


import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter; // Necesario para escribir en archivos de texto
import java.io.PrintWriter; // Para escribir líneas de texto de forma eficiente
import java.sql.*;

// Importaciones para Apache POI (Excel y Word)
import javax.swing.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * Carrillo Viveros Juan Carlos 217o02237 y Luna Zamora Marlleli 217o00552
 * La clase ClaseInforme permite exportar datos de tablas de una base de datos MySQL a diferentes formatos de archivo. Sus funciones principales son:
 * - **Interfaz de Usuario**: Proporciona una interfaz gráfica para seleccionar la base de datos, tabla y el formato de exportación (PDF, Excel, Word, SQL).
 * - **Conectividad a MySQL**: Se conecta a la base de datos MySQL para obtener los datos de la tabla seleccionada.
 * - **Exportación a PDF**: Genera un documento PDF con los datos de la tabla en formato de tabla.
 * - **Exportación a Excel**: Crea un archivo de Excel (XLSX) con los datos organizados en filas y columnas.
 * - **Exportación a Word**: Produce un documento de Word (DOCX) con los datos en formato de tabla.
 * - **Exportación a SQL**: Genera un archivo `.sql` que contiene sentencias `INSERT INTO` para replicar los datos de la tabla.
 * - **Manejo de Archivos**: Permite al usuario seleccionar la ruta donde se guardará el archivo exportado y gestiona la creación de dichos archivos.
 * - **Gestión de Errores**: Muestra mensajes de error al usuario si hay problemas de conexión, selección incompleta o fallos durante la exportación.
 */
public class ClaseInforme extends JFrame {

    public ClaseInforme() {
        setTitle("Generar Informe");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        // Componentes de la interfaz
        // ¡Aquí se añade "SQL" como una opción!
        JComboBox<String> formatos = new JComboBox<>(new String[]{"PDF", "Excel", "Word", "SQL"});
        JComboBox<String> bases = new JComboBox<>();
        JComboBox<String> tablas = new JComboBox<>();
        JTextField rutaExportacion = new JTextField();
        rutaExportacion.setEditable(false);
        JButton seleccionarRuta = new JButton("Seleccionar ruta de exportación");
        JButton exportar = new JButton("Exportar");

        // Agregar componentes al panel
        mainPanel.add(new JLabel("Formato de exportación:"));
        mainPanel.add(formatos);
        mainPanel.add(new JLabel("Base de datos:"));
        mainPanel.add(bases);
        mainPanel.add(new JLabel("Tabla:"));
        mainPanel.add(tablas);
        mainPanel.add(seleccionarRuta);
        mainPanel.add(rutaExportacion);
        mainPanel.add(exportar);

        // Cargar bases de datos
        cargarBasesDeDatos(bases);

        // Eventos
        bases.addActionListener(e -> cargarTablas(bases, tablas));
        
        seleccionarRuta.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaExportacion.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        exportar.addActionListener(e -> {
            String formato = (String) formatos.getSelectedItem();
            String bd = (String) bases.getSelectedItem();
            String tabla = (String) tablas.getSelectedItem();
            String ruta = rutaExportacion.getText();

            if (bd == null || tabla == null || ruta.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                exportarTabla(formato, bd, tabla, ruta);
                JOptionPane.showMessageDialog(this, "Archivo exportado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage() + "\nConsulte la consola para más detalles.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprimir el stack trace completo para depuración
            }
        });
    }

    private void cargarBasesDeDatos(JComboBox<String> bases) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "aAWV1c10YuHTQkmp");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
            
            while (rs.next()) {
                String db = rs.getString(1);
                // Filtrar bases de datos del sistema
                if (!db.equals("information_schema") && !db.equals("mysql") && 
                    !db.equals("performance_schema") && !db.equals("sys")) {
                    bases.addItem(db);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar bases: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarTablas(JComboBox<String> bases, JComboBox<String> tablas) {
        tablas.removeAllItems(); // Limpiar ítems anteriores
        String bd = (String) bases.getSelectedItem();
        if (bd == null) return;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + bd, "root", "aAWV1c10YuHTQkmp");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES FROM " + bd)) {
            
            while (rs.next()) {
                tablas.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar tablas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportarTabla(String formato, String bd, String tabla, String ruta) throws Exception {
        File dir = new File(ruta);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new Exception("La ruta de exportación no es válida o no existe.");
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + bd, "root", "aAWV1c10YuHTQkmp");
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            String nombreArchivo = ruta + File.separator + tabla + "." + formato.toLowerCase();
            
            switch (formato) {
                case "PDF":
                    exportarAPDF(rs, metaData, columnCount, nombreArchivo);
                    break;
                case "Excel":
                    exportarAExcel(rs, metaData, columnCount, nombreArchivo);
                    break;
                case "Word":
                    exportarAWord(rs, metaData, columnCount, nombreArchivo);
                    break;
                case "SQL": // ¡Nuevo caso para exportar a SQL!
                    exportarASQL(rs, metaData, columnCount, nombreArchivo);
                    break;
                default:
                    throw new IllegalArgumentException("Formato de exportación no soportado: " + formato);
            }
        }
    }

    private void exportarAPDF(ResultSet rs, ResultSetMetaData metaData, int columnCount, String nombreArchivo) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
        document.open();

        document.add(new Paragraph("Informe de la Tabla: " + metaData.getTableName(1)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);

        // Agregar encabezados
        for (int i = 1; i <= columnCount; i++) {
            table.addCell(new Phrase(metaData.getColumnName(i)));
        }

        // Agregar datos
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                table.addCell(new Phrase(rs.getString(i) != null ? rs.getString(i) : ""));
            }
        }

        document.add(table);
        document.close();
    }

    private void exportarAExcel(ResultSet rs, ResultSetMetaData metaData, int columnCount, String nombreArchivo) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Datos");

        // Crear fila de encabezados
        Row headerRow = sheet.createRow(0);
        for (int i = 1; i <= columnCount; i++) {
            headerRow.createCell(i-1).setCellValue(metaData.getColumnName(i));
        }

        // Agregar datos
        int rowNum = 1;
        while (rs.next()) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 1; i <= columnCount; i++) {
                row.createCell(i-1).setCellValue(rs.getString(i) != null ? rs.getString(i) : "");
            }
        }

        // Autoajustar el ancho de las columnas
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir archivo
        try (FileOutputStream outputStream = new FileOutputStream(nombreArchivo)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    private void exportarAWord(ResultSet rs, ResultSetMetaData metaData, int columnCount, String nombreArchivo) throws Exception {
        XWPFDocument document = new XWPFDocument();
        
        document.createParagraph().createRun().setText("Informe de la Tabla: " + metaData.getTableName(1));
        document.createParagraph().createRun().setText("\n");

        XWPFTable table = document.createTable();
        
        // Agregar encabezados
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 1; i <= columnCount; i++) {
            if (i == 1) {
                headerRow.getCell(0).setText(metaData.getColumnName(i));
            } else {
                headerRow.addNewTableCell().setText(metaData.getColumnName(i));
            }
        }

        // Agregar datos
        while (rs.next()) {
            XWPFTableRow row = table.createRow();
            for (int i = 1; i <= columnCount; i++) {
                if (row.getCell(i-1) == null) {
                    row.addNewTableCell();
                }
                row.getCell(i-1).setText(rs.getString(i) != null ? rs.getString(i) : "");
            }
        }

        // Escribir archivo
        try (FileOutputStream out = new FileOutputStream(nombreArchivo)) {
            document.write(out);
        }
        document.close();
    }

    // --- Nueva función para exportar a SQL ---
    private void exportarASQL(ResultSet rs, ResultSetMetaData metaData, int columnCount, String nombreArchivo) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            String tableName = metaData.getTableName(1); // Obtiene el nombre de la tabla

            // Se generan sentencias INSERT INTO para los datos
            while (rs.next()) {
                StringBuilder insertStatement = new StringBuilder("INSERT INTO ");
                insertStatement.append(tableName).append(" (");

                // Añadir nombres de columnas
                for (int i = 1; i <= columnCount; i++) {
                    insertStatement.append("`").append(metaData.getColumnName(i)).append("`"); // Usamos comillas para nombres de columnas
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