package flashcard.sorter;

import java.util.List;

import flashcard.Cards.Card;

public class WorstFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        cards.sort((a, b) -> Integer.compare(b.getIncorrectCount(), a.getIncorrectCount()));
        return cards;
    }
}