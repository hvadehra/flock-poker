package co.flock.main;

import co.flock.app.poker.FlockPokerEventHandler;
import co.flock.web.WebModule;
import co.flock.web.WebServer;
import co.flock.www.FlockEventsHandlerClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Created by hemanshu.v on 6/14/16.
 */
class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PropertiesModule());
        install(new WebModule());
        bind(WebServer.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public FlockEventsHandlerClient flockEventsHandlerClient(FlockPokerEventHandler handler,
                                                             @Named("app.secret") String appSecret) {
        return new FlockEventsHandlerClient(handler, appSecret);
    }

}
