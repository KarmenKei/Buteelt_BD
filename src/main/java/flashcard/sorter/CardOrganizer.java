package flashcard.sorter;

import java.util.List;

import flashcard.Cards.Card;

public interface CardOrganizer {
    List<Card> organize(List<Card> cards);
}
