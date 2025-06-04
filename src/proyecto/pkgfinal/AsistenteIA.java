package proyecto.pkgfinal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.swing.*;
import java.io.IOException;

/**
 * Carrillo Viveros Juan Carlos 217o02237 y Luna Zamora Marlleli 217o00552
 * La clase AsistenteIA interactúa con la API de OpenAI para proporcionar asistencia sobre consultas SQL. Sus funciones principales son:
 * - Realizar consultas a la API de OpenAI: Envía el texto SQL del usuario para que la IA lo explique o genere ayuda.
 * - Mostrar respuestas al usuario: Presenta la respuesta de la IA en un cuadro de diálogo.
 * - Manejo de errores: Gestiona posibles errores de red o respuestas no exitosas de la API.
 */
public class AsistenteIA {

    // Coloca tu API key aquí (reemplaza con tu clave real y mantenla segura)
    private static final String API_KEY = "qfKfMaYnAWuTCHTBUefB27MNApiSNdNPO3NZ7qW_HO1mOmrK2YWyN-eX1a-fxxTx2DSbJSq_LNT3BlbkFJmcFT2FpNKH1d6P_im8Hh-rOrB_LVW1TC7shFbczIXXkm9yGgyFJN133EVOFN_2JTOjwcb9n48A";

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * Responde a una consulta SQL obtenida desde un editor, mostrando la explicación en un JOptionPane.
     * Si el mensaje SQL está vacío, muestra una advertencia.
     * @param mensajeSQL La consulta SQL a ser explicada o asistida.
     */
    public static void responderDesdeEditor(String mensajeSQL) {
        if (mensajeSQL == null || mensajeSQL.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El área SQL está vacía.", "Asistente IA", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String respuesta = enviarConsulta("Explica o ayuda con esta consulta SQL:\n" + mensajeSQL.trim());
        JOptionPane.showMessageDialog(null, respuesta, "Asistente IA", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Envía una consulta de usuario a la API de OpenAI y retorna la respuesta generada por el modelo.
     * Configura el rol del asistente como un experto en SQL para optimizar las respuestas.
     * @param mensajeUsuario El mensaje o consulta del usuario.
     * @return La respuesta de la IA o un mensaje de error si la comunicación falla.
     */
    public static String enviarConsulta(String mensajeUsuario) {
        OkHttpClient client = new OkHttpClient();

        // Configuración del mensaje del sistema (rol del asistente)
        JsonObject message1 = new JsonObject();
        message1.addProperty("role", "system");
        message1.addProperty("content", "Eres un asistente experto en SQL que ayuda a programadores a entender y construir consultas.");

        // Configuración del mensaje del usuario
        JsonObject message2 = new JsonObject();
        message2.addProperty("role", "user");
        message2.addProperty("content", mensajeUsuario);

        // Creación del array de mensajes para la API
        JsonArray messages = new JsonArray();
        messages.add(message1);
        messages.add(message2);

        // Construcción del cuerpo de la solicitud JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo"); // Modelo de IA a utilizar
        requestBody.add("messages", messages);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        // Construcción de la solicitud HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY) // Clave de API para autenticación
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // Ejecución de la solicitud y manejo de la respuesta
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // Manejo de respuestas no exitosas (códigos de error HTTP)
                return "Error: " + response.code() + " - " + response.message();
            }

            // Parseo de la respuesta JSON para extraer el contenido del asistente
            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = json.getAsJsonArray("choices");
            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject message = firstChoice.getAsJsonObject("message");
            return message.get("content").getAsString().trim();

        } catch (IOException e) {
            // Manejo de errores de red o comunicación
            return "Error de red: " + e.getMessage();
        }
    }
}