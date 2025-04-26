package flashcard.session;

import java.io.*;
import java.util.*;

import flashcard.Cards.Card;
import flashcard.app.AchievementTracker;
import flashcard.cli.CommandLineParser;
import flashcard.sorter.CardOrganizer;
import flashcard.sorter.RandomSorter;
import flashcard.sorter.RecentMistakesFirstSorter;
import flashcard.sorter.WorstFirstSorter;

public class SessionManager {
    private final List<Card> cards;
    private final int repetitions;
    private final String order;
    private final Scanner scanner = new Scanner(System.in);
    private static final String STATS_FILE = "card_stats.ser";

    public SessionManager(CommandLineParser parser) throws Exception {
        this.cards = loadCardsFromFile(parser.getCardsFile(), parser.isInvertCards());
        this.repetitions = parser.getRepetitions();
        this.order = parser.getOrder();
        loadStats();
    }

    private List<Card> loadCardsFromFile(String fileName, boolean invertCards) throws Exception {
        List<Card> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("::");
                if (parts.length != 2) continue;
                String q = invertCards ? parts[1] : parts[0];
                String a = invertCards ? parts[0] : parts[1];
                result.add(new Card(q, a));
            }
        }
        return result;
    }

    private void loadStats() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STATS_FILE))) {
            @SuppressWarnings("unchecked") 
            List<Card> savedStats = (List<Card>) ois.readObject();
            
            for (Card loaded : savedStats) {
                for (Card c : cards) {
                    if (c.equals(loaded)) {
                        c.mergeStatsFrom(loaded);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void saveStats() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATS_FILE))) {
            oos.writeObject(cards);
        } catch (Exception ignored) {}
    }

    public void startSession() {
        Map<Card, Integer> correctTracker = new HashMap<>();
        Map<Card, Integer> wrongAttemptsTracker = new HashMap<>();
        Map<Card, Integer> correctAnswerStreak = new HashMap<>();
        List<Long> answerTimes = new ArrayList<>();

        for (Card card : cards) {
            correctTracker.put(card, 0);
            wrongAttemptsTracker.put(card, 0);
            correctAnswerStreak.put(card, 0);
        }

        System.out.println("\n Flashcard session has begun! Type 'exit' to stop.\n");

        boolean allComplete;
        do {
            allComplete = true;

            CardOrganizer organizer = getOrganizer(order);
            List<Card> orderedCards = organizer.organize(new ArrayList<>(cards));

            for (Card card : orderedCards) {
                if (correctTracker.get(card) >= repetitions) continue;

                System.out.println("Q: " + card.getQuestion());
                System.out.print("Your answer: ");

                long startTime = System.currentTimeMillis();
                String input = scanner.nextLine().trim();
                long duration = System.currentTimeMillis() - startTime;
                answerTimes.add(duration);

                if (input.equalsIgnoreCase("exit")) {
                    saveStats();
                    return;
                }

                boolean correct = input.equalsIgnoreCase(card.getAnswer());
                card.recordAnswer(correct);

                if (correct) {
                    System.out.println(" Correct!");
                    correctTracker.put(card, correctTracker.get(card) + 1);
                    correctAnswerStreak.put(card, correctAnswerStreak.get(card) + 1);
                } else {
                    System.out.println(" Wrong! Correct answer was: " + card.getAnswer());
                    wrongAttemptsTracker.put(card, wrongAttemptsTracker.get(card) + 1);
                    correctAnswerStreak.put(card, 0);
                }

                if (correctTracker.get(card) < repetitions) allComplete = false;
                System.out.println();
            }

            System.out.println(" Session progress:");
            for (Card card : cards) {
                System.out.println("  - " + card.getQuestion() +
                        " | Mistakes: " + card.getIncorrectCount()); 
            }

        } while (!allComplete);

        System.out.println("All cards answered correctly " + repetitions + " time(s), good job!");
        new AchievementTracker(cards, correctTracker, wrongAttemptsTracker, correctAnswerStreak).printAchievements();

        saveStats();
        System.out.println("Exiting...");
    }

    private CardOrganizer getOrganizer(String order) {
        return switch (order) {
            case "worst-first" -> new WorstFirstSorter();
            case "recent-mistakes-first" -> new RecentMistakesFirstSorter();
            default -> new RandomSorter();
        };
    }
}