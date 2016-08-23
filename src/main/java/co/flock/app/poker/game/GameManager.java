package co.flock.app.poker.game;

import co.flock.app.poker.FlockApiClientWrapper;
import co.flock.app.poker.UserStore;
import co.flock.www.model.flockevents.SlashCommand;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

/**
 * Created by hemanshu.v on 8/23/16.
 */
@Singleton
public class GameManager {

    private final UserStore userStore;
    private final FlockApiClientWrapper flockApiClient;
    private final Map<String, Game> games = Maps.newHashMap();

    @Inject
    public GameManager(UserStore userStore, FlockApiClientWrapper flockApiClient) {
        this.userStore = userStore;
        this.flockApiClient = flockApiClient;
    }

    public void command(SlashCommand slashCommand) throws Exception {
        String userId = slashCommand.getUserId();
        String userToken = userStore.getToken(userId);
        String gameId = slashCommand.getChat();
        String params = slashCommand.getText();

        String[] args = params.split(" ");
        String command = args[0].toLowerCase();
        try {
            if (command.equals("start")) {
                Game game = games.get(gameId);
                if (game != null) {
                    throw new RuntimeException("Game already started");
                } else {
                    games.put(gameId, new Game());
                    flockApiClient.sendMessage(userToken, gameId, "Game started");
                }
            } else if (command.equals("raise")) {

            }
        } catch (Throwable t) {
            flockApiClient.sendError(userToken, gameId, t);
        }
    }
}
