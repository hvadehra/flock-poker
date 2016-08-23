package co.flock.web;


import co.flock.web.servlets.PingServlet;
import co.flock.web.servlets.PokerServlet;
import com.google.inject.servlet.ServletModule;

/**
 * Created by hemanshu.v on 6/14/16.
 */
public class WebModule extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/ping").with(PingServlet.class);
        serve("/poker").with(PokerServlet.class);
    }
}
