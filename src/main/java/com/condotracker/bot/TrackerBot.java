package com.condotracker.bot;

import com.condotracker.model.Filter;
import com.condotracker.repository.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class TrackerBot implements SpringLongPollingBot, LongPollingUpdateConsumer {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String username;

    @Autowired
    private FilterRepository filterRepository;

    private final TelegramClient telegramClient;

    public TrackerBot(@Value("${telegram.bot.token}") String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (text.split(" ")[0]) {
                case "/help" -> sendMessage(chatId,
                        "/setfilter - Set filter\n" +
                                "/showfilter - Show filter\n" +
                                "/clearfilter - Delete filter\n" +
                                "/status - Bot status\n" +
                                "/pause - Pause tracking\n" +
                                "/resume - Continue tracking"
                );
                case "/setfilter" -> handleSetFilter(chatId, text);
                case "/showfilter" -> handleShowFilter(chatId);
                case "/clearfilter" -> handleClearFilter(chatId);
                case "/status" -> sendMessage(chatId, "Bot is active.");
                case "/pause" -> sendMessage(chatId, "Tracking paused.");
                case "/resume" -> sendMessage(chatId, "Tracking continued.");
                default -> sendMessage(chatId, "Unknown command. /help.");
            }
        }
    }

    @Override
    public void consume(List<Update> updates) {
        updates.forEach(this::consume);
    }

    private void handleSetFilter(long chatId, String text) {
        // format: /setfilter minPrice maxPrice minArea maxArea location
        // primer: /setfilter 300 800 30 80 Beograd
        String[] parts = text.split(" ");
        if (parts.length < 6) {
            sendMessage(chatId, "Format: /setfilter minPrice maxPrice minArea maxArea location\nPrimer: /setfilter 300 800 30 80 Beograd");
            return;
        }

        try {
            filterRepository.findByActiveTrue().forEach(f -> {
                f.setActive(false);
                filterRepository.save(f);
            });

            Filter filter = new Filter();
            filter.setMinPrice(Double.parseDouble(parts[1]));
            filter.setMaxPrice(Double.parseDouble(parts[2]));
            filter.setMinArea(Double.parseDouble(parts[3]));
            filter.setMaxArea(Double.parseDouble(parts[4]));
            String location = String.join(" ", java.util.Arrays.copyOfRange(parts, 5, parts.length));
            filter.setLocation(location);
            filter.setActive(true);
            filterRepository.save(filter);

            sendMessage(chatId, "Filter setup!");
        } catch (Exception e) {
            sendMessage(chatId, "Error while setting filters.");
        }
    }

    private void handleShowFilter(long chatId) {
        List<Filter> filters = filterRepository.findByActiveTrue();
        if (filters.isEmpty()) {
            sendMessage(chatId, "No active filter.");
            return;
        }

        Filter f = filters.get(0);
        String msg = String.format(
                "*Active filter:*\n Price: %.0f - %.0f €\n Area: %.0f - %.0f m²\n Location: %s",
                f.getMinPrice(), f.getMaxPrice(),
                f.getMinArea(), f.getMaxArea(),
                f.getLocation()
        );
        sendMessage(chatId, msg);
    }

    private void handleClearFilter(long chatId) {
        filterRepository.findByActiveTrue().forEach(f -> {
            f.setActive(false);
            filterRepository.save(f);
        });
        sendMessage(chatId, "Filter deleted.");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }
}