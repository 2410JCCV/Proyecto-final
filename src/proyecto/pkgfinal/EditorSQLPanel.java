package proyecto.pkgfinal;

import com.formdev.flatlaf.FlatLightLaf;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.LinkedHashSet;
import proyecto.pkgfinal.AsistenteIA;

/**
 * La clase `EditorSQLPanel` proporciona un editor de SQL con funcionalidades para ejecutar consultas,
 * mostrar resultados en una tabla, gestionar un historial de consultas, seleccionar bases de datos y tablas,
 * y recibir asistencia de una IA(la ia para que funciones necesita API key Funcional).
 */
public class EditorSQLPanel extends JPanel {
    private Runnable onChangeCallback;

    public EditorSQLPanel(Runnable onChangeCallback) {
        this();
        this.onChangeCallback = onChangeCallback;
    }


    private RSyntaxTextArea sqlArea;
    private JTable resultadoTabla;
    private DefaultTableModel tableModel;
    private JComboBox<String> historialCombo;
    private final LinkedHashSet<String> historial = new LinkedHashSet<>();
    public JComboBox<String> comboBasesDatos;
    public JComboBox<String> comboTablas;

    private static final String URL_SERVER = "jdbc:mysql://localhost:3306/";
    private static final String USUARIO = "root";
    private static final String CLAVE = "aAWV1c10YuHTQkmp";
    private static final String ARCHIVO_ULTIMA_BD = "ultima_bd.txt";

    public EditorSQLPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(titulo(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
        add(crearPanelLateral(), BorderLayout.WEST);

        cargarBasesDatos();
        cargarUltimaBaseUsada();
    }

    private Component titulo() {
        JPanel panelTitulo = new JPanel(new BorderLayout(5, 5));
        panelTitulo.setBackground(Color.WHITE);

        JLabel label = new JLabel("Editor SQL", SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel panelBD = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBD.setBackground(Color.WHITE);

        JLabel lblBD = new JLabel("Base de datos:");
        comboBasesDatos = new JComboBox<>();
        comboBasesDatos.setEditable(true);
        comboBasesDatos.setPreferredSize(new Dimension(150, 25));
        comboBasesDatos.addActionListener(e -> cargarTablasBaseDatos());

        comboTablas = new JComboBox<>();
        comboTablas.setPreferredSize(new Dimension(150, 25));
        comboTablas.addActionListener(e -> mostrarEstructuraTabla());

        JButton btnCrearBD = new JButton("Nueva BD");
        btnCrearBD.addActionListener(e -> crearNuevaBaseDatos());

        panelBD.add(lblBD);
        panelBD.add(comboBasesDatos);
        panelBD.add(new JLabel("Tabla:"));
        panelBD.add(comboTablas);
        panelBD.add(btnCrearBD);

        panelTitulo.add(label, BorderLayout.WEST);
        panelTitulo.add(panelBD, BorderLayout.EAST);

        return panelTitulo;
    }

    private void cargarTablasBaseDatos() {
        comboTablas.removeAllItems();
        String baseDatos = (String) comboBasesDatos.getSelectedItem();
        if (baseDatos == null || baseDatos.isEmpty()) return;

        try (Connection conn = DriverManager.getConnection(URL_SERVER + baseDatos, USUARIO, CLAVE);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            while (rs.next()) {
                comboTablas.addItem(rs.getString(1));
            }

            // Si solo hay una tabla, seleccionarla automáticamente
            if (comboTablas.getItemCount() == 1) {
                comboTablas.setSelectedIndex(0);
                mostrarEstructuraTabla();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando tablas: " + ex.getMessage());
        }
    }

    public void mostrarEstructuraTabla() {
        String baseDatos = (String) comboBasesDatos.getSelectedItem();
        String tabla = (String) comboTablas.getSelectedItem();
        if (baseDatos == null || tabla == null) return;

        try (Connection conn = DriverManager.getConnection(URL_SERVER + baseDatos, USUARIO, CLAVE);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("DESCRIBE " + tabla)) {

            StringBuilder estructura = new StringBuilder("-- Estructura de tabla: " + tabla + "\n");
            estructura.append("CREATE TABLE ").append(tabla).append(" (\n");

            while (rs.next()) {
                String campo = rs.getString("Field");
                String tipo = rs.getString("Type");
                String nulo = rs.getString("Null");
                String clave = rs.getString("Key");
                String defecto = rs.getString("Default");
                String extra = rs.getString("Extra");

                estructura.append("  ").append(campo).append(" ").append(tipo);
                if ("NO".equals(nulo)) estructura.append(" NOT NULL");
                if (defecto != null) estructura.append(" DEFAULT '").append(defecto).append("'");
                if ("PRI".equals(clave)) estructura.append(" PRIMARY KEY");
                if (!extra.isEmpty()) estructura.append(" ").append(extra);
                estructura.append(",\n");
            }

            estructura.delete(estructura.length()-2, estructura.length()); // Eliminar última coma
            estructura.append("\n);");

            sqlArea.setText(estructura.toString());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error obteniendo estructura: " + ex.getMessage());
        }
    }

    private void crearNuevaBaseDatos() {
        String nombreBD = JOptionPane.showInputDialog(this, "Nombre de la nueva base de datos:");
        if (nombreBD == null || nombreBD.trim().isEmpty()) return;

        try (Connection conn = DriverManager.getConnection(URL_SERVER, USUARIO, CLAVE);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE " + nombreBD);
            JOptionPane.showMessageDialog(this, "Base de datos '" + nombreBD + "' creada exitosamente.");
            cargarBasesDatos();
            comboBasesDatos.setSelectedItem(nombreBD);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error creando base de datos: " + ex.getMessage());
        }
    }

    // ... (los demás métodos permanecen igual: crearCentro, crearPanelInferior, 
    // crearPanelLateral, crearBotonPlantilla, ejecutarConsulta, cargarResultados,
    // cargarBasesDatos, guardarUltimaBaseUsada, cargarUltimaBaseUsada, main)
    
    private Component crearCentro() {
        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.setBackground(Color.WHITE);

        sqlArea = new RSyntaxTextArea(10, 60);
        sqlArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        sqlArea.setCodeFoldingEnabled(true);
        sqlArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        centro.add(new RTextScrollPane(sqlArea), BorderLayout.CENTER);

        tableModel = new DefaultTableModel();
        resultadoTabla = new JTable(tableModel);
        JScrollPane scrollTabla = new JScrollPane(resultadoTabla);
        scrollTabla.setPreferredSize(new Dimension(800, 200));
        centro.add(scrollTabla, BorderLayout.SOUTH);

        return centro;
    }

    private Component crearPanelInferior() {
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBotones.setBackground(Color.WHITE);

    historialCombo = new JComboBox<>();
    historialCombo.setPreferredSize(new Dimension(250, 25));
    historialCombo.addActionListener(e -> {
        String seleccion = (String) historialCombo.getSelectedItem();
        if (seleccion != null) {
            sqlArea.setText(seleccion);
        }
    });

    // 👉 Botón del Asistente IA
    JButton botonIA = new JButton("Asistente IA");
    botonIA.addActionListener(e -> AsistenteIA.responderDesdeEditor(sqlArea.getText()));
    panelBotones.add(botonIA); // agrégalo al panel

    // Otros botones
    JButton ejecutar = new JButton("Ejecutar SQL");
    ejecutar.addActionListener(e -> ejecutarConsulta());

    JButton limpiarEditor = new JButton("Limpiar Editor");
    limpiarEditor.addActionListener(e -> sqlArea.setText(""));

    JButton limpiarTabla = new JButton("Limpiar Resultados");
    limpiarTabla.addActionListener(e -> {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    });

    // Añadir componentes al panel
    panelBotones.add(new JLabel("Historial:"));
    panelBotones.add(historialCombo);
    panelBotones.add(ejecutar);
    panelBotones.add(limpiarEditor);
    panelBotones.add(limpiarTabla);

    return panelBotones;
}


    private Component crearPanelLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Plantillas"));

        panel.add(crearBotonPlantilla("CREATE TABLE ejemplo (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(50));"));
        panel.add(crearBotonPlantilla("INSERT INTO ejemplo (nombre) VALUES ('Juan');"));
        panel.add(crearBotonPlantilla("SELECT * FROM ejemplo;"));
        panel.add(crearBotonPlantilla("UPDATE ejemplo SET nombre = 'Carlos' WHERE id = 1;"));
        panel.add(crearBotonPlantilla("DELETE FROM ejemplo WHERE id = 1;"));

        return panel;
    }

    private JButton crearBotonPlantilla(String sql) {
        String titulo = sql.split(" ")[0] + "...";
        JButton btn = new JButton(titulo);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 30));
        btn.addActionListener(e -> sqlArea.setText(sql));
        return btn;
    }

