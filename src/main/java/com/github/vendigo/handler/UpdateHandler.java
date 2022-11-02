package com.github.vendigo.handler;

import com.github.vendigo.exception.GameFlowException;
import com.github.vendigo.service.SpyfallGameService;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@AllArgsConstructor
public class UpdateHandler {

    private static final String NEW_GAME_COMMAND = "/newgame";
    private static final String START_GAME_COMMAND = "/start";
    private static final String IN_COMMAND = "/in";

    private final SpyfallGameService spyfallGameService;

    public BotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getText() == null) {
            return null;
        }

        String responseTo = message.getChatId().toString();

        if (message.getChat().isGroupChat()) {
            try {
                if (message.getText().startsWith(NEW_GAME_COMMAND)) {
                    String response = spyfallGameService.createNewGame(message);
                    return new SendMessage(responseTo, response);
                }

                if (message.getText().startsWith(IN_COMMAND)) {
                    String response = spyfallGameService.addPlayer(message);
                    return new SendMessage(responseTo, response);
                }

                if (message.getText().startsWith(START_GAME_COMMAND)) {
                    String response = spyfallGameService.startNewGame(message);
                    return new SendMessage(responseTo, response);
                }
            } catch (GameFlowException ex) {
                return new SendMessage(responseTo, ex.getMessage());
            }

            return new SendMessage(responseTo, spyfallGameService.howToUseGroups(message));
        }

        return new SendMessage(responseTo, spyfallGameService.howToUse(message));
    }
}
