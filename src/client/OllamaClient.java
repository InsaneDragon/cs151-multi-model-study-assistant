package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class OllamaClient {

    // Create one HTTP client for sending requests to Ollama.
    private final HttpClient client =
            HttpClient.newHttpClient();

    // Send a prompt to a specified Ollama model.
    public CompletableFuture<String> generate(
            String modelName,
            String prompt) {

        // Create the JSON request body required by Ollama.
        String json =
                "{\"model\":\"" + escapeJson(modelName)
                + "\",\"prompt\":\"" + escapeJson(prompt)
                + "\",\"stream\":false}";

        // Build the HTTP request.
        HttpRequest request =
                HttpRequest.newBuilder()

                        // Specify the Ollama API address.
                        .uri(URI.create(
                                "http://localhost:11434/api/generate"))

                        // Tell Ollama that the request body contains JSON.
                        .header(
                                "Content-Type",
                                "application/json")

                        // Send the JSON data using HTTP POST.
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(json))

                        // Finish building the request.
                        .build();

        // Send the HTTP request asynchronously.
        return client.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString())

                // Process the response when it becomes available.
                .thenApply(response -> {

                    // Check whether Ollama returned a successful response.
                    if (response.statusCode() != 200) {

                        // Report an error if the request failed.
                        throw new RuntimeException(
                                "Ollama HTTP error "
                                + response.statusCode());
                    }

                    // Extract and return the generated text.
                    return getResponseText(
                            response.body());
                });
    }

    // Extract the generated text from Ollama's JSON response.
    private String getResponseText(String json) {

        // Locate the beginning of the "response" value.
        String marker = "\"response\":\"";

        int markerIndex = json.indexOf(marker);

        // No "response" field means Ollama sent something unexpected.
        if (markerIndex < 0) {

            throw new RuntimeException(
                    "Ollama reply has no response field");
        }

        // Find where the actual generated text starts.
        int start = markerIndex + marker.length();

        // Read until the closing quote, turning escapes back into characters.
        StringBuilder text = new StringBuilder();

        for (int i = start; i < json.length(); i++) {

            char c = json.charAt(i);

            // An unescaped quote ends the value.
            if (c == '"') {

                return text.toString();
            }

            if (c != '\\' || i + 1 >= json.length()) {

                text.append(c);
                continue;
            }

            // Handle an escape sequence such as \n or \"
            char next = json.charAt(++i);

            switch (next) {

                case 'n': text.append('\n'); break;
                case 't': text.append('\t'); break;
                case 'r': text.append('\r'); break;
                case 'b': text.append('\b'); break;
                case 'f': text.append('\f'); break;
                case 'u':
                    if (i + 4 < json.length()) {

                        String hex = json.substring(i + 1, i + 5);
                        text.append((char) Integer.parseInt(hex, 16));
                        i += 4;
                    }
                    break;
                default: text.append(next); // covers \" \\ \/
            }
        }

        // Closing quote never found.
        throw new RuntimeException(
                "Ollama reply was cut off");
    }

    // Make text safe to place inside a JSON string.
    private String escapeJson(String value) {

        StringBuilder escaped = new StringBuilder();

        for (char c : value.toCharArray()) {

            switch (c) {

                case '"':  escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (c < 0x20) {

                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {

                        escaped.append(c);
                    }
            }
        }
        return escaped.toString();
    }
}