    private void ejecutarConsulta() {
        String consulta = sqlArea.getText().trim();
        if (consulta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La consulta está vacía.");
            return;
        }

        String baseDeDatos = ((String) comboBasesDatos.getEditor().getItem()).trim();
        if (baseDeDatos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona o escribe el nombre de la base de datos.");
            return;
        }

        guardarUltimaBaseUsada(baseDeDatos);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        }

        if (!historial.contains(consulta)) {
            historial.add(consulta);
            historialCombo.addItem(consulta);
        }

        String url = URL_SERVER + baseDeDatos;

        try (Connection conn = DriverManager.getConnection(url, USUARIO, CLAVE);
             Statement stmt = conn.createStatement()) {

            boolean esConsulta = stmt.execute(consulta);

            if (esConsulta) {
                ResultSet rs = stmt.getResultSet();
                cargarResultados(rs);
            } else {
                int filas = stmt.getUpdateCount();
                JOptionPane.showMessageDialog(this, filas + " filas afectadas.");
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarResultados(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        String[] headers = new String[columnas];
        for (int i = 1; i <= columnas; i++) {
            headers[i - 1] = meta.getColumnName(i);
        }

        tableModel.setColumnIdentifiers(headers);
        tableModel.setRowCount(0);

        while (rs.next()) {
            Object[] fila = new Object[columnas];
            for (int i = 1; i <= columnas; i++) {
                fila[i - 1] = rs.getObject(i);
            }
            tableModel.addRow(fila);
        }
    }

    private void cargarBasesDatos() {
        comboBasesDatos.removeAllItems();
        try (Connection conn = DriverManager.getConnection(URL_SERVER, USUARIO, CLAVE);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {

            while (rs.next()) {
                String bd = rs.getString(1);
                if (!bd.equalsIgnoreCase("information_schema") && 
                    !bd.equalsIgnoreCase("mysql") && 
                    !bd.equalsIgnoreCase("performance_schema") && 
                    !bd.equalsIgnoreCase("sys")) {
                    comboBasesDatos.addItem(bd);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando bases de datos: " + ex.getMessage());
        }
    }

    private void guardarUltimaBaseUsada(String base) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_ULTIMA_BD))) {
            pw.println(base);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la base de datos: " + e.getMessage());
        }
    }

    private void cargarUltimaBaseUsada() {
        File file = new File(ARCHIVO_ULTIMA_BD);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ultima = br.readLine();
            if (ultima != null && !ultima.trim().isEmpty()) {
                comboBasesDatos.addItem(ultima);
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar la base de datos: " + e.getMessage());
        }
    }
}