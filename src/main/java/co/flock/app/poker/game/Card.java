package co.flock.app.poker.game;

import com.google.common.collect.Lists;

import java.util.List;

import static java.util.Collections.shuffle;

/**
 * Created by hemanshu.v on 8/23/16.
 */
public class Card {
    private final Suit suit;
    private final Value number;
    //    private boolean exposed = false;
    private boolean exposed = true;

    public Card(Suit suit, Value number) {
        this.suit = suit;
        this.number = number;
    }

    @Override
    public String toString() {
        return exposed ? number.disp + suit.disp : "X";
    }

    public void expose() {
        this.exposed = true;
    }

    private enum Suit {
        SPADE("s"), HEART("h"), DIAMOND("d"), CLUB("c");

        private final String disp;

        Suit(String disp) {

            this.disp = disp;
        }

        @Override
        public String toString() {
            return disp;
        }

    }

    private enum Value {
        A("A"),
        K("K"), Q("Q"), J("J"), T("T"),
        Nine("9"),
        Eight("8"),
        Seven("7"),
        Six("6"),
        Five("5"),
        Four("4"),
        Three("3"), Two("2"), One("1");

        private final String disp;

        Value(String disp) {
            this.disp = disp;
        }

        @Override
        public String toString() {
            return disp;
        }

    }

    public static List<Card> getShuffledDeck() {
        List<Card> cards = Lists.newArrayList();
        for (Value value : Value.values()) {
            for (Suit suit : Suit.values()) {
                cards.add(new Card(suit, value));
            }
        }
        shuffle(cards);
        return cards;
    }


}
