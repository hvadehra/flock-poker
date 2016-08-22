package co.flock.main;

/**
 * Created by hemanshu.v on 6/14/16.
 */

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;


public class PropertiesModule extends AbstractModule {
    private static Logger log = LoggerFactory.getLogger(PropertiesModule.class.getCanonicalName());

    @Override
    protected final void configure() {
        PropertiesService propertiesService = new PropertiesService();
        logProperties(propertiesService.properties);
        Names.bindProperties(binder(), propertiesService.properties);
        bind(PropertiesService.class).toInstance(propertiesService);
    }

    private void logProperties(Properties properties) {
        StringBuilder builder = new StringBuilder("Binding properties: \n");
        for (Map.Entry entry : properties.entrySet()) {
            builder.append(format("  %s=%s\n", entry.getKey(), entry.getValue()));
        }
        log.info(builder.toString());
    }

    public static class PropertiesService {
        private final Properties properties;

        public PropertiesService() {
            properties = loadConfig("settings");
        }

        private static Properties loadConfig(String propertiesFileName) {
            try {
                Properties properties = new Properties();
                Configuration config = new PropertiesConfiguration(propertiesFileName + ".properties");
                Iterator<String> keys = config.getKeys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    properties.put(key, config.getString(key));
                }

                return properties;
            } catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        public Properties properties() {
            return properties;
        }
    }
}