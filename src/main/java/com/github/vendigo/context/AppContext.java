package com.github.vendigo.context;

import com.github.vendigo.handler.UpdateHandler;
import com.github.vendigo.model.GlobalConfig;
import com.github.vendigo.service.DataStoreService;
import com.github.vendigo.service.MessageSendingService;
import com.github.vendigo.service.SpyfallGameService;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import lombok.Getter;

@Getter
public class AppContext {

    private final Datastore dataStore = DatastoreOptions.getDefaultInstance().getService();
    private final MessageSendingService messageSendingService = new MessageSendingService();
    private final DataStoreService dataStoreService = new DataStoreService(dataStore);
    private final GlobalConfig config = dataStoreService.getGlobalConfig();
    private final SpyfallGameService spyfallGameService = new SpyfallGameService(config, dataStoreService, messageSendingService);
    private final UpdateHandler updateHandler = new UpdateHandler(spyfallGameService, config);

}
