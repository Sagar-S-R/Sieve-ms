package com.sieve.reminder.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TelegramClient {

    private static final Logger log = LoggerFactory.getLogger(TelegramClient.class);

    private final RestTemplate restTemplate;
    private final String botToken;
    private final boolean dryRun;

    public TelegramClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${TELEGRAM_BOT_TOKEN:}") String botToken,
            @Value("${reminder.dry-run:true}") boolean dryRun) {
        this.restTemplate = restTemplateBuilder.build();
        this.botToken = botToken;
        this.dryRun = dryRun;
    }

    public boolean sendDm(Long userId, String message) {
        if (dryRun) {
            log.info("Dry-run Telegram DM userId={} message={}", userId, message);
            return true;
        }
        if (botToken.isBlank()) {
            log.error("Cannot send Telegram DM: TELEGRAM_BOT_TOKEN is empty");
            return false;
        }

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url,
                    Map.of("chat_id", userId, "text", message, "parse_mode", "HTML"),
                    Map.class);
            return response.getStatusCode().is2xxSuccessful()
                    && Boolean.TRUE.equals(response.getBody() == null
                    ? null : response.getBody().get("ok"));
        } catch (RuntimeException exception) {
            log.error("Telegram DM failed for userId={}", userId, exception);
            return false;
        }
    }
}
