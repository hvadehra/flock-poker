package co.flock.app.poker.game;

import co.flock.app.poker.FlockApiClientWrapper;
import co.flock.www.model.PublicProfile;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by hemanshu.v on 8/23/16.
 */
public class Game {
    private static final int STARTSTACK = 100;
    private static final int SMALLBLINDAMT = 1;
    private static final int BIGBLINGAMT = 2;


    private final FlockApiClientWrapper flockApiClient = new FlockApiClientWrapper();
    private final String creatorId;
    private final String creatorToken;
    private final String gameId;
    private final List<Player> players;
    private List<Card> communityCards;
    private GameState gameState;
    private Player dealer;
    private Player actionOn;
    private Player lastActor;
    private List<Player> playersInHand;
    private int currentBet;

    public Game(String creatorId, String creatorToken, String gameId, List<Player> players) throws Exception {
        this.creatorToken = creatorToken;
        this.creatorId = creatorId;
        this.gameId = gameId;
        this.players = players;
        this.playersInHand = Lists.newArrayList(players);
        this.gameState = new GameState();
        distributeCards();
        setupAction();
    }

    public static List<Player> initPlayers(PublicProfile[] groupMembers) {
        List<Player> players = Lists.newArrayList();
        for (PublicProfile groupMember : groupMembers) {
            players.add(new Player(groupMember));
        }
        return players;
    }

    public static void main(String[] args) throws Exception {
        List<Player> p = Lists.newArrayList(
                new Player("p1", "f1", "l1"),
                new Player("p2", "f2", "l2"),
                new Player("p3", "f3", "l3"),
                new Player("p4", "f4", "l4")
        );
        Game game = new Game("p1", "token", "1", p);

        game.call("p2");
        game.call("p3");
        game.call("p4");
        game.call("p1");
        game.call("p1");
        game.call("p1");
        game.call("p2");

    }

    private void distributeCards() {
        List<Card> shuffledDeck = Card.getShuffledDeck();
        for (Player player : players) {
            player.cards = Lists.newArrayList(
                    shuffledDeck.remove(0),
                    shuffledDeck.remove(0)
            );
        }
        communityCards = Lists.newArrayList(
                shuffledDeck.remove(0),
                shuffledDeck.remove(0),
                shuffledDeck.remove(0),
                shuffledDeck.remove(0),
                shuffledDeck.remove(0)
        );
    }

    private void printGameState() throws Exception {
        StringBuilder msg = new StringBuilder();
        msg
                .append(gameState.get())
                .append("\n").append(stacks())
                .append("\n").append(board())
                .append("\n\n").append("Action on ").append(actionOn);
        flockApiClient.sendMessage(creatorToken, creatorId, msg.toString());
    }

    private StringBuilder board() {
        StringBuilder board = new StringBuilder()
                .append("Board: ");
        for (Card card : communityCards) {
            board.append(" ").append(card);
        }
        return board;
    }

    private StringBuilder stacks() {
        StringBuilder stks = new StringBuilder()
                .append("Stacks");
        for (Player player : playersInHand) {
            stks.append("\n").append(player).append(": ").append(player.getStack());
        }
        return stks;
    }

    private void setupAction() throws Exception {
        StringBuilder handStartMsg = new StringBuilder();
        dealer = playersInHand.get(0);
        actionOn = dealer;
        handStartMsg = handStartMsg.append("\n").append(actionOn).append(" is the Dealer");
        moveAction(false);
        actionOn.call(1);
        handStartMsg = handStartMsg.append("\n").append(actionOn).append(" has posted the small blind");
        moveAction(true);
        actionOn.call(2);
        handStartMsg = handStartMsg.append("\n").append(actionOn).append(" has posted the big blind");
        moveAction(true);
        currentBet = BIGBLINGAMT;
        flockApiClient.sendMessage(creatorToken, gameId, handStartMsg.toString());
    }

    private void moveAction(boolean tookAction) throws Exception {
        if (tookAction)
            lastActor = actionOn;
        int i = (1 + playersInHand.indexOf(actionOn)) % playersInHand.size();
        Player next = playersInHand.get(i);
        while (next.state == PlayerState.ALLIN && next != lastActor) {
            next = playersInHand.get(i);
            i = (i + 1) % playersInHand.size();
        }
        if (next == lastActor) {
            nextGameState();
        } else {
            actionOn = next;
        }
        printGameState();
    }

    private Player nextGameState() {
        gameState.next();
        currentBet = BIGBLINGAMT;
        if (gameState.get().equals(GameState.State.SHOWDOWN)) {
//            showDown();
        } else if (gameState.get().equals(GameState.State.FLOP)) {
            communityCards.get(0).expose();
            communityCards.get(1).expose();
            communityCards.get(2).expose();
        } else if (gameState.get().equals(GameState.State.TURN)) {
            communityCards.get(3).expose();
        } else if (gameState.get().equals(GameState.State.RIVER)) {
            communityCards.get(4).expose();
        }
        actionOn = findFirstToAct();
        return actionOn;
    }

    private Player findFirstToAct() {
        int i = (1 + players.indexOf(dealer)) % players.size();
        Player smallBlind = playersInHand.get(i);
        Player firstToAct = smallBlind;
        while ((!playersInHand.contains(firstToAct))
                || firstToAct.state == PlayerState.ALLIN) {
            i = i + 1 % playersInHand.size();
            firstToAct = playersInHand.get(i);
            if (firstToAct == smallBlind) {
                return nextGameState();
            }
        }
        return firstToAct;
    }

    public void end(String userId) throws Exception {
        if (userId.equals(creatorId)) {
            flockApiClient.sendMessage(creatorToken, gameId, "game has been ended");
        }
        throw new RuntimeException("Only game creator can end the game. Use /poker quit if you wish to leave the game");
    }

    public String quit(String userId) {
        Player player = getPlayer(userId);
        if (player != null) {
            players.remove(player);
            playersInHand.remove(player);
            return player.fn + " " + player.ln + " has left the game.";
        }
        return null;
    }

    private Player getPlayer(String userId) {
        for (Player player : players) {
            if (userId.equals(player.id))
                return player;
        }
        return null;
    }

    public void call(String userId) throws Exception {
        Player player = getPlayer(userId);
        if (player != null && player == actionOn) {
            player.call(currentBet);
            flockApiClient.sendMessage(creatorToken, gameId, player + " called " + currentBet);
            moveAction(false);
        } else {
            flockApiClient.sendError(creatorToken, userId, new RuntimeException("Not your turn."));
        }
    }

    private enum PlayerState {
        INPLAY, ALLIN;

    }

    private static class Player {

        private final String id;
        private final String fn;
        private final String ln;
        private PlayerState state;
        private List<Card> cards;
        private int stack;
        private int lastBet;

        Player(String id, String fn, String ln) {
            this.id = id;
            this.fn = fn;
            this.ln = ln;
            this.state = PlayerState.INPLAY;
            this.stack = STARTSTACK;
            this.lastBet = 0;
        }

        Player(PublicProfile groupMember) {
            this(groupMember.getId(), groupMember.getFirstName(), groupMember.getLastName());
        }

        @Override
        public String toString() {
            return
//                    "[" + super.hashCode() + "] " +
                    cards + " " +
                            fn + " " + ln;
        }

        public int getStack() {
            return stack;
        }

        public void call(int currentBet) {
            stack = stack - (currentBet - lastBet);
            lastBet = currentBet;
        }
    }
}
