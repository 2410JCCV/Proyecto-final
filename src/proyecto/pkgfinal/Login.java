package proyecto.pkgfinal;

import com.formdev.flatlaf.FlatIntelliJLaf; // Importa la apariencia FlatLaf IntelliJ
import com.google.zxing.BarcodeFormat; // Para especificar el formato del código de barras (QR_CODE)
import com.google.zxing.WriterException; // Excepción de la librería ZXing para errores de escritura
import com.google.zxing.client.j2se.MatrixToImageWriter; // Convierte BitMatrix a BufferedImage
import com.google.zxing.common.BitMatrix; // Representación de la matriz de bits de un código QR
import com.google.zxing.qrcode.QRCodeWriter; // Generador de códigos QR
import com.warrenstrange.googleauth.GoogleAuthenticator; // Librería para autenticación de Google
import com.warrenstrange.googleauth.GoogleAuthenticatorKey; // Genera claves secretas para Google Authenticator

import javax.imageio.ImageIO; // Para manejar imágenes
import javax.swing.*; // Clases de Swing para la interfaz gráfica
import java.awt.*; // Clases de AWT para gráficos y UI
import java.awt.event.*; // Clases para manejo de eventos
import java.awt.image.BufferedImage; // Para manipular imágenes
import java.io.IOException; // Para manejo de errores de I/O
import java.net.URL; // Para cargar recursos desde una URL
import java.sql.*; // Clases para conexión y operaciones de base de datos SQL

/**
 * La clase `Login` representa la ventana de inicio de sesión de la aplicación.
 * Permite a los usuarios ingresar sus credenciales, registrar nuevas cuentas
 * e implementa la autenticación de dos factores (2FA) con Google Authenticator
 * para mayor seguridad.
 *
 * Funcionalidades principales:
 * - **Interfaz de Usuario Atractiva**: Utiliza FlatLaf para un diseño moderno y un panel de fondo con imagen.
 * - **Campos de Credenciales**: `usuarioField` y `contraseñaField` para que el usuario ingrese su nombre y contraseña.
 * - **Botones de Acción**:
 * - `loginButton`: Para verificar las credenciales y autenticar al usuario.
 * - `registrarButton`: Para abrir la ventana de registro de nuevos usuarios.
 * - **Opciones Adicionales**:
 * - `rememberCheckBox`: Un checkbox para "Recordarme" (funcionalidad no implementada en este fragmento).
 * - `forgotPasswordLabel`: Un enlace para "Olvidé mi contraseña".
 * - **Autenticación con Base de Datos**: Conecta a una base de datos MySQL (`escuela2`) para validar usuarios y contraseñas.
 * - **Autenticación de Dos Factores (2FA)**:
 * - Si un usuario tiene una clave secreta (`secret`) registrada en la base de datos, se le solicita un código de 6 dígitos de Google Authenticator.
 * - Utiliza la librería `com.warrenstrange.googleauth` para verificar el código 2FA.
 * - **Registro de Usuarios**:
 * - Permite crear nuevos usuarios (`nuevoUsuario`, `nuevaContraseña`, `confirmarContraseña`).
 * - Genera automáticamente una clave secreta de Google Authenticator para el nuevo usuario.
 * - Muestra un código QR (`mostrarCodigoQR`) que el usuario puede escanear con la aplicación Google Authenticator para configurar el 2FA.
 * - **Manejo de Errores**: Proporciona mensajes de error claros para credenciales incorrectas, fallos de conexión a la BD, contraseñas no coincidentes, etc.
 */
public class Login extends JFrame {
    // Campos de texto para el usuario y la contraseña
    private JTextField usuarioField;
    private JPasswordField contraseñaField;
    // Botones de acción
    private JButton loginButton, registrarButton;
    // Checkbox para recordar sesión
    private JCheckBox rememberCheckBox;
    // Etiqueta para la opción de "olvidar contraseña"
    private JLabel forgotPasswordLabel;

