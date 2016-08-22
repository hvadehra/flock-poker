package co.flock.main;

import co.flock.web.WebModule;
import co.flock.web.WebServer;
import com.google.inject.AbstractModule;

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
}
