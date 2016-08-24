package co.flock.app.poker.game;

import ca.ualberta.cs.poker.Hand;
import ca.ualberta.cs.poker.HandEvaluator;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

import static java.util.Collections.shuffle;

/**
 * Created by hemanshu.v on 8/23/16.
 */
public class Card {
    private final Suit suit;
    private final Value number;
    private boolean exposed = false;
//    private boolean exposed = true;

    public Card(Suit suit, Value number) {
        this.suit = suit;
        this.number = number;
    }

    public static Hand getBestHand(List<Card> hole, List<Card> community) {
        List<Card> all = Lists.newArrayList();
        all.addAll(hole);
        all.addAll(community);

        String cards = Joiner.on(" -").join(Lists.transform(all,
                card -> new ca.ualberta.cs.poker.Card(card.number.numericVal, card.suit.numeric).toString()));
        HandEvaluator handEvaluator = new HandEvaluator();
        return handEvaluator.getBest5CardHand(new Hand(cards));
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

    @Override
    public String toString() {
        return exposed ? number.disp + suit.disp : "X";
    }

    public void expose() {
        this.exposed = true;
    }

    public String getImgName() {
        if (exposed) {
            String val = number.disp;
            if (number == Value.T) val = "10";
            if (number == Value.JACK ||
                    number == Value.QUEEN ||
                    number == Value.KING ||
                    number == Value.ACE) val = number.name();
            if (number == Value.T) val = "10";
            if (number == Value.T) val = "10";
            if (number == Value.T) val = "10";
            return val.toLowerCase() + "_of_" + suit.name().toLowerCase() + ".png";
        } else {
            return "face_down.png";
        }
    }

    private enum Suit {
        SPADES("s", 0), HEARTS("h", 1), DIAMONDS("d", 2), CLUBS("c", 3);

        private final String disp;
        public final int numeric;

        Suit(String disp, int numeric) {
            this.disp = disp;
            this.numeric = numeric;
        }

        @Override
        public String toString() {
            return disp;
        }

    }

    private enum Value {
        ACE("A", 12),
        KING("K", 11),
        QUEEN("Q", 10),
        JACK("J", 9),
        T("T", 8),
        Nine("9", 7),
        Eight("8", 6),
        Seven("7", 5),
        Six("6", 4),
        Five("5", 3),
        Four("4", 2),
        Three("3", 1),
        Two("2", 0);

        private final String disp;
        private final int numericVal;

        Value(String disp, int numericVal) {
            this.disp = disp;
            this.numericVal = numericVal;
        }

        @Override
        public String toString() {
            return disp;
        }
    }

}
