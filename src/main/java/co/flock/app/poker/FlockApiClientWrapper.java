package co.flock.app.poker;

import co.flock.www.FlockApiClient;
import co.flock.www.model.PublicProfile;
import co.flock.www.model.messages.FlockMessage;
import co.flock.www.model.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by hemanshu.v on 8/23/16.
 */
public class FlockApiClientWrapper {

    private final Logger log = LoggerFactory.getLogger(FlockApiClientWrapper.class.getCanonicalName());

    private static final boolean PROD_ENV = false;
    private static final boolean DEBUG = true;

    public void sendMessage(String userToken, String to, String msg) throws Exception {
        if (msg == null) return;

        if (DEBUG) {
            log.info(msg);
            return;
        }
        FlockApiClient flockApiClient = getClient(userToken);
        Message message = new Message(to, msg);
        FlockMessage flockMessage = new FlockMessage(message);
        flockApiClient.chatSendMessage(flockMessage);
    }

    public void sendError(String userToken, String to, Throwable t) throws Exception {
        if (DEBUG) {
            log.error("ERROR", t);
            return;
        }
        FlockApiClient flockApiClient = getClient(userToken);
        Message message = new Message(to, "ERROR: \n" + t.getMessage());
        FlockMessage flockMessage = new FlockMessage(message);
        flockApiClient.chatSendMessage(flockMessage);
    }

    public PublicProfile[] getGroupMembers(String userToken, String groupId) throws Exception {
        return getClient(userToken).getGroupMembers(groupId);
    }

    private FlockApiClient getClient(String userToken) {
        return new FlockApiClient(userToken, PROD_ENV);
    }
}
