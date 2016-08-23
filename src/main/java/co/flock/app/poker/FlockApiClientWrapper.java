package co.flock.app.poker;

import co.flock.www.FlockApiClient;
import co.flock.www.model.messages.FlockMessage;
import co.flock.www.model.messages.Message;

/**
 * Created by hemanshu.v on 8/23/16.
 */
public class FlockApiClientWrapper {

    private static final boolean PROD_ENV = false;

    public void sendMessage(String userToken, String to, String msg) throws Exception {
        FlockApiClient flockApiClient = new FlockApiClient(userToken, PROD_ENV);
        Message message = new Message(to, msg);
        FlockMessage flockMessage = new FlockMessage(message);
        flockApiClient.chatSendMessage(flockMessage);
    }

    public void sendError(String userToken, String to, Throwable t) throws Exception {
        FlockApiClient flockApiClient = new FlockApiClient(userToken, PROD_ENV);
        Message message = new Message(to, "ERROR: \n" + t.getMessage());
        FlockMessage flockMessage = new FlockMessage(message);
        flockApiClient.chatSendMessage(flockMessage);
    }
}
