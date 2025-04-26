package flashcard.sorter;

import java.util.List;

import flashcard.Cards.Card;

public class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        cards.sort((a, b) -> Long.compare(b.getLastMistakeTime(), a.getLastMistakeTime()));
        return cards;
    }
}