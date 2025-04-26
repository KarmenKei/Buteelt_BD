package flashcard.app;

import java.util.List;
import java.util.Map;

import flashcard.Cards.Card;

public class AchievementTracker {
    private final List<Card> cards;
    private final Map<Card, Integer> correctTracker;
    private final Map<Card, Integer> wrongAttemptsTracker;
    private final Map<Card, Integer> correctAnswerStreak;

    public AchievementTracker(List<Card> cards,
                               Map<Card, Integer> correctTracker,
                               Map<Card, Integer> wrongAttemptsTracker,
                               Map<Card, Integer> correctAnswerStreak) {
        this.cards = cards;
        this.correctTracker = correctTracker;
        this.wrongAttemptsTracker = wrongAttemptsTracker;
        this.correctAnswerStreak = correctAnswerStreak;
    }

    public void printAchievements() {
        boolean allCorrect = cards.stream().allMatch(card -> correctTracker.get(card) > 0);
        boolean hasRepeated = cards.stream().anyMatch(card -> wrongAttemptsTracker.get(card) > 5);
        boolean confident = cards.stream().anyMatch(card -> correctAnswerStreak.get(card) >= 3);

        System.out.println("\nAchievements Unlocked:");
        
        if (allCorrect) {
            System.out.println(" CORRECT - All cards answered correctly in the final round.");
        }
        if (hasRepeated) {
            System.out.println(" REPEAT - Answered a card more than 5 times!");
        }
        if (confident) {
            System.out.println(" CONFIDENT - Answered a card correctly at least 3 times in a row!");
        }
    }
}