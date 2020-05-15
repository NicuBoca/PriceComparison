package com.pricescraper.filter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WordFilter {
    private Set<String> exceptions;
    private Set<String> stopwords;
    private final List<String> specialChars = Arrays.asList(",", "-", "\"", "'", "!", "(", ")", "{", "}", "[", "]", "^",
            "~", "*", "?", ":", "\\(o\\)", "\u00B0", "\\(C\\)", "\u00a9", "\\(R\\)", "\u00AE", "\\(TM\\)", "\u2122");

    public WordFilter() {
        Set<String> myExceptions = new HashSet<>();
        Set<String> myStopwords = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File("src/main/java/com/pricescraper/filter/files/exceptions.txt"));
            while (scanner.hasNextLine()) {
                myExceptions.add(scanner.next());
            }
            scanner = new Scanner(new File("src/main/java/com/pricescraper/filter/files/stopwords.txt"));
            while (scanner.hasNextLine()) {
                myStopwords.add(scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        exceptions = myExceptions;
        stopwords = myStopwords;
    }

    public Set<String> filterForStopwordsAndExceptions(String[] splittedName) {
        Set<String> result = new HashSet<>();
        for (String word : splittedName) {
            if (!stopwords.contains(word) && !exceptions.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }

    public String removeSpecialChars(String prodName) {
        for (String ch : specialChars) {
            prodName = prodName.replace(ch, " ");
        }
        return prodName;
    }
}
