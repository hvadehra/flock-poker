package co.flock.app.poker;

import co.flock.app.poker.game.GameManager;
import co.flock.www.FlockEventsHandler;
import co.flock.www.model.flockevents.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hemanshu.v on 8/23/16.
 */
@Singleton
public class FlockPokerEventHandler implements FlockEventsHandler {

    private final Logger log = LoggerFactory.getLogger(FlockPokerEventHandler.class.getCanonicalName());

    private final UserStore userStore;
    private final GameManager gameManager;

    @Inject
    public FlockPokerEventHandler(UserStore userStore, GameManager gameManager) {
        this.userStore = userStore;
        this.gameManager = gameManager;
    }

    public void onAppInstall(AppInstall appInstall) {
        log.info("Got app install for {} with token {}", appInstall.getUserId(), appInstall.getUserToken());
        userStore.registerUser(appInstall.getUserId(), appInstall.getUserToken());
    }

    public void onAppUnInstall(AppUnInstall appUnInstall) {

    }

    public void onChatMessageReceived(ChatReceiveMessage chatReceiveMessage) {

    }

    public void onUnfurlUrl(UnfurlUrl unfurlUrl) {

    }

    public void onFlockMLAction(FlockMLAction flockMLAction) {

    }

    public void onOpenAttachmentWidget(OpenAttachmentWidget openAttachmentWidget) {

    }

    public void onPressButton(PressButton pressButton) {

    }

    public void onSlashCommand(SlashCommand slashCommand) {
        log.debug("Got slash command: {} {} from {} {}", slashCommand.getCommand(), slashCommand.getText(),
                slashCommand.getUserId(), slashCommand.getUserName());
        String command = slashCommand.getCommand();
        if (!command.equalsIgnoreCase("poker2"))
            throw new RuntimeException("Invalid command");
        try {
            gameManager.command(slashCommand);
        } catch (Throwable t) {
            log.error("Could not handle slash command", t);
        }
    }

    public void onWidgetAction(WidgetAction widgetAction) {

    }

    public void onGroupUpdated(GroupUpdated groupUpdated) {

    }
}
