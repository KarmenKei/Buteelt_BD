package flashcard.sorter;

import java.util.Collections;
import java.util.List;

import flashcard.Cards.Card;

public class RandomSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }
}
