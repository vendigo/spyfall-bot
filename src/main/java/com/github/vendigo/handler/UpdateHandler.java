package com.github.vendigo.handler;

import com.github.vendigo.model.GlobalConfig;
import com.github.vendigo.service.DataStoreService;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@AllArgsConstructor
public class UpdateHandler {

    private final DataStoreService dataStoreService;

    public BotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId().toString());

        GlobalConfig globalConfig = dataStoreService.getGlobalConfig();

        answer.setText(globalConfig + "\n"+ "Available locations: "+ globalConfig.availableLocations());
        return answer;
    }
}
