package co.flock.web;


import co.flock.web.servlets.MainServlet;
import co.flock.web.servlets.PingServlet;
import com.google.inject.servlet.ServletModule;

/**
 * Created by hemanshu.v on 6/14/16.
 */
public class WebModule extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/ping").with(PingServlet.class);
        serve("/main").with(MainServlet.class);
    }
}
