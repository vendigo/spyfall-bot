package com.github.vendigo.context;

import com.github.vendigo.handler.UpdateHandler;
import com.github.vendigo.service.DataStoreService;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import lombok.Getter;

@Getter
public class AppContext {

    private final Datastore dataStore = DatastoreOptions.getDefaultInstance().getService();
    private final DataStoreService dataStoreService = new DataStoreService(dataStore);
    private final UpdateHandler updateHandler = new UpdateHandler(dataStoreService);

}
