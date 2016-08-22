package co.flock.web;

/**
 * Created by hemanshu.v on 6/14/16.
 */

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.webapp.WebAppContext;


@Singleton
public class WebServer {
    private Server server;

    @Inject
    public WebServer(@Named("webservice.port") int servicePort) {
        server = new Server(servicePort);
        start();
    }

    private void start() {
        try {

            WebAppContext appContext = new WebAppContext(server, "poker", "/");

            final RequestLogHandler requestLogHandler = new RequestLogHandler();
            appContext.addHandler(requestLogHandler);

            appContext.addFilter(GuiceFilter.class, "/*", 0);

            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws Exception {
        server.stop();
    }
}

