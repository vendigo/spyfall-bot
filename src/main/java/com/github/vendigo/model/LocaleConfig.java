package com.github.vendigo.model;

import java.util.List;

public record LocaleConfig(String newGame,
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
                           List<String> locations) {
}
