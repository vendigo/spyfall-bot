package com.github.vendigo.service;

import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.datastore.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        String newGame = configEntity.getString("newGame");
        String gameStarted = configEntity.getString("gameStarted");
        String howToUse = configEntity.getString("howToUse");
        String howToUseGroup = configEntity.getString("howToUseGroup");
        String gameNotStarted = configEntity.getString("gameNotStarted");
        String gameNotFound = configEntity.getString("gameNotFound");
        String gameAlreadyStarted = configEntity.getString("gameAlreadyStarted");
        String playerAdded = configEntity.getString("playerAdded");
        String playerLocation = configEntity.getString("playerLocation");
        String playerSpy = configEntity.getString("playerSpy");
        String notEnoughPlayers = configEntity.getString("notEnoughPlayers");
        List<String> locations = configEntity.<Value<String>>getList("locations").stream()
                .map(Value::get)
                .toList();
        return new GlobalConfig(newGame, gameStarted, howToUse, howToUseGroup, gameNotStarted, gameNotFound,
                gameAlreadyStarted, playerAdded, playerLocation, playerSpy, notEnoughPlayers, locations);
    }

    public Optional<GameEntity> findGame(Long chatId) {
        Key configKey = datastore.newKeyFactory()
                .setNamespace("spyfall")
                .setKind("Game")
                .newKey(chatId);
        Entity gameEntity = datastore.get(configKey);
        if (gameEntity == null) {
            return Optional.empty();
        }

        var game = new GameEntity(
                chatId,
                gameEntity.getLong("gameId"),
                gameEntity.<Value<String>>getList("locations").stream().map(Value::get).toList(),
                gameEntity.getTimestamp("creationTime"),
                gameEntity.getString("gameState"),
                gameEntity.<Value<Long>>getList("players").stream().map(Value::get).collect(Collectors.toSet())
        );
        return Optional.of(game);
    }

    public void saveGame(GameEntity game) {
        log.info("Game {} for chat {} is saved", game.gameId(), game.chatId());
        Key gameKey = datastore.newKeyFactory()
                .setNamespace("spyfall")
                .setKind("Game")
                .newKey(game.chatId());

        List<StringValue> locations = game.locations().stream()
                .map(StringValue::of)
                .toList();
        List<LongValue> players = game.players().stream()
                .map(LongValue::of)
                .toList();
        Entity gameEntity = Entity.newBuilder(gameKey)
                .set("gameId", game.gameId())
                .set("locations", ListValue.of(locations))
                .set("creationTime", TimestampValue.of(game.creationTime()))
                .set("gameState", game.gameState())
                .set("players", players)
                .build();
        datastore.put(gameEntity);
    }
}
