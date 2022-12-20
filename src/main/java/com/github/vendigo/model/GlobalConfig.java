package com.github.vendigo.model;

import java.util.List;

public record GlobalConfig(String newGame,
                           String gameStarted,
                           String howToUse,
                           String howToUseGroup,
                           String gameNotFound,
                           String gameAlreadyStarted,
                           String playerAdded,
                           String playerLocation,
                           String playerSpy,
                           String notEnoughPlayers,
                           String rules,
                           String unknownError,
                           String cantStartChat,
                           int locationsPerGame,
                           List<String> locations) {
}
