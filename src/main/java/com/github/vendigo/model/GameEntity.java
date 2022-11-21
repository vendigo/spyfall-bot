package com.github.vendigo.model;

import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public record GameEntity(Long chatId, Long gameId, List<String> locations, Timestamp creationTime, String gameState, Set<Long> players) {

    public GameEntity addPlayer(Long playerId) {
        Set<Long> updatedPlayers = ImmutableSet.<Long>builder()
                .addAll(players)
                .add(playerId)
                .build();
        return new GameEntity(chatId, gameId, locations, creationTime, gameState, updatedPlayers);
    }

    public GameEntity withState(String state) {
        return new GameEntity(chatId, gameId, locations, creationTime, state, players);
    }
}
