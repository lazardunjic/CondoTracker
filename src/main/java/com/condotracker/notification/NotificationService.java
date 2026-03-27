package com.condotracker.notification;

import com.condotracker.model.Listing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NotificationService {
    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.chat.id}")
    private String chatId;

    public void sendAlert(Listing listing) {
        try {
            String url = "https://api.telegram.org/bot" + token + "/sendMessage";

            String text = listing.getUrl();

            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> body = Map.of(
                    "chat_id", chatId,
                    "text", text,
                    "parse_mode", "Markdown"
            );

            restTemplate.postForObject(url, body, String.class);

        } catch (Exception e) {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }
}
