package proyecto.pkgfinal;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import org.apache.poi.xwpf.usermodel.*;

// Importaciones de iText 5
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * `RichTextEditorApp` es un editor de texto enriquecido con interfaz JavaFX,
 * que permite crear, abrir, guardar y exportar documentos.
 *
 * Funcionalidades:
 * - **Múltiples Pestañas**: Permite trabajar con varios documentos (`HTMLEditor`) a la vez (`TabPane`).
 * - **Gestión de Archivos**: Abrir (`openFile`), guardar (`saveFile`) y guardar como.
 * - **Exportación**: Exporta el contenido de la pestaña actual a PDF (`exportToPdf`) y DOCX (`exportToWord`).
 * - **Barra de Estado**: `statusLabel` para mensajes de usuario.
 */

public class RichTextEditorApp extends Application {

    private TabPane tabPane;
    private Label statusLabel;
    private Stage primaryStage;
    private int untitledCount = 1;

    // Instancia estática para acceder a la aplicación desde fuera
    private static RichTextEditorApp instance;

    public RichTextEditorApp() {
        // Almacena la instancia cuando se crea. Esto es crucial para acceder a ella desde Swing.
        instance = this;
    }

    public static RichTextEditorApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Editor Multiformato");

        BorderPane root = new BorderPane();

        // Barra de estado
        statusLabel = new Label("Listo.");
        statusLabel.setPadding(new Insets(5, 10, 5, 10));
        statusLabel.setStyle("-fx-background-color: #f0f0f0;");
        root.setBottom(statusLabel);

        // Menú
        MenuBar menuBar = new MenuBar();

        // Menú Archivo
        Menu fileMenu = new Menu("Archivo");
        MenuItem newTabItem = new MenuItem("Nueva Pestaña");
        newTabItem.setOnAction(e -> createNewTab("Documento sin título " + (untitledCount++)));
        MenuItem openItem = new MenuItem("Abrir...");
        openItem.setOnAction(e -> openFile());
        MenuItem saveItem = new MenuItem("Guardar");
        saveItem.setOnAction(e -> saveCurrentTab());
        MenuItem saveAsItem = new MenuItem("Guardar como...");
        saveAsItem.setOnAction(e -> saveCurrentTabAs());
        MenuItem exportToPDFItem = new MenuItem("Exportar a PDF...");
        exportToPDFItem.setOnAction(e -> exportToPDF());
        MenuItem exportToWordItem = new MenuItem("Exportar a Word...");
        exportToWordItem.setOnAction(e -> exportToWord());
        MenuItem closeTabItem = new MenuItem("Cerrar Pestaña");
        closeTabItem.setOnAction(e -> closeCurrentTab());
        // Cambiado para que el editor de texto solo cierre su ventana, no la aplicación completa.
        MenuItem exitItem = new MenuItem("Salir");
        exitItem.setOnAction(e -> primaryStage.close());

        fileMenu.getItems().addAll(newTabItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exportToPDFItem, exportToWordItem, new SeparatorMenuItem(), closeTabItem, exitItem);

        menuBar.getMenus().add(fileMenu);
        root.setTop(menuBar);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        root.setCenter(tabPane);

        createNewTab("Documento sin título 1"); // Crea la primera pestaña

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        // NO LLAMES primaryStage.show() AQUÍ. La ventana se mostrará desde TablaVisualizadora
        // cuando el usuario seleccione la opción.
    }

    // Método para mostrar la ventana del editor (llamado desde Swing)
    public void showStage() {
        if (primaryStage != null) {
            primaryStage.show();
            primaryStage.toFront(); // Pone la ventana al frente
        }
    }

    private void createNewTab(String title) {
        HTMLEditor editor = new HTMLEditor();
        editor.setHtmlText("");
        Tab tab = new Tab(title, editor);
        tab.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Cierre");
            alert.setHeaderText("Cerrar Pestaña");
            alert.setContentText("¿Deseas guardar los cambios antes de cerrar esta pestaña?");

            ButtonType buttonTypeYes = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeYes) {
                    saveCurrentTab();
                    tabPane.getTabs().remove(tab);
                } else if (response == buttonTypeNo) {
                    tabPane.getTabs().remove(tab);
                } else {
                    event.consume();
                }
            });
        });
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        updateStatus("Nueva pestaña creada: " + title);
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Documento");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de Texto", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos HTML", "*.html", "*.htm"),
                new FileChooser.ExtensionFilter("Todos los Archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                String content = readFile(file);
                HTMLEditor editor = new HTMLEditor();
                editor.setHtmlText(content);
                Tab tab = new Tab(file.getName(), editor);
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                updateStatus("Archivo abierto: " + file.getName());
            } catch (IOException e) {
                showError("Error al abrir archivo", "No se pudo leer el archivo: " + e.getMessage());
            }
        }
    }

    private void saveCurrentTab() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            HTMLEditor editor = (HTMLEditor) currentTab.getContent();
            String htmlContent = editor.getHtmlText();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Documento");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos HTML", "*.html"),
                    new FileChooser.ExtensionFilter("Archivos de Texto", "*.txt")
            );
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(htmlContent);
                    updateStatus("Archivo guardado: " + file.getName());
                } catch (IOException e) {
                    showError("Error al guardar archivo", "No se pudo guardar el archivo: " + e.getMessage());
                }
            }
        } else {
            updateStatus("No hay pestaña seleccionada para guardar.");
        }
    }

    private void saveCurrentTabAs() {
        saveCurrentTab(); // En este ejemplo, "Guardar como" es igual que "Guardar"
    }

    private void exportToPDF() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            HTMLEditor editor = (HTMLEditor) currentTab.getContent();
            String htmlContent = editor.getHtmlText();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar a PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();

                    String textContent = extractTextFromHTMLWithLineBreaks(htmlContent);
                    document.add(new Paragraph(textContent));

                    document.close();
                    updateStatus("Exportado a PDF: " + file.getName());
                } catch (Exception e) {
                    showError("Error al exportar a PDF", "No se pudo exportar el archivo PDF: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            updateStatus("No hay pestaña seleccionada para exportar a PDF.");
        }
    }

    private void exportToWord() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            HTMLEditor editor = (HTMLEditor) currentTab.getContent();
            String htmlContent = editor.getHtmlText();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar a Word");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Word (.docx)", "*.docx"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try (FileOutputStream out = new FileOutputStream(file)) {
                    XWPFDocument document = new XWPFDocument();
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();

                    String textContent = htmlContent.replaceAll("<[^>]*>", "");
                    textContent = textContent.replaceAll("&nbsp;", " ");

                    run.setText(textContent);
                    document.write(out);
                    updateStatus("Exportado a Word: " + file.getName());
                    document.close();
                } catch (IOException e) {
                    showError("Error al exportar a Word", "No se pudo exportar el archivo Word: " + e.getMessage());
                }
            }
        } else {
            updateStatus("No hay pestaña seleccionada para exportar a Word.");
        }
    }

    private void closeCurrentTab() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            currentTab.getOnCloseRequest().handle(null);
        } else {
            updateStatus("No hay pestaña seleccionada para cerrar.");
        }
    }

    private String extractTextFromHTMLWithLineBreaks(String html) {
        String text = html.replaceAll("(?i)<p>", "\n\n");
        text = text.replaceAll("(?i)<br>", "\n");
        text = text.replaceAll("<[^>]*>", "");
        text = text.replaceAll("&nbsp;", " ");
        return text.trim();
    }

    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}