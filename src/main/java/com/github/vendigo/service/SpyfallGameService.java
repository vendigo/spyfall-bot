package com.github.vendigo.service;

import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GlobalConfig;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class SpyfallGameService {

    private static final int LOCATIONS_PER_GAME = 10;

    private final GlobalConfig config;
    private final DataStoreService dataStoreService;

    public String startNewGame(Long chatId) {
        List<String> locations = choseLocations();

        Long gameId = dataStoreService.getLastGameId(chatId) + 1;
        var gameEntity = new GameEntity(chatId, gameId, locations, LocalDateTime.now());
        dataStoreService.saveNewGame(gameEntity);

        return config.gameStarted().formatted(gameId, locations);
    }

    public String howToUse(String username) {
        return config.howToUse().formatted(username);
    }

    public String howToUseGroups() {
        return config.howToUseGroup();
    }

    private List<String> choseLocations() {
        var allLocations = new ArrayList<>(config.availableLocations());
        Collections.shuffle(allLocations);
        return allLocations.subList(0, LOCATIONS_PER_GAME);
    }
}
