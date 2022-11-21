package com.github.vendigo.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class DataStoreService {

    private static final String CONFIG_KIND = "Config";
    private static final String GAME_KIND = "Game";
    private static final String GAME_ID = "gameId";
    private static final String CREATION_TIME = "creationTime";
    private static final String GAME_STATE = "gameState";
    private static final String PLAYERS = "players";

    private final Datastore datastore;

    public GlobalConfig getGlobalConfig() {
        Key configKey = newKeyFactory()
            .setKind(CONFIG_KIND)
            .newKey("globalConfig");
        Entity e = datastore.get(configKey);

        return new GlobalConfig(
            e.getString("newGame"),
            e.getString("gameStarted"),
            e.getString("howToUse"),
            e.getString("howToUseGroup"),
            e.getString("gameNotStarted"),
            e.getString("gameNotFound"),
            e.getString("gameAlreadyStarted"),
            e.getString("playerAdded"),
            e.getString("playerLocation"),
            e.getString("playerSpy"),
            e.getString("notEnoughPlayers"),
            e.getString("rules"),
            parseLocations(e));
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

    private List<String> parseLocations(Entity entity) {
        return entity.<Value<String>>getList("locations")
            .stream()
            .map(Value::get)
            .toList();
    }

    private KeyFactory newKeyFactory() {
        return datastore.newKeyFactory()
            .setNamespace("spyfall");
    }
}
