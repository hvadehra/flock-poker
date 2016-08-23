package co.flock.app.poker;

import co.flock.www.FlockApiClient;
import co.flock.www.model.PublicProfile;
import co.flock.www.model.messages.FlockMessage;
import co.flock.www.model.messages.Message;
import co.flock.www.model.messages.SendAs;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by hemanshu.v on 8/23/16.
 */
@Singleton
public class FlockApiClientWrapper {

    private final Logger log = LoggerFactory.getLogger(FlockApiClientWrapper.class.getCanonicalName());

    private static final boolean PROD_ENV = false;
    private static final boolean DEBUG = false;
    private final UserStore userStore;
    private final String appId;

    @Inject
    public FlockApiClientWrapper(UserStore userStore,
                                 @Named("app.id") String appId) {
        this.userStore = userStore;
        this.appId = appId;
    }

    public void sendMessage(String userToken, String to, String msg) throws Exception {
        if (msg == null) return;

        log.info(msg);
        if (DEBUG) {
            return;
        }
        FlockApiClient flockApiClient = getClient(userToken);
        Message message = new Message(to, msg);
        FlockMessage flockMessage = new FlockMessage(message);
        flockApiClient.chatSendMessage(flockMessage);
    }

    public void sendError(String userToken, String to, Throwable t) throws Exception {
        log.error("ERROR", t);
        if (DEBUG) {
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

    public void sendSelfMessage(String userId, String msg) {
        log.info(msg);
        if (DEBUG) {
            return;
        }
        FlockApiClient client = getClient(userStore.getToken(userId));
        Message message = new Message(userId, msg);
        message.setAppId(appId);
        message.setSendAs(new SendAs("PokerBot", ""));
//        message.setFrom(userId);
        FlockMessage flockMessage = new FlockMessage(message);
        try {
            client.chatSendMessage(flockMessage);
        } catch (Exception e) {
            log.error("Couldn't send self message for " + userId, e);
        }
    }
}
