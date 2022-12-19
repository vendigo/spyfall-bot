package com.github.vendigo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import com.github.vendigo.exception.GameFlowException;
import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class SpyfallGameService {

    private static final String NEW_GAME_STATE = "NEW_GAME";
    private static final String STARTED_GAME_STATE = "STARTED_GAME";
    private static final int LOCATIONS_PER_GAME = 30;
    private static final Random RANDOM = new Random();
    private static final int MIN_PLAYERS = 3;
    private static final long DEFAULT_GAME_ID = 1L;
    private static final String LIST_EL_TEMPLATE = "\uD83D\uDD38 %s\n";

    private final GlobalConfig config;
    private final DataStoreService dataStoreService;
    private final MessageSendingService messageSendingService;


    public String createNewGame(Message message) {
        log.info("[Command] Create new game");
        Long chatId = message.getChatId();
        Optional<GameEntity> game = dataStoreService.findGame(chatId);
        Long gameId = game.map(GameEntity::gameId)
            .orElse(DEFAULT_GAME_ID);
        String lastGameState = game.map(GameEntity::gameState)
            .orElse(STARTED_GAME_STATE);

        var gameEntity = new GameEntity(chatId, gameId, Timestamp.now(), NEW_GAME_STATE, Set.of());
        dataStoreService.saveGame(gameEntity);
        return config.newGame();
    }

    public String howToUse(Message message) {
        log.info("[Command] How to use");
        return config.howToUse();
    }

    public String howToUseGroups(Message message) {
        log.info("[Command] How to use groups");
        return config.howToUseGroup();
    }

    public String addPlayer(Message message) {
        log.info("[Command] Add player");
        Long chatId = message.getChatId();
        GameEntity game = findNewGame(chatId);

        User from = message.getFrom();
        Long playerId = from.getId();
        dataStoreService.saveGame(game.addPlayer(playerId));

        log.info("Added player {} to game {}", playerId, chatId);
        return config.playerAdded().formatted(getUsername(from));
    }

    public String startNewGame(Message message, boolean forceStart) {
        log.info("[Command] Start new game");
        Long chatId = message.getChatId();
        GameEntity game = findNewGame(chatId);

        if (!forceStart && game.players().size() < MIN_PLAYERS) {
            return config.notEnoughPlayers();
        }

        List<String> locations = choseLocations();
        sendRoles(game, locations);
        GameEntity startedGame = game.withState(STARTED_GAME_STATE);
        dataStoreService.saveGame(startedGame);

        log.info("Started game in chat {}", chatId);
        return formatGameStartedMessage(game, locations);
    }

    public String getRules(Message message) {
        log.info("[Command] Get rules");
        return config.rules();
    }

    private String formatGameStartedMessage(GameEntity game, List<String> locations) {
        String locationsJoined = locations.stream()
            .map(LIST_EL_TEMPLATE::formatted)
            .collect(Collectors.joining());
        return config.gameStarted().formatted(game.gameId(), locationsJoined);
    }

    private List<String> choseLocations() {
        var allLocations = new ArrayList<>(config.locations());
        Collections.shuffle(allLocations);
        return allLocations.subList(0, LOCATIONS_PER_GAME);
    }

    private GameEntity findNewGame(Long chatId) {
        GameEntity game = dataStoreService.findGame(chatId)
            .orElseThrow(() -> new GameFlowException(config.gameNotFound()));
        if (STARTED_GAME_STATE.equals(game.gameState())) {
            throw new GameFlowException(config.gameAlreadyStarted());
        }
        return game;
    }

    @SneakyThrows
    private void sendRoles(GameEntity game, List<String> locations) {
        Set<Long> players = game.players();
        String randomLocation = randomElement(locations);
        Long spyId = randomElement(players);

        for (Long playerId : players) {
            String message = playerId.equals(spyId) ? config.playerSpy().formatted(game.gameId()) :
                             config.playerLocation().formatted(game.gameId(), randomLocation);
            messageSendingService.execute(new SendMessage(playerId.toString(), message));
        }
    }

    private static String getUsername(User user) {
        return user.getUserName() == null ? user.getFirstName() : user.getUserName();
    }

    private static <T> T randomElement(Collection<T> collection) {
        List<T> list = List.copyOf(collection);
        int randomIndex = RANDOM.nextInt(list.size());
        return list.get(randomIndex);
    }
}
