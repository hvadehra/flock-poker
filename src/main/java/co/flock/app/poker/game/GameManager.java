package co.flock.app.poker.game;

import co.flock.app.poker.FlockApiClientWrapper;
import co.flock.app.poker.UserStore;
import co.flock.www.model.PublicProfile;
import co.flock.www.model.flockevents.SlashCommand;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

import static co.flock.app.poker.game.Game.initPlayers;

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
                startGame(userId, userToken, gameId);
            } else if (command.equals("end")) {
                Game game = getGame(gameId);
                game.end(userId);
                games.remove(gameId);
            } else if (command.equals("call")) {
                getGame(gameId).call(userId);
            } else if (command.equals("quit")) {
                getGame(gameId).quit(userId);
            }
        } catch (Throwable t) {
            flockApiClient.sendError(userToken, gameId, t);
        }
    }

    private Game getGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new RuntimeException("No game in progress");
        }
        return game;
    }

    private void startGame(String userId, String userToken, String gameId) throws Exception {
        if (games.containsKey(gameId)) {
            throw new RuntimeException("Game already in progress");
        } else {
            PublicProfile[] groupMembers = flockApiClient.getGroupMembers(userToken, gameId);
            Game game = new Game(userId, userToken, gameId, initPlayers(groupMembers));
            games.put(gameId, game);
        }
    }
}
