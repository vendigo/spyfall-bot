package com.github.vendigo.handler;

import com.github.vendigo.model.GlobalConfig;
import com.github.vendigo.service.DataStoreService;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@AllArgsConstructor
public class UpdateHandler {

    private final DataStoreService dataStoreService;

    public BotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId().toString());

        GlobalConfig globalConfig = dataStoreService.getGlobalConfig();
        Chat chat = message.getChat();

        if (chat.isGroupChat()) {
            answer.setText(globalConfig.helloGroup());
            return answer;
        }

        String helloMessage = globalConfig.helloSingle().formatted(getUsername(message.getFrom()));
        answer.setText(helloMessage);
        return answer;
    }

    private static String getUsername(User user) {
        return user.getUserName() == null ? user.getFirstName() : user.getUserName();
    }
}
