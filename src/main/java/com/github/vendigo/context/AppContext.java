package com.github.vendigo.context;

import com.github.vendigo.handler.UpdateHandler;
import com.github.vendigo.model.GameLocale;
import com.github.vendigo.model.LocaleConfig;
import com.github.vendigo.service.DataStoreService;
import com.github.vendigo.service.MessageSendingService;
import com.github.vendigo.service.SpyfallGameService;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class AppContext {

    private final String locale = checkNotNull(System.getenv().get("LOCALE"), "Locale is not set");
    private final Datastore dataStore = DatastoreOptions.getDefaultInstance().getService();
    private final MessageSendingService messageSendingService = new MessageSendingService();
    private final DataStoreService dataStoreService = new DataStoreService(dataStore);
    private final LocaleConfig config = getLocaleConfig();
    private final SpyfallGameService spyfallGameService = new SpyfallGameService(config, dataStoreService, messageSendingService);
    private final UpdateHandler updateHandler = new UpdateHandler(spyfallGameService, config);


    private LocaleConfig getLocaleConfig() {
        var gameLocale = locale.toUpperCase().equals("UA") ? GameLocale.UA : GameLocale.EN;
        return dataStoreService.getConfig(gameLocale);
    }
}
