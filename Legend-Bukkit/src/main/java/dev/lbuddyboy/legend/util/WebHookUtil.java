package dev.lbuddyboy.legend.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebHookUtil {

    /**
     * Sends a message to a Discord webhook with an embed description.
     *
     * @param webhookUrl  the Discord webhook URL
     * @param username    the username to display
     * @param title       the embed title
     * @param description the embed description
     * @param color       the embed color (RGB integer, e.g., 0xFFAA00)
     * @param authorName  the author name to display in the embed
     * @return true if the message was sent successfully, false otherwise
     */
    public static boolean sendWebhookMessage(String webhookUrl, String username, String title, String description, int color, String authorName) {
        try {
            // Escape special characters like \n and "
            String escapedDescription = description
                    .replace("\\", "\\\\") // Escape backslashes
                    .replace("\"", "\\\"") // Escape quotes
                    .replace("\n", "\\n"); // Escape newlines

            String payload = String.format("""
                    {
                        "username": "%s",
                        "embeds": [
                            {
                                "description": "%s",
                                "color": %d,
                                "author": {
                                    "name": "%s"
                                }
                            }
                        ]
                    }
                    """, username, escapedDescription, color, authorName);

            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            return responseCode == 204; // 204 No Content means success

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
