package co.flock.app.poker.game;

import com.google.common.collect.Lists;

import java.util.Iterator;

/**
 * Created by hemanshu.v on 8/23/16.
 */
public class GameState {

    private final Iterator<State> states = Lists.newArrayList(State.values()).iterator();
    private State curState;

    public GameState() {
        this.curState = this.states.next();
    }

    public State next() {
        curState = states.next();
        return curState;
    }

    public State get() {
        return curState;
    }

    public enum State {
        PREFLOP, FLOP, TURN, RIVER, SHOWDOWN;
    }
}
