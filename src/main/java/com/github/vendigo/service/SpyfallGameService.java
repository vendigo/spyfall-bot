package com.github.vendigo.service;

import com.github.vendigo.exception.GameFlowException;
import com.github.vendigo.model.GameEntity;
import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;

@AllArgsConstructor
@Slf4j
public class SpyfallGameService {

    public static final String NEW_GAME_STATE = "NEW_GAME";
    public static final String STARTED_GAME_STATE = "STARTED_GAME";
    private static final int LOCATIONS_PER_GAME = 10;

    private final GlobalConfig config;
    private final DataStoreService dataStoreService;
    private final MessageSendingService messageSendingService;

    public String createNewGame(Message message) {
        Long chatId = message.getChatId();
        Optional<GameEntity> game = dataStoreService.findGame(chatId);
        Long gameId = game.map(GameEntity::gameId)
                .orElse(1L);
        String lastGameState = game.map(GameEntity::gameState)
                .orElse(STARTED_GAME_STATE);

        if (NEW_GAME_STATE.equals(lastGameState)) {
            throw new GameFlowException(config.gameNotStarted());
        }

        List<String> locations = choseLocations();
        var gameEntity = new GameEntity(chatId, gameId, locations, Timestamp.now(), NEW_GAME_STATE, Set.of());
        dataStoreService.saveGame(gameEntity);
        return config.newGame();
    }

    public String howToUse(Message message) {
        return config.howToUse();
    }

    public String howToUseGroups(Message message) {
        return config.howToUseGroup();
    }

    private List<String> choseLocations() {
        var allLocations = new ArrayList<>(config.locations());
        Collections.shuffle(allLocations);
        return allLocations.subList(0, LOCATIONS_PER_GAME);
    }

    public String addPlayer(Message message) {
        Long chatId = message.getChatId();
        GameEntity game = dataStoreService.findGame(chatId)
                .orElseThrow(() -> new GameFlowException(config.gameNotFound()));

        User from = message.getFrom();
        Long playerId = from.getId();
        dataStoreService.saveGame(game.addPlayer(playerId));

        log.info("Added player {} to game {}", playerId, chatId);
        return config.playerAdded().formatted(getUsername(from));
    }

    public String startNewGame(Message message, boolean forceStart) {
        Long chatId = message.getChatId();
        GameEntity game = dataStoreService.findGame(chatId)
                .orElseThrow(() -> new GameFlowException(config.gameNotFound()));
        if (STARTED_GAME_STATE.equals(game.gameState())) {
            throw new GameFlowException(config.gameAlreadyStarted());
        }

        if (!forceStart && game.players().size() < 3) {
            return config.notEnoughPlayers();
        }

        GameEntity startedGame = game.withState(STARTED_GAME_STATE);
        dataStoreService.saveGame(startedGame);
        sendRoles(startedGame);

        log.info("Started game in chat {}", chatId);
        return config.gameStarted().formatted(game.gameId(), game.locations());
    }

    @SneakyThrows
    private void sendRoles(GameEntity game) {
        Set<Long> players = game.players();
        String randomLocation = randomElement(game.locations());
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
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }
}
