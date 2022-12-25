package com.github.vendigo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameLocale {
    UA("messagesUa"), EN("messagesEn");

    String messagesField;

}