    /**
     * Constructor de la clase Login.
     * Configura la interfaz de usuario, los estilos y los listeners de eventos.
     */
    public Login() {
        // Bloque try-catch para configurar la apariencia de la UI (FlatLaf)
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf()); // Aplica el Look and Feel FlatLaf IntelliJ
            // Configurar colores y estilos personalizados para componentes de Swing
            UIManager.put("Panel.background", new Color(245, 245, 245, 200)); // Color de fondo del panel
            UIManager.put("Button.background", new Color(100, 150, 220)); // Color de fondo de los botones
            UIManager.put("Button.foreground", Color.WHITE); // Color del texto de los botones
            UIManager.put("Button.arc", 10); // Redondez de los botones
            UIManager.put("Component.focusWidth", 0); // Ancho del foco de los componentes
            UIManager.put("TextComponent.arc", 5); // Redondez de los campos de texto
        } catch (Exception ex) {
            ex.printStackTrace(); // Imprime la pila de errores si falla la configuración del L&F
        }

        // Configuración básica de la ventana principal (JFrame)
        setTitle("Inicio de Sesión"); // Título de la ventana
        setSize(500, 400); // Tamaño de la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setResizable(false); // La ventana no puede ser redimensionada

        // Panel con imagen de fondo
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Carga la imagen de fondo desde una URL
                    Image bgImage = new ImageIcon(new URL("https://images.unsplash.com/photo-1497366754035-f200968a6e72?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80")).getImage();
                    // Dibuja la imagen escalada para que ocupe todo el panel
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    // Si falla la carga de la imagen, usa un color de fondo gris claro
                    g.setColor(new Color(240, 240, 240));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Panel principal para los elementos del formulario, con transparencia
        JPanel mainPanel = new JPanel(new GridBagLayout()); // Usa GridBagLayout para un control preciso
        mainPanel.setBackground(new Color(245, 245, 245, 200)); // Fondo semitransparente
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Borde vacío para espaciado interno

        GridBagConstraints gbc = new GridBagConstraints(); // Objeto para configurar restricciones de GridBagLayout
        gbc.insets = new Insets(10, 10, 10, 10); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Rellena el espacio horizontalmente

        // Título de la ventana de login
        JLabel titleLabel = new JLabel("Inicio de Sesión");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Fuente y tamaño del título
        titleLabel.setForeground(new Color(60, 60, 60)); // Color del título
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.gridwidth = 2; // Ocupa 2 columnas
        gbc.anchor = GridBagConstraints.CENTER; // Centra el componente
        mainPanel.add(titleLabel, gbc); // Añade el título al panel
        gbc.gridwidth = 1; // Restaura el ancho de columna a 1

        // Carga de iconos desde URLs para los campos de texto y botones
        ImageIcon userIcon = cargarIcono("https://cdn-icons-png.flaticon.com/512/1077/1077114.png", 24, 24);
        ImageIcon lockIcon = cargarIcono("https://cdn-icons-png.flaticon.com/512/3064/3064197.png", 24, 24);
        ImageIcon loginIcon = cargarIcono("https://cdn-icons-png.flaticon.com/512/1828/1828490.png", 20, 20);
        ImageIcon registerIcon = cargarIcono("https://cdn-icons-png.flaticon.com/512/992/992651.png", 20, 20);

        // Inicialización y estilización de los campos de texto
        usuarioField = new JTextField(15);
        estiloCampoTexto(usuarioField); // Aplica estilo personalizado al campo de usuario

        contraseñaField = new JPasswordField(15);
        estiloCampoTexto(contraseñaField); // Aplica estilo personalizado al campo de contraseña

        // Inicialización y estilización de los botones
        loginButton = new JButton("Ingresar", loginIcon); // Botón de login con texto e icono
        estiloBotonPrincipal(loginButton); // Aplica estilo principal al botón de login

        registrarButton = new JButton("Registrar", registerIcon); // Botón de registro con texto e icono
        estiloBotonSecundario(registrarButton); // Aplica estilo secundario al botón de registro

        // Checkbox "Recordarme"
        rememberCheckBox = new JCheckBox("Recordarme");
        rememberCheckBox.setOpaque(false); // Hace el checkbox transparente para ver el fondo
        rememberCheckBox.setForeground(new Color(80, 80, 80)); // Color del texto del checkbox

        // Enlace "Olvidé mi contraseña"
        forgotPasswordLabel = new JLabel("¿Olvidaste tu contraseña?");
        forgotPasswordLabel.setForeground(new Color(70, 130, 200)); // Color azul para el enlace
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Muestra un mensaje al hacer clic en el enlace
                JOptionPane.showMessageDialog(Login.this, "Por favor contacte al administrador del sistema.");
            }
        });

        // Panel para agrupar las opciones "Recordarme" y "Olvidé mi contraseña"
        JPanel optionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        optionsPanel.setOpaque(false); // Hace el panel transparente
        optionsPanel.add(rememberCheckBox);
        optionsPanel.add(forgotPasswordLabel);

        // --- Agregar componentes al panel principal usando GridBagConstraints ---
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 1; // Fila 1
        // Etiqueta "Usuario:" con icono, alineado a la izquierda
        mainPanel.add(new JLabel("Usuario:", userIcon, SwingConstants.LEFT), gbc);
        gbc.gridx = 1; // Columna 1
        mainPanel.add(usuarioField, gbc); // Campo de usuario

        gbc.gridx = 0; // Columna 0
        gbc.gridy = 2; // Fila 2
        // Etiqueta "Contraseña:" con icono, alineado a la izquierda
        mainPanel.add(new JLabel("Contraseña:", lockIcon, SwingConstants.LEFT), gbc);
        gbc.gridx = 1; // Columna 1
        mainPanel.add(contraseñaField, gbc); // Campo de contraseña

        gbc.gridx = 0; // Columna 0
        gbc.gridy = 3; // Fila 3
        gbc.gridwidth = 2; // Ocupa 2 columnas
        mainPanel.add(optionsPanel, gbc); // Panel de opciones
        gbc.gridwidth = 1; // Restaura el ancho de columna a 1

        gbc.gridx = 0; // Columna 0
        gbc.gridy = 4; // Fila 4
        gbc.gridwidth = 1; // Ocupa 1 columna
        mainPanel.add(loginButton, gbc); // Botón de login
        gbc.gridx = 1; // Columna 1
        mainPanel.add(registrarButton, gbc); // Botón de registro

        // Separador visual
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 5; // Fila 5
        gbc.gridwidth = 2; // Ocupa 2 columnas
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(200, 200, 200)); // Color del separador
        mainPanel.add(separator, gbc); // Añade el separador

        // Mensaje de registro (texto informativo)
        gbc.gridy = 6; // Fila 6
        JLabel registerLabel = new JLabel("¿No tienes una cuenta? Regístrate");
        registerLabel.setForeground(new Color(100, 100, 100)); // Color del texto
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centra el texto
        mainPanel.add(registerLabel, gbc); // Añade el mensaje

        // Agrega el panel principal (con el formulario) al panel de fondo
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        // Agrega el panel de fondo al JFrame
        add(backgroundPanel);

        // Acciones de los botones (listeners)
        loginButton.addActionListener(e -> verificarCredenciales()); // Llama a verificarCredenciales al hacer clic en Login
        registrarButton.addActionListener(e -> abrirVentanaRegistro()); // Llama a abrirVentanaRegistro al hacer clic en Registrar

        // Permitir login presionando Enter en el campo de contraseña
        contraseñaField.addActionListener(e -> verificarCredenciales());
    }

    /**
     * Aplica un estilo uniforme a los campos de texto.
     * @param field El JTextField o JPasswordField a estilizar.
     */
    private void estiloCampoTexto(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder( // Borde compuesto para padding interno
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Borde de línea
            BorderFactory.createEmptyBorder(8, 10, 8, 10) // Padding interno
        ));
        field.setBackground(Color.WHITE); // Fondo blanco
    }

    /**
     * Aplica un estilo visual para el botón principal (Ingresar).
     * @param button El JButton a estilizar.
     */
    private void estiloBotonPrincipal(JButton button) {
        button.setBackground(new Color(100, 150, 220)); // Color de fondo azul
        button.setForeground(Color.WHITE); // Color del texto blanco
        button.setFocusPainted(false); // No pinta el foco alrededor del texto
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interno
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima
    }

    /**
     * Aplica un estilo visual para el botón secundario (Registrar).
     * @param button El JButton a estilizar.
     */
    private void estiloBotonSecundario(JButton button) {
        button.setBackground(new Color(230, 230, 230)); // Color de fondo gris claro
        button.setForeground(new Color(80, 80, 80)); // Color del texto gris oscuro
        button.setFocusPainted(false); // No pinta el foco alrededor del texto
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interno
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima
    }

    /**
     * Carga un icono desde una URL y lo redimensiona.
     * Si la carga falla, devuelve un ImageIcon vacío.
     * @param url La URL de la imagen.
     * @param w El ancho deseado.
     * @param h La altura deseada.
     * @return Un ImageIcon con la imagen cargada y redimensionada.
     */
    private ImageIcon cargarIcono(String url, int w, int h) {
        try {
            // Carga la imagen desde la URL y la escala a las dimensiones deseadas
            Image image = new ImageIcon(new URL(url)).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        } catch (Exception e) {
            // Si hay un error, devuelve un ImageIcon de una imagen en blanco
            return new ImageIcon(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        }
    }

    /**
     * Verifica las credenciales ingresadas por el usuario contra la base de datos.
     * Si son correctas y el usuario tiene 2FA configurado, solicita el código.
     * Si la autenticación es exitosa, abre la ventana `TablaVisualizadora`.
     */
    private void verificarCredenciales() {
        String usuario = usuarioField.getText().trim(); // Obtiene el texto del campo de usuario y elimina espacios
        String contraseña = new String(contraseñaField.getPassword()).trim(); // Obtiene la contraseña y elimina espacios

        // Valida que los campos no estén vacíos
        if (usuario.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simulación de conexión a BD (reemplaza con tu conexión real)
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/escuela2", "root", "aAWV1c10YuHTQkmp"); // Establece la conexión a la BD
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE username=? AND password=?")) { // Prepara la consulta SQL

            stmt.setString(1, usuario); // Establece el parámetro del usuario
            stmt.setString(2, contraseña); // Establece el parámetro de la contraseña
            ResultSet rs = stmt.executeQuery(); // Ejecuta la consulta

            if (rs.next()) { // Si se encuentra un registro (credenciales correctas)
                String secret = rs.getString("secret"); // Obtiene la clave secreta 2FA del usuario

                if (secret != null && !secret.isEmpty()) { // Si el usuario tiene una clave secreta (2FA configurado)
                    // Solicita el código de Google Authenticator
                    String input = JOptionPane.showInputDialog(this, "Ingresa el código de 6 dígitos de Google Authenticator:");
                    if (input == null || input.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Debe ingresar un código válido.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        int codigo = Integer.parseInt(input); // Intenta convertir el input a entero
                        GoogleAuthenticator gAuth = new GoogleAuthenticator(); // Crea una instancia del autenticador
                        if (gAuth.authorize(secret, codigo)) { // Verifica el código 2FA
                            JOptionPane.showMessageDialog(this, "✅ Autenticación exitosa", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            dispose(); // Cierra la ventana de login
                            new TablaVisualizadora().setVisible(true); // Abre la ventana principal de la aplicación
                        } else {
                            JOptionPane.showMessageDialog(this, "❌ Código incorrecto", "Error", JOptionPane.ERROR_MESSAGE); // Código 2FA incorrecto
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "El código debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE); // El código no es numérico
                    }
                } else { // Si el usuario no tiene 2FA configurado
                    JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Cierra la ventana de login
                    new TablaVisualizadora().setVisible(true); // Abre la ventana principal de la aplicación
                }
            } else { // Credenciales incorrectas
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime la pila de errores de SQL
            // Muestra un mensaje de error de conexión a la base de datos
            JOptionPane.showMessageDialog(this,
                "Error al conectar con la base de datos.\nDetalle: " + e.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre un cuadro de diálogo para registrar un nuevo usuario.
     * Solicita nombre de usuario, contraseña y confirmación de contraseña.
     * Si el registro es exitoso, genera una clave secreta 2FA y muestra un código QR.
     */
    private void abrirVentanaRegistro() {
        // Campos de texto para el registro de nuevo usuario
        JTextField nuevoUsuario = new JTextField();
        JPasswordField nuevaContraseña = new JPasswordField();
        JPasswordField confirmarContraseña = new JPasswordField();

        // Panel para organizar los campos de registro
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5)); // GridLayout de una columna
        panel.add(new JLabel("Nuevo usuario:"));
        panel.add(nuevoUsuario);
        panel.add(new JLabel("Nueva contraseña:"));
        panel.add(nuevaContraseña);
        panel.add(new JLabel("Confirmar contraseña:"));
        panel.add(confirmarContraseña);

        // Muestra el cuadro de diálogo de confirmación para el registro
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Registrar nuevo usuario",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) { // Si el usuario hace clic en "OK"
            String user = nuevoUsuario.getText().trim(); // Obtiene el nuevo usuario
            String pass = new String(nuevaContraseña.getPassword()).trim(); // Obtiene la nueva contraseña
            String confirmPass = new String(confirmarContraseña.getPassword()).trim(); // Obtiene la confirmación de contraseña

            // Validaciones de los campos de registro
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Intenta registrar el usuario en la base de datos
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/escuela2", "root", "aAWV1c10YuHTQkmp");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios (username, password, secret) VALUES (?, ?, ?)")) {

                GoogleAuthenticator gAuth = new GoogleAuthenticator(); // Crea una instancia para Google Authenticator
                GoogleAuthenticatorKey key = gAuth.createCredentials(); // Genera nuevas credenciales (clave secreta)
                String secret = key.getKey(); // Obtiene la clave secreta generada

                stmt.setString(1, user); // Establece el nombre de usuario
                stmt.setString(2, pass); // Establece la contraseña
                stmt.setString(3, secret); // Establece la clave secreta 2FA
                stmt.executeUpdate(); // Ejecuta la inserción en la base de datos

                // Genera la URL para el código QR de Google Authenticator
                String otpUrl = "otpauth://totp/MiApp:" + user + "?secret=" + secret + "&issuer=MiApp";
                mostrarCodigoQR(otpUrl); // Muestra el código QR al usuario

                // Muestra un mensaje de éxito con la clave secreta generada
                JOptionPane.showMessageDialog(this,
                    "<html><b>Usuario registrado con éxito</b><br>" +
                    "Guarda esta clave secreta en un lugar seguro:<br>" +
                    "<code>" + secret + "</code></html>",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | WriterException | IOException e) {
                e.printStackTrace(); // Imprime la pila de errores
                // Muestra un mensaje de error si el registro falla
                JOptionPane.showMessageDialog(this,
                    "Error al registrar el usuario.\nDetalle: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Genera y muestra un código QR basado en la URL `otpUrl` proporcionada.
     * Este QR es para configurar Google Authenticator.
     * @param otpUrl La URL de formato `otpauth://` para generar el QR.
     * @throws WriterException Si ocurre un error al escribir el código QR.
     * @throws IOException Si ocurre un error de I/O al manejar la imagen.
     */
    private void mostrarCodigoQR(String otpUrl) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter(); // Creador de códigos QR
        BitMatrix matrix = writer.encode(otpUrl, BarcodeFormat.QR_CODE, 250, 250); // Codifica la URL en una matriz de bits QR
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix); // Convierte la matriz de bits a una imagen
        ImageIcon icon = new ImageIcon(image); // Crea un ImageIcon a partir de la imagen
        // Muestra el código QR en un cuadro de diálogo
        JOptionPane.showMessageDialog(this, new JLabel(icon),
            "Escanea este código QR con Google Authenticator", JOptionPane.PLAIN_MESSAGE);
    }
}