package com.github.vendigo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GameLocale;
import com.github.vendigo.model.LocaleConfig;
import com.google.cloud.datastore.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class DataStoreService {

    private static final String CONFIG_KIND = "Config";
    private static final String GAME_KIND = "Game";
    private static final String GAME_ID = "gameId";
    private static final String CREATION_TIME = "creationTime";
    private static final String GAME_STATE = "gameState";
    private static final String PLAYERS = "players";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Datastore datastore;

    @SneakyThrows
    public LocaleConfig getConfig(GameLocale locale) {
        Key configKey = newKeyFactory()
                .setKind(CONFIG_KIND)
                .newKey("globalConfig");
        Entity e = datastore.get(configKey);

        return objectMapper.readValue(e.getString(locale.getMessagesField()), LocaleConfig.class);
    }

    public Optional<GameEntity> findGame(Long chatId) {
        Key configKey = newKeyFactory()
                .setKind(GAME_KIND)
                .newKey(chatId);
        Entity gameEntity = datastore.get(configKey);
        if (gameEntity == null) {
            return Optional.empty();
        }

        return Optional.of(new GameEntity(
                chatId,
                gameEntity.getLong(GAME_ID),
                gameEntity.getTimestamp(CREATION_TIME),
                gameEntity.getString(GAME_STATE),
                parsePlayers(gameEntity)
        ));
    }

    private Set<Long> parsePlayers(Entity gameEntity) {
        return gameEntity.<Value<Long>>getList(PLAYERS)
                .stream()
                .map(Value::get)
                .collect(Collectors.toSet());
    }

    public void saveGame(GameEntity game) {
        log.info("Game {} for chat {} is saved", game.gameId(), game.chatId());
        Key gameKey = newKeyFactory()
                .setKind(GAME_KIND)
                .newKey(game.chatId());

        List<LongValue> players = game.players().stream()
                .map(LongValue::of)
                .toList();
        Entity gameEntity = Entity.newBuilder(gameKey)
                .set(GAME_ID, game.gameId())
                .set(CREATION_TIME, TimestampValue.of(game.creationTime()))
                .set(GAME_STATE, game.gameState())
                .set(PLAYERS, players)
                .build();
        datastore.put(gameEntity);
    }

    private KeyFactory newKeyFactory() {
        return datastore.newKeyFactory()
                .setNamespace("spyfall");
    }
}
