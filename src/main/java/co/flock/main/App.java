package co.flock.main;

import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class.getCanonicalName());

    private static void start(Module... modules) {
        logger.warn("Starting server...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        Injector injector = Guice.createInjector(Modules.override(new ApplicationModule()).with(modules));
        long startUpTime = stopwatch.elapsed(TimeUnit.SECONDS);
        logger.warn("Server started in {} seconds", startUpTime);
    }

    public static void main(String[] args) {
        start();
    }
}
