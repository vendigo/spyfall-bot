package com.github.vendigo.handler;

import com.github.vendigo.service.SpyfallGameService;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@AllArgsConstructor
public class UpdateHandler {

    private static final String NEW_GAME_COMMAND = "/newgame";

    private final SpyfallGameService spyfallGameService;

    public BotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getText() == null) {
            return null;
        }

        String responseTo = message.getChatId().toString();
        String username = getUsername(message.getFrom());

        if (message.getChat().isGroupChat()) {
            if (message.getText().startsWith(NEW_GAME_COMMAND)) {
                String response = spyfallGameService.startNewGame(message.getChatId());
                return new SendMessage(responseTo, response);
            }

            return new SendMessage(responseTo, spyfallGameService.howToUseGroups());
        }

        return new SendMessage(responseTo, spyfallGameService.howToUse(username));
    }

    private static String getUsername(User user) {
        return user.getUserName() == null ? user.getFirstName() : user.getUserName();
    }
}
