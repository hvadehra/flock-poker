package co.flock.app.poker;

import co.flock.www.FlockApiClient;
import co.flock.www.model.PublicProfile;
import co.flock.www.model.messages.Attachments.Attachment;
import co.flock.www.model.messages.Attachments.HtmlView;
import co.flock.www.model.messages.Attachments.View;
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
    private final Logger log = LoggerFactory.getLogger(FlockApiClientWrapper.class.getCanonicalName());
    private final UserStore userStore;
    private final String appId;
    private final String botGuid;
    private final FlockApiClient flockApiClient;
    private final FlockApiClient backupClient = new FlockApiClient("a8012a8d-b1b0-4717-98e4-f7f6e2a7803c", PROD_ENV);

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
        sendMessage(to, msg, null);
    }

    public void sendMessage(String to, String msg, HtmlView html) throws Exception {
        if (msg == null) return;
        log.info("Sending to {} msg={} html={}", to, msg, html);
        Message message = new Message(to, msg);
        constructMsgAndSend(to, message, html);
    }

    public void sendError(String to, Throwable t) throws Exception {
        log.error("Sending ERROR to {}", to, t);
        Message message = new Message(to, "ERROR: \n" + t.getMessage());
        constructMsgAndSend(to, message, null);
    }

    private void constructMsgAndSend(String to, Message message, HtmlView html) throws Exception {
        FlockApiClient client;
//        if (to.startsWith("u")) {
            client = flockApiClient;
            message.setAppId(appId);
            message.setSendAs(new SendAs("PokerBot", ""));
            message.setFrom(botGuid);
//        } else {
//            client = backupClient;
//        }
        if (html != null) {
            Attachment[] attachments = new Attachment[1];
            attachments[0] = new Attachment();
            View views = new View();
            views.setHtml(html);
            attachments[0].setViews(views);
            message.setAttachments(attachments);
        }
        FlockMessage flockMessage = new FlockMessage(message);
        client.chatSendMessage(flockMessage);
    }

    public PublicProfile[] getGroupMembers(String userToken, String groupId) throws Exception {
        log.info("Fetching group members for {} with token {}", groupId, userToken);
        return getClient(userToken).getGroupMembers(groupId);
    }

    private FlockApiClient getClient(String userToken) {
        return new FlockApiClient(userToken, PROD_ENV);
    }
}
