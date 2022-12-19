package com.github.vendigo.service;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageSendingService extends DefaultAbsSender {

    private final String botToken = checkNotNull(System.getenv().get("BOT_TOKEN"), "Bot token is not set");

    public MessageSendingService() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
