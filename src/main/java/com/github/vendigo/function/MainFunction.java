package com.github.vendigo.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
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
    private final ObjectReader requestReader = objectMapper.readerFor(Update.class);
    private final ObjectWriter responseWriter = objectMapper.writer();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Update update = requestReader.readValue(request.getReader());
        BotApiMethod<?> answer = updateHandler.handleUpdate(update);
        BufferedWriter writer = response.getWriter();
        String responseJson = responseWriter.writeValueAsString(answer);
        writer.write(responseJson);
        response.setContentType("application/json");
    }
}
