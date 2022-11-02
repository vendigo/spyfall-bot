package com.github.vendigo.service;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

public class MessageSendingService extends DefaultAbsSender {

    private final String botToken = System.getenv().get("BOT_TOKEN");

    public MessageSendingService() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
