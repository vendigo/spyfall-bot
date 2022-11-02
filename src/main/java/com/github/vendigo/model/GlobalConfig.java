package com.github.vendigo.model;

import java.util.List;

public record GlobalConfig(String newGame,
                           String gameStarted,
                           String howToUse,
                           String howToUseGroup,
                           String gameNotStarted,
                           String gameNotFound,
                           String playerAdded,
                           List<String> locations) {
}
