package com.github.vendigo.service;

import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataStoreService {

    private final Datastore datastore;

    public GlobalConfig getGlobalConfig() {
        Key configKey = datastore.newKeyFactory()
                .setNamespace("spyfall")
                .setKind("Config")
                .newKey("globalConfig");
        Entity configEntity = datastore.get(configKey);

        String helloMessage = configEntity.getString("helloMessage");
        String locations = configEntity.getString("locations");
        return new GlobalConfig(helloMessage, locations);
    }
}
