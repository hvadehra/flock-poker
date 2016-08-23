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

    private static final boolean PROD_ENV = false;
    private static final boolean DEBUG = false;
    private final Logger log = LoggerFactory.getLogger(FlockApiClientWrapper.class.getCanonicalName());
    private final UserStore userStore;
    private final String appId;
    private final String botGuid;
    private final FlockApiClient flockApiClient;

    @Inject
    public FlockApiClientWrapper(UserStore userStore,
                                 @Named("app.id") String appId,
                                 @Named("bot.id") String botGuid,
                                 @Named("bot.token") String botToken) {
        this.userStore = userStore;
        this.appId = appId;
        this.flockApiClient = new FlockApiClient(botToken, PROD_ENV);
        this.botGuid = botGuid;
    }

    public void sendMessage(String to, String msg) throws Exception {
        if (msg == null) return;
        log.info(msg);
        if (DEBUG) return;
        Message message = new Message(to, msg);
        message.setAppId(appId);
        message.setSendAs(new SendAs("PokerBot", ""));
        message.setFrom(botGuid);
        FlockMessage flockMessage = new FlockMessage(message);
        flockApiClient.chatSendMessage(flockMessage);
    }

    public void sendError(String to, Throwable t) throws Exception {
        log.error("ERROR", t);
        if (DEBUG) {
            return;
        }
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
