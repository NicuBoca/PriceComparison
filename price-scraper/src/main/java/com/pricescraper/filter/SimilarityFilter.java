package com.pricescraper.filter;

import com.pricescraper.model.Product;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimilarityFilter {

    public static List<Product> getTheMostSimilarProducts(List<Product> products, String searchProduct) {
        List<Product> filteredProducts = new ArrayList<>();
        String[] arrOfSearchName = searchProduct.split("\\s+");
        arrOfSearchName = Arrays.stream(arrOfSearchName).distinct().toArray(String[]::new);
        int arrSearchNameLength = arrOfSearchName.length;
        double[] similarityRate = new double[arrSearchNameLength];
        String[] similarString = new String[arrSearchNameLength];
        int prodListLength = products.size();
        double[] avgSimilarity = new double[prodListLength];

        int counter = 0;
        for (Product product : products) {
            avgSimilarity[counter] = getAvgSearchNameMatching(product.getName(), arrOfSearchName, arrSearchNameLength, similarityRate, similarString);
            counter++;
        }
        double maxAvgSimilarity = Arrays.stream(avgSimilarity)
                .max()
                .getAsDouble();
        for (int i = 0; i < prodListLength; i++) {
            if (avgSimilarity[i] == maxAvgSimilarity) {
                filteredProducts.add(products.get(i));
            }
        }
        return filteredProducts;
    }

    public static String getSearchSuggestion(Product product, String searchProduct) {
        String[] arrOfSearchName = searchProduct.split("\\s+");
        arrOfSearchName = Arrays.stream(arrOfSearchName).distinct().toArray(String[]::new);
        int arrSearchNameLength = arrOfSearchName.length;
        double[] similarityRate = new double[arrSearchNameLength];
        String[] similarString = new String[arrSearchNameLength];
        String suggestion = "";

        double sim = getAvgSearchNameMatching(product.getName(), arrOfSearchName, arrSearchNameLength, similarityRate, similarString);
        if (sim != 1) {
            for (int i = 0; i < arrSearchNameLength; i++) {
                suggestion = suggestion + similarString[i] + " ";
            }
        }
        return suggestion;
    }

    private static double getAvgSearchNameMatching(String productName, String[] arrOfSearchName, int arrSearchNameLength, double[] similarityRate, String[] similarString) {
        JaccardSimilarity js = new JaccardSimilarity();
        productName = productName.toLowerCase();
        String[] arrOfProductName = productName.split("\\s+");
        double[] currentSimilarityRate = new double[arrSearchNameLength];

        for (int i = 0; i < arrSearchNameLength; i++) {
            for (String s : arrOfProductName) {
                double similarity = js.apply(arrOfSearchName[i], s);
                if (similarity > currentSimilarityRate[i]) {
                    currentSimilarityRate[i] = similarity;
                    if (currentSimilarityRate[i] > similarityRate[i]) {
                        similarityRate[i] = similarity;
                        similarString[i] = s;
                    }
                }
            }
        }

        double sum = 0;
        for (int k = 0; k < arrSearchNameLength; k++) {
            sum += currentSimilarityRate[k];
        }
        return sum / arrSearchNameLength;
    }

}
