package com.github.vendigo.model;

import java.time.LocalDateTime;
import java.util.List;

public record GameEntity(Long chatId, Long gameId, List<String> locations, LocalDateTime creationTime) {
}
