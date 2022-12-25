package com.github.vendigo.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vendigo.context.AppContext;
import com.github.vendigo.handler.UpdateHandler;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedWriter;

public class MainFunction implements HttpFunction {

    private final AppContext appContext = new AppContext();
    private final UpdateHandler updateHandler = appContext.getUpdateHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Update update = objectMapper.readValue(request.getReader(), Update.class);
        BotApiMethod<?> answer = updateHandler.handleUpdate(update);
        response.setContentType("application/json");
        BufferedWriter writer = response.getWriter();
        String responseJson = objectMapper.writeValueAsString(answer);
        writer.write(responseJson);
    }
}
