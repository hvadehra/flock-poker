package co.flock.main;

import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

    private static void start(Module... modules) {
        logger.severe("Starting server...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        Injector injector = Guice.createInjector(Modules.override(new ApplicationModule()).with(modules));
        long startUpTime = stopwatch.elapsed(TimeUnit.SECONDS);
        logger.severe("Server started in " + startUpTime + " seconds.");
    }

    public static void main(String[] args) {
        start();
    }
}
