package districts;

import districts.exceptions.DistrictLoadingException;

import java.util.*;
import java.util.stream.Collectors;

public class DistrictsFuzzyFinder {
    // For tests purpose only:
    public static void main(String[] args) throws DistrictLoadingException {
        DistrictsRepositoryLoader loader = new DistrictsRepositoryLoader("/data/districts/districts_paths.txt");
        loader.load();
        DistrictsRepository repo = new DistrictsRepository(loader);
        DistrictsFuzzyFinder finder = new DistrictsFuzzyFinder(repo);
        System.out.println("editDistance(\"Bajana\", \"Bajanqe\") = " + finder.editDistance("Bajana", "Bajanqe"));
        System.out.println("distance = " + finder.distance("Jerzego Bajana","Bajanqe"));
        System.out.println(finder.getBestMatches("Hallerq", 31));
    }
    
    public DistrictsFuzzyFinder(DistrictsRepository repository) {
        this.repository = repository.getRepositoryMap();
    }

    public List<String> getBestMatches(String street, int number) {
        return repository.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> distance(entry.getKey(), street)))
                .filter(listEntry -> listEntry.getValue().stream()
                        .anyMatch(districtEntry -> districtEntry.containsNumber(number)))
                .filter(listEntry -> distance(listEntry.getKey(), street) <= DISTANCE_THRESHOLD)
                .limit(MATCHES_LIMIT)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    private int distance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        ArrayList<String> wordsOfS1 = new ArrayList<String>(Arrays.asList(s1.split("\\s|-")));
        ArrayList<String> wordsOfS2 = new ArrayList<String>(Arrays.asList(s2.split("\\s|-")));
        if(wordsOfS1.size() < wordsOfS2.size())
            return calculateDistance(wordsOfS1, wordsOfS2);
        else
            return calculateDistance(wordsOfS2, wordsOfS1);
    }

    private int calculateDistance(ArrayList<String> words1, ArrayList<String> words2) {
        if(words1.isEmpty())
            return words2.size() * REMAINING_WORD_COST;
        if(words2.isEmpty())
            return words1.size() * REMAINING_WORD_COST;

        int currentResult = INF;
        for(int i=0; i < words2.size(); i++) {
            ArrayList<String> newWords1 = new ArrayList<String>(words1.subList(1, words1.size()));
            String word1 = words1.get(0);
            String word2 = words2.get(i);
            words2.remove(i);
            int currentDistance = editDistance(word1, word2) + calculateDistance(newWords1, words2);
            currentResult = Math.min(currentResult, currentDistance);
            words2.add(i, word2);
        }
        return currentResult;
    }
    
    private int editDistance(String w1, String w2) {
        // Levenshtein distance
        int n = w1.length();
        int m = w2.length();
        int[][] dp = new int[n+1][m+1];
        for (int i = 0; i <= n; i++)
            dp[i][0] = i * DELETION_COST;
        for (int j = 0; j <= m; j++)
            dp[0][j] = j * INSERTION_COST;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if(w1.charAt(i - 1) == w2.charAt(j - 1))
                    dp[i][j] = dp[i-1][j-1];
                else
                    dp[i][j] = min(dp[i][j-1] + INSERTION_COST,
                            dp[i-1][j] + DELETION_COST,
                            dp[i-1][j-1] + SUBSTITUTION_COST);
            }
        }
        return dp[n][m];
    }

    private int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private Map<String, List<DistrictEntry>> repository;

    // constants for computing edit distance
    private final static int DELETION_COST = 2;
    private final static int INSERTION_COST = 2;
    private final static int SUBSTITUTION_COST = 2;
    private final static int REMAINING_WORD_COST = 1;
    private final static int INF = 10000000;
    private final static int DISTANCE_THRESHOLD = 10;
    private final static int MATCHES_LIMIT = 3;
}
