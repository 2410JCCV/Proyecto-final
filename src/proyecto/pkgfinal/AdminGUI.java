 /**
    Carrillo Viveros Juan Carlos 217o02237 y Luna Zamora Marlleli 217o00552
    La clase AdminGUI es una aplicación Java Swing que simula un administrador de permisos de MySQL. Permite a los usuarios:
    Crear y Eliminar Usuarios: Gestiona la creación y eliminación de usuarios de MySQL.
    Otorgar y Revocar Permisos: Asigna o quita privilegios específicos (de datos, estructura o administración) a los usuarios sobre bases de datos o tablas.
    Interfaz Intuitiva: Ofrece una interfaz gráfica con campos de texto y checkboxes para facilitar la administración de permisos.
    Conexión a Base de Datos: Se conecta a una base de datos MySQL para ejecutar las operaciones.
*/

package proyecto.pkgfinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminGUI extends JFrame {

    // --- Configuración de la Conexión a la Base de Datos ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mysql"; // La base de datos 'mysql' contiene la información de usuarios
    private static final String DB_USER = "root"; // Usuario root de phpMyAdmin
    private static final String DB_PASS = "aAWV1c10YuHTQkmp"; // ¡CAMBIA ESTO POR TU CONTRASEÑA DE ROOT REAL!

    // --- Componentes de la GUI ---
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtTarget; // Para base_de_datos.tabla o base_de_datos.*
    private JCheckBox cbGlobalPrivileges; // Checkbox para seleccionar '*.*'

    // Checkboxes para privilegios de Datos
    private JCheckBox cbSelect, cbInsert, cbUpdate, cbDelete, cbFile;

    // Checkboxes para privilegios de Estructura
    private JCheckBox cbCreate, cbAlter, cbIndex, cbDrop, cbCreateTempTables,
            cbShowView, cbCreateRoutine, cbAlterRoutine, cbExecute, cbCreateView, cbEvent, cbTrigger;

    // Checkboxes para privilegios de Administración
    private JCheckBox cbGrant, cbProcess, cbReload, cbShutdown, cbSuper, cbShowDatabases,
            cbLockTables, cbReferences, cbReplicationClient, cbReplicationSlave, cbCreateUser;

    private JTextArea taOutput; // Para mostrar mensajes de estado

    // Una lista para almacenar todos los checkboxes de privilegios y facilitar la limpieza
    private List<JCheckBox> privilegeCheckBoxes;


    // --- Constructor de la Clase ---
    public AdminGUI() {
        setTitle("Administrador de Permisos MySQL (phpMyAdmin-like)");
        setSize(800, 700);
        setLocationRelativeTo(null); // Centrar la ventana

        initComponents();
        setupLayout();
        addListeners();
    }

    // --- Métodos de Inicialización de la GUI ---
    private void initComponents() {
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtTarget = new JTextField(30);
        cbGlobalPrivileges = new JCheckBox("Otorgar Privilegios Globales (*.*)");

        // Inicializar Checkboxes de Datos
        cbSelect = new JCheckBox("SELECT");
        cbInsert = new JCheckBox("INSERT");
        cbUpdate = new JCheckBox("UPDATE");
        cbDelete = new JCheckBox("DELETE");
        cbFile = new JCheckBox("FILE");

        // Inicializar Checkboxes de Estructura
        cbCreate = new JCheckBox("CREATE");
        cbAlter = new JCheckBox("ALTER");
        cbIndex = new JCheckBox("INDEX");
        cbDrop = new JCheckBox("DROP");
        cbCreateTempTables = new JCheckBox("CREATE TEMPORARY TABLES");
        cbShowView = new JCheckBox("SHOW VIEW");
        cbCreateRoutine = new JCheckBox("CREATE ROUTINE");
        cbAlterRoutine = new JCheckBox("ALTER ROUTINE");
        cbExecute = new JCheckBox("EXECUTE");
        cbCreateView = new JCheckBox("CREATE VIEW");
        cbEvent = new JCheckBox("EVENT");
        cbTrigger = new JCheckBox("TRIGGER");

        // Inicializar Checkboxes de Administración
        cbGrant = new JCheckBox("GRANT OPTION"); // GRANT en la interfaz de phpMyAdmin se refiere a GRANT OPTION
        cbProcess = new JCheckBox("PROCESS");
        cbReload = new JCheckBox("RELOAD");
        cbShutdown = new JCheckBox("SHUTDOWN");
        cbSuper = new JCheckBox("SUPER");
        cbShowDatabases = new JCheckBox("SHOW DATABASES");
        cbLockTables = new JCheckBox("LOCK TABLES");
        cbReferences = new JCheckBox("REFERENCES");
        cbReplicationClient = new JCheckBox("REPLICATION CLIENT");
        cbReplicationSlave = new JCheckBox("REPLICATION SLAVE");
        cbCreateUser = new JCheckBox("CREATE USER");

        taOutput = new JTextArea(10, 40);
        taOutput.setEditable(false);
        taOutput.setLineWrap(true);
        taOutput.setWrapStyleWord(true);

        // Rellenar la lista de checkboxes de privilegios
        privilegeCheckBoxes = new ArrayList<>();
        privilegeCheckBoxes.add(cbSelect);
        privilegeCheckBoxes.add(cbInsert);
        privilegeCheckBoxes.add(cbUpdate);
        privilegeCheckBoxes.add(cbDelete);
        privilegeCheckBoxes.add(cbFile);
        privilegeCheckBoxes.add(cbCreate);
        privilegeCheckBoxes.add(cbAlter);
        privilegeCheckBoxes.add(cbIndex);
        privilegeCheckBoxes.add(cbDrop);
        privilegeCheckBoxes.add(cbCreateTempTables);
        privilegeCheckBoxes.add(cbShowView);
        privilegeCheckBoxes.add(cbCreateRoutine);
        privilegeCheckBoxes.add(cbAlterRoutine);
        privilegeCheckBoxes.add(cbExecute);
        privilegeCheckBoxes.add(cbCreateView);
        privilegeCheckBoxes.add(cbEvent);
        privilegeCheckBoxes.add(cbTrigger);
        privilegeCheckBoxes.add(cbGrant);
        privilegeCheckBoxes.add(cbProcess);
        privilegeCheckBoxes.add(cbReload);
        privilegeCheckBoxes.add(cbShutdown);
        privilegeCheckBoxes.add(cbSuper);
        privilegeCheckBoxes.add(cbShowDatabases);
        privilegeCheckBoxes.add(cbLockTables);
        privilegeCheckBoxes.add(cbReferences);
        privilegeCheckBoxes.add(cbReplicationClient);
        privilegeCheckBoxes.add(cbReplicationSlave);
        privilegeCheckBoxes.add(cbCreateUser);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de Información del Usuario
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Información de la Cuenta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; userPanel.add(new JLabel("Nombre de usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; userPanel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; userPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; userPanel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; userPanel.add(new JLabel("Objetivo (db.tabla o db.*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; userPanel.add(txtTarget, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; userPanel.add(cbGlobalPrivileges, gbc);

        // Panel de Privilegios
        JPanel privilegesPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // 3 columnas para Datos, Estructura, Admin
        privilegesPanel.setBorder(BorderFactory.createTitledBorder("Privilegios Globales"));

        // Subpanel de Datos
        JPanel dataPrivsPanel = new JPanel(new GridLayout(0, 1));
        dataPrivsPanel.setBorder(BorderFactory.createTitledBorder("Datos"));
        dataPrivsPanel.add(cbSelect);
        dataPrivsPanel.add(cbInsert);
        dataPrivsPanel.add(cbUpdate);
        dataPrivsPanel.add(cbDelete);
        dataPrivsPanel.add(cbFile);
        privilegesPanel.add(dataPrivsPanel);

        // Subpanel de Estructura
        JPanel structPrivsPanel = new JPanel(new GridLayout(0, 1));
        structPrivsPanel.setBorder(BorderFactory.createTitledBorder("Estructura"));
        structPrivsPanel.add(cbCreate);
        structPrivsPanel.add(cbAlter);
        structPrivsPanel.add(cbIndex);
        structPrivsPanel.add(cbDrop);
        structPrivsPanel.add(cbCreateTempTables);
        structPrivsPanel.add(cbShowView);
        structPrivsPanel.add(cbCreateRoutine);
        structPrivsPanel.add(cbAlterRoutine);
        structPrivsPanel.add(cbExecute);
        structPrivsPanel.add(cbCreateView);
        structPrivsPanel.add(cbEvent);
        structPrivsPanel.add(cbTrigger);
        privilegesPanel.add(structPrivsPanel);

        // Subpanel de Administración
        JPanel adminPrivsPanel = new JPanel(new GridLayout(0, 1));
        adminPrivsPanel.setBorder(BorderFactory.createTitledBorder("Administración"));
        adminPrivsPanel.add(cbGrant);
        adminPrivsPanel.add(cbProcess);
        adminPrivsPanel.add(cbReload);
        adminPrivsPanel.add(cbShutdown);
        adminPrivsPanel.add(cbSuper);
        adminPrivsPanel.add(cbShowDatabases);
        adminPrivsPanel.add(cbLockTables);
        adminPrivsPanel.add(cbReferences);
        adminPrivsPanel.add(cbReplicationClient);
        adminPrivsPanel.add(cbReplicationSlave);
        adminPrivsPanel.add(cbCreateUser);
        privilegesPanel.add(adminPrivsPanel);

        // Panel de Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnCreateUser = new JButton("Crear Usuario");
        JButton btnGrantPrivs = new JButton("Otorgar Permisos");
        JButton btnRevokePrivs = new JButton("Revocar Permisos");
        JButton btnDeleteUser = new JButton("Eliminar Usuario");
        JButton btnClear = new JButton("Limpiar Parámetros"); // Nuevo botón limpiar

        buttonPanel.add(btnCreateUser);
        buttonPanel.add(btnGrantPrivs);
        buttonPanel.add(btnRevokePrivs);
        buttonPanel.add(btnDeleteUser);
        buttonPanel.add(btnClear); // Añadir el botón limpiar al panel
        

        // Añadir al panel principal
        mainPanel.add(userPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(privilegesPanel), BorderLayout.CENTER); // Usar JScrollPane para privilegios
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Salida"));
        outputPanel.add(new JScrollPane(taOutput), BorderLayout.CENTER); // Scroll para la salida

        add(mainPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
    }

    // --- Métodos de Listeners de la GUI ---
    private void addListeners() {
        // Listener para el checkbox de privilegios globales
        cbGlobalPrivileges.addActionListener(e -> {
            boolean selected = cbGlobalPrivileges.isSelected();
            txtTarget.setEnabled(!selected); // Deshabilita el campo Target si son globales
            if (selected) {
                txtTarget.setText("*.*");
            } else {
                txtTarget.setText("");
            }
        });

        // Botón Crear Usuario
        ((JButton) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(2)).getComponent(0)).addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                taOutput.append("Error: Nombre de usuario y contraseña no pueden estar vacíos para crear un usuario.\n");
                return;
            }
            String result = crearUsuario(username, password); // Llama al método de la BD
            taOutput.append(result + "\n");
        });

        // Botón Otorgar Permisos
        ((JButton) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(2)).getComponent(1)).addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String target = txtTarget.getText().trim();
            if (username.isEmpty()) {
                taOutput.append("Error: El nombre de usuario no puede estar vacío para otorgar permisos.\n");
                return;
            }
            if (!cbGlobalPrivileges.isSelected() && target.isEmpty()) {
                taOutput.append("Error: El objetivo (base de datos o tabla) no puede estar vacío a menos que se seleccionen privilegios globales.\n");
                return;
            }
            List<String> selectedPrivileges = getSelectedPrivileges();
            String result = otorgarPermisos(selectedPrivileges, target, username); // Llama al método de la BD
            taOutput.append(result + "\n");
        });

        // Botón Revocar Permisos
        ((JButton) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(2)).getComponent(2)).addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String target = txtTarget.getText().trim();
            if (username.isEmpty()) {
                taOutput.append("Error: El nombre de usuario no puede estar vacío para revocar permisos.\n");
                return;
            }
            if (!cbGlobalPrivileges.isSelected() && target.isEmpty()) {
                taOutput.append("Error: El objetivo (base de datos o tabla) no puede estar vacío a menos que se seleccionen privilegios globales.\n");
                return;
            }
            List<String> selectedPrivileges = getSelectedPrivileges();
            String result = revocarPermisos(selectedPrivileges, target, username); // Llama al método de la BD
            taOutput.append(result + "\n");
        });

        // Botón Eliminar Usuario
        ((JButton) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(2)).getComponent(3)).addActionListener(e -> {
            String username = txtUsername.getText().trim();
            if (username.isEmpty()) {
                taOutput.append("Error: El nombre de usuario no puede estar vacío para eliminar un usuario.\n");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres eliminar al usuario '" + username + "'?", "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String result = eliminarUsuario(username); // Llama al método de la BD
                taOutput.append(result + "\n");
            }
        });

        // Listener para el botón "Limpiar Parámetros" (nuevo)
        ((JButton) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(2)).getComponent(4)).addActionListener(e -> {
            clearParameters(); // Llama al nuevo método de limpieza
        });
    }

    /**
     * Recopila los privilegios seleccionados en los checkboxes.
     * @return Una lista de Strings con los nombres de los privilegios seleccionados.
     */
    private List<String> getSelectedPrivileges() {
        List<String> privileges = new ArrayList<>();
        // Datos
        if (cbSelect.isSelected()) privileges.add("SELECT");
        if (cbInsert.isSelected()) privileges.add("INSERT");
        if (cbUpdate.isSelected()) privileges.add("UPDATE");
        if (cbDelete.isSelected()) privileges.add("DELETE");
        if (cbFile.isSelected()) privileges.add("FILE");

        // Estructura
        if (cbCreate.isSelected()) privileges.add("CREATE");
        if (cbAlter.isSelected()) privileges.add("ALTER");
        if (cbIndex.isSelected()) privileges.add("INDEX");
        if (cbDrop.isSelected()) privileges.add("DROP");
        if (cbCreateTempTables.isSelected()) privileges.add("CREATE TEMPORARY TABLES");
        if (cbShowView.isSelected()) privileges.add("SHOW VIEW");
        if (cbCreateRoutine.isSelected()) privileges.add("CREATE ROUTINE");
        if (cbAlterRoutine.isSelected()) privileges.add("ALTER ROUTINE");
        if (cbExecute.isSelected()) privileges.add("EXECUTE");
        if (cbCreateView.isSelected()) privileges.add("CREATE VIEW");
        if (cbEvent.isSelected()) privileges.add("EVENT");
        if (cbTrigger.isSelected()) privileges.add("TRIGGER");

        // Administración
        if (cbGrant.isSelected()) privileges.add("GRANT OPTION");
        if (cbProcess.isSelected()) privileges.add("PROCESS");
        if (cbReload.isSelected()) privileges.add("RELOAD");
        if (cbShutdown.isSelected()) privileges.add("SHUTDOWN");
        if (cbSuper.isSelected()) privileges.add("SUPER");
        if (cbShowDatabases.isSelected()) privileges.add("SHOW DATABASES");
        if (cbLockTables.isSelected()) privileges.add("LOCK TABLES");
        if (cbReferences.isSelected()) privileges.add("REFERENCES");
        if (cbReplicationClient.isSelected()) privileges.add("REPLICATION CLIENT");
        if (cbReplicationSlave.isSelected()) privileges.add("REPLICATION SLAVE");
        if (cbCreateUser.isSelected()) privileges.add("CREATE USER");

        return privileges;
    }

    /**
     * Limpia todos los campos de entrada de la GUI y desmarca los checkboxes.
     */
    private void clearParameters() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtTarget.setText("");
        cbGlobalPrivileges.setSelected(false);
        txtTarget.setEnabled(true); // Asegúrate de que el campo Target esté habilitado

        // Desmarcar todos los checkboxes de privilegios
        for (JCheckBox cb : privilegeCheckBoxes) {
            cb.setSelected(false);
        }
        
        taOutput.setText(""); // Opcional: Limpiar también el área de salida
    }

    // --- Métodos de Lógica de Base de Datos (integrados aquí) ---

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver no encontrado. Asegúrate de tener el JAR en tu CLASSPATH.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Crea un nuevo usuario en MySQL.
     * @param username Nombre del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @return Mensaje de éxito o error.
     */
    public String crearUsuario(String username, String password) {
        String message;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE USER '" + username + "'@'localhost' IDENTIFIED BY '" + password + "';";
            stmt.executeUpdate(sql);
            message = "Usuario '" + username + "' creado exitosamente.";
        } catch (SQLException e) {
            message = "Error al crear usuario '" + username + "': " + e.getMessage();
        }
        return message;
    }

    /**
     * Otorga permisos específicos a un usuario sobre un objetivo (tabla/BD).
     * @param privileges Lista de permisos a otorgar (ej. "SELECT", "INSERT").
     * @param target Objeto sobre el cual se otorgan los permisos (ej. "nombre_bd.nombre_tabla" o "nombre_bd.*" o "*.*").
     * @param username Nombre del usuario al que se le otorgan los permisos.
     * @return Mensaje de éxito o error.
     */
    public String otorgarPermisos(List<String> privileges, String target, String username) {
        String message;
        if (privileges.isEmpty()) {
            return "No se seleccionaron privilegios para otorgar.";
        }
        String privilegesStr = String.join(", ", privileges); // Une la lista de privilegios con comas

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Validar que el target no esté vacío si no son privilegios globales
            if (!target.equals("*.*") && (target == null || target.trim().isEmpty())) {
                return "El objetivo (base de datos o tabla) no puede estar vacío si no son privilegios globales.";
            }

            String sql = "GRANT " + privilegesStr + " ON " + target + " TO '" + username + "'@'localhost';";
            stmt.executeUpdate(sql);
            stmt.executeUpdate("FLUSH PRIVILEGES;"); // Para que los cambios surtan efecto
            message = "Permisos '" + privilegesStr + "' otorgados a '" + username + "' sobre '" + target + "'.";
        } catch (SQLException e) {
            // Aquí capturamos el error de tabla no existente
            if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist")) {
                message = "Error: La tabla o base de datos '" + target + "' no existe. Por favor, verifica el nombre.";
            } else {
                message = "Error al otorgar permisos: " + e.getMessage();
            }
        }
        return message;
    }

    /**
     * Revoca permisos específicos a un usuario sobre un objetivo (tabla/BD).
     * @param privileges Lista de permisos a revocar.
     * @param target Objeto sobre el cual se revocan los permisos.
     * @param username Nombre del usuario al que se le revocan los permisos.
     * @return Mensaje de éxito o error.
     */
    public String revocarPermisos(List<String> privileges, String target, String username) {
        String message;
        if (privileges.isEmpty()) {
            return "No se seleccionaron privilegios para revocar.";
        }
        String privilegesStr = String.join(", ", privileges);

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            if (!target.equals("*.*") && (target == null || target.trim().isEmpty())) {
                return "El objetivo (base de datos o tabla) no puede estar vacío si no son privilegios globales.";
            }

            String sql = "REVOKE " + privilegesStr + " ON " + target + " FROM '" + username + "'@'localhost';";
            stmt.executeUpdate(sql);
            stmt.executeUpdate("FLUSH PRIVILEGES;");
            message = "Permisos '" + privilegesStr + "' revocados a '" + username + "' sobre '" + target + "'.";
        } catch (SQLException e) {
            message = "Error al revocar permisos: " + e.getMessage();
        }
        return message;
    }

    /**
     * Elimina un usuario de MySQL.
     * @param username Nombre del usuario a eliminar.
     * @return Mensaje de éxito o error.
     */
    public String eliminarUsuario(String username) {
        String message;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "DROP USER '" + username + "'@'localhost';";
            stmt.executeUpdate(sql);
            message = "Usuario '" + username + "' eliminado exitosamente.";
        } catch (SQLException e) {
            message = "Error al eliminar usuario '" + username + "': " + e.getMessage();
        }
        return message;
    }
}