package com.github.vendigo.service;

import com.github.vendigo.model.GlobalConfig;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
public class DataStoreService {

    private final Datastore datastore;

    public GlobalConfig getGlobalConfig() {
        Key configKey = datastore.newKeyFactory()
                .setNamespace("spyfall")
                .setKind("Config")
                .newKey("globalConfig");
        Entity configEntity = datastore.get(configKey);

        String helloSingle = configEntity.getString("helloSingle");
        String helloGroup = configEntity.getString("helloGroup");
        String letsRollCall = configEntity.getString("letsRollcall");
        List<String> locations = Stream.of(configEntity.getString("locations").split(", "))
                .toList();
        return new GlobalConfig(helloSingle, helloGroup, letsRollCall, locations);
    }
}
