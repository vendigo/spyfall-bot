package com.github.vendigo.service;

import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
public class DataStoreService {

    private final Datastore datastore;

    public GlobalConfig getGlobalConfig() {
        Key configKey = datastore.newKeyFactory()
                .setNamespace("spyfall")
                .setKind("Config")
                .newKey("globalConfig");
        Entity configEntity = datastore.get(configKey);

        String gameStarted = configEntity.getString("gameStarted");
        String howToUse = configEntity.getString("howToUse");
        String howToUseGroup = configEntity.getString("howToUseGroup");
        List<String> locations = Stream.of(configEntity.getString("locations").split(", "))
                .toList();
        return new GlobalConfig(gameStarted, howToUse, howToUseGroup, locations);
    }

    public Long getLastGameId(Long chatId) {
        return 1L;
    }

    public void saveNewGame(GameEntity game) {
        log.info("Game {} for chat {} is saved", game.gameId(), game.chatId());
    }
}
