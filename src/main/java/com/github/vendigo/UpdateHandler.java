package com.github.vendigo;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedWriter;

public class UpdateHandler implements HttpFunction {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        Key configKey = datastore.newKeyFactory()
                .setNamespace("spyfall")
                .setKind("Config")
                .newKey("globalConfig");
        Entity configEntity = datastore.get(configKey);

        String helloMessage = configEntity.getString("helloMessage");
        String locations = configEntity.getString("locations");

        BufferedWriter writer = response.getWriter();
        writer.write(helloMessage+"\n");
        writer.write("Our locations: " + locations);
    }
}
