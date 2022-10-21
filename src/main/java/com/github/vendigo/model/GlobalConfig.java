package com.github.vendigo.model;

import java.util.List;

public record GlobalConfig(String helloSingle, String helloGroup, String letsRollCall, List<String> availableLocations) {
}
