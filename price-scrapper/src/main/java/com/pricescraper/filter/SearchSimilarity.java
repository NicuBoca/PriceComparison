package com.pricescraper.filter;

import org.apache.commons.text.similarity.JaccardSimilarity;

public class SearchSimilarity {

    public static double getSimilarityBetweenSearchAndFoundName(String productSearchName, String productFoundName) {
        JaccardSimilarity js = new JaccardSimilarity();

        productSearchName = productSearchName.toLowerCase();
        String[] productSearchWords = productSearchName.split("\\s+");
        int noProductSearchWords = productSearchWords.length;

        productFoundName = productFoundName.toLowerCase();
        String[] productFoundWords = productFoundName.split("\\s+");
        int noProductFoundWords = productFoundWords.length;

        double productSearchWordsSimilarity[] = new double[noProductSearchWords];

        for (int i = 0; i < noProductSearchWords; i++) {
            for (int j = 0; j < noProductFoundWords; j++) {
                double currentSimilarity = js.apply(productSearchWords[i], productFoundWords[j]);
                if (currentSimilarity > productSearchWordsSimilarity[i]) {
                    productSearchWordsSimilarity[i] = currentSimilarity;
                }
            }
        }

        double sum = 0;
        for (int k = 0; k < noProductSearchWords; k++) {
            sum += productSearchWordsSimilarity[k];
        }
        double similarityCoefficient = sum / noProductSearchWords;
        return similarityCoefficient;
    }
}
