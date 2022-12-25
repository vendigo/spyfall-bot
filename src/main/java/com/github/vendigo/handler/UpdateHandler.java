package com.github.vendigo.handler;

import com.github.vendigo.exception.GameFlowException;
import com.github.vendigo.model.GlobalConfig;
import com.github.vendigo.model.LocaleConfig;
import com.github.vendigo.service.SpyfallGameService;
import com.google.api.client.http.HttpStatusCodes;
import com.google.common.collect.ImmutableMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Map;
import java.util.function.Function;

public class UpdateHandler {

    private static final String NEW_GAME_COMMAND = "/newgame";
    private static final String START_GAME_COMMAND = "/start";
    private static final String FORCE_START_GAME_COMMAND = "/forcestart";
    private static final String IN_COMMAND = "/in";
    private static final String RULES_COMMAND = "/rules";

    private final SpyfallGameService spyfallGameService;
    private final Map<String, Function<Message, String>> commandStrategies;
    private final LocaleConfig config;

    public UpdateHandler(SpyfallGameService spyfallGameService, LocaleConfig config) {
        this.spyfallGameService = spyfallGameService;
        this.config = config;
        this.commandStrategies = ImmutableMap
                .<String, Function<Message, String>>builder()
                .put(NEW_GAME_COMMAND, spyfallGameService::createNewGame)
                .put(IN_COMMAND, spyfallGameService::addPlayer)
                .put(START_GAME_COMMAND, message -> spyfallGameService.startNewGame(message, false))
                .put(FORCE_START_GAME_COMMAND, message -> spyfallGameService.startNewGame(message, true))
                .put(RULES_COMMAND, spyfallGameService::getRules)
                .build();
    }

    public SendMessage handleUpdate(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getText() == null) {
            return null;
        }

        return new SendMessage(message.getChatId().toString(), processMessage(message));
    }

    private String processMessage(Message message) {
        if (Boolean.FALSE.equals(message.getChat().isGroupChat())) {
            return spyfallGameService.howToUse(message);
        }

        try {
            return processCommand(message);
        } catch (Exception ex) {
            return processException(ex);
        }
    }

    private String processException(Exception ex) {
        if (ex instanceof GameFlowException) {
            return ex.getMessage();
        }
        if (ex instanceof TelegramApiRequestException apiRequestException
                && apiRequestException.getErrorCode() == HttpStatusCodes.STATUS_CODE_FORBIDDEN) {
            return config.cantStartChat();
        }
        ex.printStackTrace();
        return config.unknownError();
    }

    private String processCommand(Message message) {
        String messageText = message.getText();

        for (var entry : commandStrategies.entrySet()) {
            if (messageText.startsWith(entry.getKey())) {
                return entry.getValue().apply(message);
            }
        }

        return spyfallGameService.howToUseGroups(message);
    }
}
