package com.github.vendigo.model;

import java.util.List;

public record GlobalConfig(String gameStarted, String howToUse, String howToUseGroup, List<String> availableLocations) {
}
