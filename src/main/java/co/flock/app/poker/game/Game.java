package co.flock.app.poker.game;

import ca.ualberta.cs.poker.Hand;
import ca.ualberta.cs.poker.HandEvaluator;
import co.flock.app.poker.FlockApiClientWrapper;
import co.flock.www.model.PublicProfile;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static co.flock.app.poker.game.Card.getBestHand;

/**
 * Created by hemanshu.v on 8/23/16.
 */
public class Game {
    private static final int STARTSTACK = 100;
    private static final int SMALLBLINDAMT = 1;
    private static final int BIGBLINGAMT = 2;


    private final Logger log = LoggerFactory.getLogger(Game.class.getCanonicalName());

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
    private int pot;

    public Game(String creatorId, String creatorToken, String gameId, List<Player> players) throws Exception {
        this.creatorToken = creatorToken;
        this.creatorId = creatorId;
        this.gameId = gameId;
        this.players = players;
        nextHand();
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
        game.call("p3");
        game.call("p2");
        game.check("p2");
        game.check("p2");
        game.check("p3");
        game.raise("p4", 5);
        game.raise("p1", 2);
        game.raise("p2", 2);
        game.check("p3");
        game.call("p3");
        game.call("p4");
        game.call("p1");
        game.raise("p2", 10);
        game.call("p3");
        game.call("p4");
        game.call("p1");
        game.check("p2");
        game.check("p3");
        game.check("p4");
        game.check("p1");
    }

    private void fold(String userId) throws Exception {
        Player player = getPlayer(userId);
        if (player != null && player == actionOn) {
            flockApiClient.sendMessage(creatorToken, gameId, player + " folded.");
            moveAction(false);
            if (player == lastActor) {
                lastActor = actionOn;
            }
            pot += player.lastBet;
            player.lastBet = 0;
            playersInHand.remove(player);
        } else {
            flockApiClient.sendError(creatorToken, userId, new RuntimeException("Not your turn."));
        }
    }

    public void raise(String userId, int raise) throws Exception {
        Player player = getPlayer(userId);
        if (player != null && player == actionOn) {
            int raiseAmount = player.raise(currentBet, raise);
            flockApiClient.sendMessage(creatorToken, gameId, player + " raised to " + raiseAmount);
            currentBet = raiseAmount;
            moveAction(true);
        } else {
            flockApiClient.sendError(creatorToken, userId, new RuntimeException("Not your turn."));
        }
    }

    public void check(String userId) throws Exception {
        Player player = getPlayer(userId);
        if (player != null && player == actionOn) {
            if (currentBet == 0) {
                flockApiClient.sendMessage(creatorToken, gameId, player + " checked.");
                moveAction(false);
            } else {
                flockApiClient.sendMessage(creatorToken, player.id, "There is a bet in play. You can only call/raise/fold.");
            }
        } else {
            flockApiClient.sendError(creatorToken, userId, new RuntimeException("Not your turn."));
        }
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
                .append("\n").append("Pot: ").append(pot)
                .append("\n").append("Current bet ").append(currentBet)
                .append("\n\n").append("Action on ").append(actionOn)
                .append(" (").append(currentBet - actionOn.lastBet).append(" to call)");
        ;
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

    private void setupAction(int from) throws Exception {
        StringBuilder handStartMsg = new StringBuilder();
        dealer = playersInHand.get(from);
        pot = 0;
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
        log.debug("Last actor {}", lastActor);
        log.debug("Moving action from {}", actionOn);
        if (tookAction)
            lastActor = actionOn;
        log.debug("Updated last actor {}", lastActor);
        int i = (1 + playersInHand.indexOf(actionOn)) % playersInHand.size();
        Player next = playersInHand.get(i);
        log.debug("Potential next: {}", next);
        while (next.state == PlayerState.ALLIN && next != lastActor) {
            next = playersInHand.get(i);
            log.debug("Potential next: {}", next);
            i = (i + 1) % playersInHand.size();
        }
        log.debug("Next: {}", next);
        if (next == lastActor) {
            nextGameState();
        } else {
            actionOn = next;
        }
        printGameState();
    }

    private Player nextGameState() throws Exception {
        log.debug("Next game state");
        pot += addBetsToPot();
        gameState.next();
        currentBet = 0;
        if (gameState.get().equals(GameState.State.SHOWDOWN)) {
            showDown();
            nextHand();
            return null;
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
        log.debug("Action on: {}", actionOn);
        lastActor = actionOn;
        return actionOn;
    }

    private void nextHand() throws Exception {
        this.playersInHand = Lists.newArrayList(players);
        for (Player player : playersInHand) {
            player.lastBet = 0;
        }
        this.gameState = new GameState();
        distributeCards();
        int from = dealer == null ? 0 : (players.indexOf(dealer) + 1) % players.size();
        setupAction(from);
    }

    private void showDown() throws Exception {
        StringBuilder showDownMsg = new StringBuilder();
        showDownMsg.append("Showdown");
        for (Player player : playersInHand) {
            showDownMsg.append("\n").append(player).append(": ").append(player.cards);
        }
        Player winner = playersInHand.stream().max((p1, p2) -> {
            Hand h1 = getBestHand(p1.cards, communityCards);
            Hand h2 = getBestHand(p2.cards, communityCards);
            return new HandEvaluator().compareHands(h1, h2);
        }).get();
        winner.stack += pot;
        Hand bestHand = getBestHand(winner.cards, communityCards);
        String nameHand = HandEvaluator.nameHand(bestHand);
        showDownMsg.append("\n\n").append(winner).append(" wins with ")
                .append(bestHand).append(" (").append(nameHand).append(")");
        flockApiClient.sendMessage(creatorToken, gameId, showDownMsg.toString());
    }

    private int addBetsToPot() {
        int total = 0;
        for (Player player : playersInHand) {
            total += player.lastBet;
            player.lastBet = 0;
        }
        return total;
    }

    private Player findFirstToAct() throws Exception {
        int i = (1 + players.indexOf(dealer)) % players.size();
        Player smallBlind = players.get(i);
        Player firstToAct = smallBlind;
        while (!playersInHand.contains(firstToAct)) {
            i = (i + 1) % players.size();
            firstToAct = players.get(i);
        }
        i = playersInHand.indexOf(firstToAct);
        while (firstToAct.state == PlayerState.ALLIN && firstToAct != dealer) {
            i = (i + 1) % playersInHand.size();
        }
        if (firstToAct == dealer)
            return nextGameState();
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
            if (currentBet > 0) {
                int callAmount = player.call(currentBet);
                flockApiClient.sendMessage(creatorToken, gameId, player + " called " + callAmount);
                moveAction(false);
            } else {
                flockApiClient.sendMessage(creatorToken, player.id, "No bets place, you can only check/raise/fold.");
            }
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
//                    cards + " " +
                            fn + " " + ln;
        }

        public int getStack() {
            return stack;
        }

        public int call(int currentBet) {
            int callAmount = currentBet - lastBet;
            stack = stack - callAmount;
            lastBet = currentBet;
            return callAmount;
        }

        public int raise(int currentBet, int raise) {
            int raiseAmount = currentBet + raise - lastBet;
            stack = stack - raiseAmount;
            lastBet = raiseAmount;
            return raiseAmount;
        }

        public void exposeCards() {
            this.cards.stream().forEach(Card::expose);
        }
    }

}
