package com.github.vendigo.model;

import java.util.Set;

import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableSet;

public record GameEntity(Long chatId, Long gameId, Timestamp creationTime, String gameState, Set<Long> players) {

    public GameEntity addPlayer(Long playerId) {
        Set<Long> updatedPlayers = ImmutableSet.<Long>builder()
            .addAll(players)
            .add(playerId)
            .build();
        return new GameEntity(chatId, gameId, creationTime, gameState, updatedPlayers);
    }

    public GameEntity withState(String state) {
        return new GameEntity(chatId, gameId, creationTime, state, players);
    }
}
