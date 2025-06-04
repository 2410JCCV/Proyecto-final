package proyecto.pkgfinal;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

/**
 * Carrillo Viveros Juan Carlos 217o02237 y Luna Zamora Marlleli 217o00552
 * La clase ClaseGrafica se encarga de generar gráficas de barras a partir de datos de una base de datos MySQL. Sus funciones principales son:
 * - **Mostrar un selector de base de datos, tabla y columnas para la gráfica**: Permite al usuario elegir qué datos visualizar mediante menús desplegables.
 * - **Conectarse a la base de datos**: Establece una conexión JDBC con MySQL para acceder a los datos.
 * - **Recuperar y procesar datos**: Extrae los datos de la tabla y columnas seleccionadas, filtrando tipos numéricos para el eje de valores.
 * - **Generar y mostrar gráficas de barras**: Utiliza la librería JFreeChart para crear y visualizar gráficas de barras con los datos obtenidos.
 */
public class ClaseGrafica {

    private static Connection conexionGlobal;

    /**
     * Muestra una ventana de selección para que el usuario elija la base de datos, tabla y columnas
     * para generar una gráfica de barras.
     */
    public static void mostrarSelectorYGrafica() {
        JFrame selectorFrame = new JFrame("Seleccionar Datos y Generar Gráfica");
        selectorFrame.setSize(500, 300);
        selectorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        selectorFrame.setLocationRelativeTo(null);
        selectorFrame.setLayout(new GridLayout(5, 2, 10, 10));

        // Componentes de la interfaz de selección
        final JComboBox<String> comboBases = new JComboBox<>();
        final JComboBox<String> comboTablas = new JComboBox<>();
        final JComboBox<String> comboColumnasCategoria = new JComboBox<>();
        final JComboBox<String> comboColumnasValor = new JComboBox<>();
        JButton btnGenerarGrafica = new JButton("Generar Gráfica");

        // --- Configuración de la interfaz de selección ---
        selectorFrame.add(new JLabel("Base de Datos:"));
        selectorFrame.add(comboBases);

        selectorFrame.add(new JLabel("Tabla:"));
        selectorFrame.add(comboTablas);

        selectorFrame.add(new JLabel("Columna para Categoría (Eje X):"));
        selectorFrame.add(comboColumnasCategoria);

        selectorFrame.add(new JLabel("Columna para Valor (Eje Y):"));
        selectorFrame.add(comboColumnasValor);

        selectorFrame.add(btnGenerarGrafica);
        selectorFrame.add(new JLabel("")); 

        // --- Lógica de Conexión y Carga Inicial ---
        conectarMySQL(selectorFrame); 
        cargarBasesDeDatos(comboBases, selectorFrame);

        // --- Listeners para la interacción del usuario ---
        comboBases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTablas(comboBases, comboTablas, comboColumnasCategoria, comboColumnasValor, selectorFrame);
            }
        });

        comboTablas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarColumnas(comboBases, comboTablas, comboColumnasCategoria, comboColumnasValor, selectorFrame);
            }
        });

        btnGenerarGrafica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener los valores seleccionados justo antes de generar la gráfica
                String dbName = (String) comboBases.getSelectedItem();
                String tableName = (String) comboTablas.getSelectedItem();
                String categoryColumn = (String) comboColumnasCategoria.getSelectedItem();
                String valueColumn = (String) comboColumnasValor.getSelectedItem();
                
                // Llamar al método de la gráfica con los valores obtenidos
                mostrarGraficaSeleccionada(dbName, tableName, categoryColumn, valueColumn, selectorFrame);
            }
        });

        selectorFrame.setVisible(true);
    }

    /**
     * Establece una conexión global a la base de datos MySQL.
     * Muestra un mensaje de error si la conexión falla.
     * @param parentComponent El componente padre para mostrar diálogos de error.
     */
    private static void conectarMySQL(Component parentComponent) {
        try {
            conexionGlobal = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "aAWV1c10YuHTQkmp");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponent,
                "Error al conectar con MySQL: " + e.getMessage() + "\nAsegúrate de que MySQL está corriendo y las credenciales son correctas.",
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Carga los nombres de las bases de datos disponibles en un JComboBox.
     * Excluye las bases de datos del sistema.
     * @param comboBases El JComboBox donde se cargarán los nombres de las bases de datos.
     * @param parentComponent El componente padre para mostrar diálogos.
     */
    private static void cargarBasesDeDatos(JComboBox<String> comboBases, Component parentComponent) {
        comboBases.removeAllItems();
        if (conexionGlobal == null) return;

        try (Statement stmt = conexionGlobal.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
            while (rs.next()) {
                String dbName = rs.getString(1);
                if (!dbName.equals("information_schema") && !dbName.equals("mysql") &&
                    !dbName.equals("performance_schema") && !dbName.equals("sys")) {
                    comboBases.addItem(dbName);
                }
            }
            if (comboBases.getItemCount() > 0) {
                comboBases.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(parentComponent, "No se encontraron bases de datos de usuario.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponent, "Error al cargar bases de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Carga los nombres de las tablas de la base de datos seleccionada en un JComboBox.
     * También limpia y recarga los JComboBox de columnas.
     * @param comboBases JComboBox de bases de datos.
     * @param comboTablas JComboBox donde se cargarán los nombres de las tablas.
     * @param comboColumnasCategoria JComboBox de columnas para la categoría.
     * @param comboColumnasValor JComboBox de columnas para el valor.
     * @param parentComponent El componente padre para mostrar diálogos.
     */
    private static void cargarTablas(JComboBox<String> comboBases, JComboBox<String> comboTablas,
                                      JComboBox<String> comboColumnasCategoria, JComboBox<String> comboColumnasValor,
                                      Component parentComponent) {
        comboTablas.removeAllItems();
        comboColumnasCategoria.removeAllItems(); 
        comboColumnasValor.removeAllItems();     

        String selectedDb = (String) comboBases.getSelectedItem();
        if (selectedDb == null || conexionGlobal == null) return;

        try (Statement stmt = conexionGlobal.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES FROM `" + selectedDb + "`")) {
            while (rs.next()) {
                comboTablas.addItem(rs.getString(1));
            }
            if (comboTablas.getItemCount() > 0) {
                comboTablas.setSelectedIndex(0);
            }
            cargarColumnas(comboBases, comboTablas, comboColumnasCategoria, comboColumnasValor, parentComponent);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponent, "Error al cargar tablas de '" + selectedDb + "': " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Carga los nombres de las columnas de la tabla seleccionada en los JComboBoxes de columnas.
     * Distingue entre columnas numéricas para el eje de valores y todas las columnas para el eje de categorías.
     * @param comboBases JComboBox de bases de datos.
     * @param comboTablas JComboBox de tablas.
     * @param comboColumnasCategoria JComboBox donde se cargarán las columnas para la categoría.
     * @param comboColumnasValor JComboBox donde se cargarán las columnas para el valor (solo numéricas).
     * @param parentComponent El componente padre para mostrar diálogos.
     */
    private static void cargarColumnas(JComboBox<String> comboBases, JComboBox<String> comboTablas,
                                       JComboBox<String> comboColumnasCategoria, JComboBox<String> comboColumnasValor,
                                       Component parentComponent) {
        comboColumnasCategoria.removeAllItems();
        comboColumnasValor.removeAllItems();

        String selectedDb = (String) comboBases.getSelectedItem();
        String selectedTable = (String) comboTablas.getSelectedItem();
        if (selectedDb == null || selectedTable == null || conexionGlobal == null) return;

        try {
            DatabaseMetaData metaData = conexionGlobal.getMetaData();
            ResultSet columns = metaData.getColumns(selectedDb, null, selectedTable, null);

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int dataType = columns.getInt("DATA_TYPE");

                comboColumnasCategoria.addItem(columnName);

                if (isNumericType(dataType)) {
                    comboColumnasValor.addItem(columnName);
                }
            }

            if (comboColumnasCategoria.getItemCount() > 0) {
                comboColumnasCategoria.setSelectedIndex(0);
            } else {
                 comboColumnasCategoria.removeAllItems();
                 comboColumnasValor.removeAllItems();
            }
            if (comboColumnasValor.getItemCount() > 0) comboColumnasValor.setSelectedIndex(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponent, "Error al cargar columnas de '" + selectedTable + "': " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Verifica si un tipo de dato SQL es numérico.
     * @param dataType El tipo de dato SQL (java.sql.Types).
     * @return true si el tipo de dato es numérico, false en caso contrario.
     */
    private static boolean isNumericType(int dataType) {
        return dataType == Types.BIGINT || dataType == Types.DECIMAL ||
               dataType == Types.DOUBLE || dataType == Types.FLOAT ||
               dataType == Types.INTEGER || dataType == Types.NUMERIC ||
               dataType == Types.REAL || dataType == Types.SMALLINT ||
               dataType == Types.TINYINT;
    }

    /**
     * Genera y muestra una gráfica de barras con los datos seleccionados por el usuario.
     * @param dbName Nombre de la base de datos seleccionada.
     * @param tableName Nombre de la tabla seleccionada.
     * @param categoryColumn Nombre de la columna para el eje de categorías (eje X).
     * @param valueColumn Nombre de la columna para el eje de valores (eje Y).
     * @param parentComponent El componente padre para mostrar diálogos y centrar la gráfica.
     */
    private static void mostrarGraficaSeleccionada(String dbName, String tableName, String categoryColumn, String valueColumn, Component parentComponent) {
        if (dbName == null || tableName == null || categoryColumn == null || valueColumn == null || categoryColumn.isEmpty() || valueColumn.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Por favor, selecciona una base de datos, tabla y ambas columnas. Asegúrate de que la tabla tiene al menos una columna de texto y una numérica.", "Selección Incompleta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "SELECT `" + categoryColumn + "`, `" + valueColumn + "` FROM `" + tableName + "`";

        Connection chartConnection = null;
        try {
            chartConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName, "root", "aAWV1c10YuHTQkmp");
            Statement stmt = chartConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String category = rs.getString(categoryColumn);
                Number value = rs.getDouble(valueColumn);
                dataset.addValue(value, valueColumn, category);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponent,
                "Error al cargar datos para la gráfica: " + e.getMessage() +
                "\nAsegúrate de que la columna '" + valueColumn + "' es numérica y que los datos son accesibles.",
                "Error de Datos de Gráfica", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent,
                "Ocurrió un error inesperado al procesar los datos para la gráfica: " + e.getMessage(),
                "Error General de Gráfica", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        } finally {
            if (chartConnection != null) {
                try {
                    chartConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            valueColumn + " por " + categoryColumn + " en la tabla " + tableName + " de la BD " + dbName,
            categoryColumn,
            valueColumn,
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        JFrame chartFrame = new JFrame("Gráfica: " + valueColumn + " vs " + categoryColumn);
        chartFrame.setSize(800, 600);
        chartFrame.setLocationRelativeTo(parentComponent);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        ChartPanel chartPanel = new ChartPanel(chart);
        chartFrame.setContentPane(chartPanel);
        chartFrame.setVisible(true);
    }
}