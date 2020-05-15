package com.pricescraper.filter;

import com.pricescraper.model.Product;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SimilarityFilter {

    public static List<Product> getTheMostSimilarProducts(List<Product> products, String searchProduct) {
        WordFilter wordFilter = new WordFilter();
        List<Product> filteredProducts = new ArrayList<Product>();

        String[] arrOfSearchName = searchProduct.split(" ");
        Set<String> setOfSearchName = wordFilter.filterForStopwordsAndExceptions(arrOfSearchName);
        int arrSearchNameLength = arrOfSearchName.length;
        double[] similarityRate = new double[arrSearchNameLength];
        int prodListLength = products.size();
        double[] avgSimilarity = new double[prodListLength];

        int counter = 0;
        for (Product product : products) {
            avgSimilarity[counter] = getAvgSearchNameMatching(product.getName(), setOfSearchName, similarityRate);
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

    private static double getAvgSearchNameMatching(String productName, Set<String> setOfSearchName, double[] similarityRate) {
        JaccardSimilarity js = new JaccardSimilarity();
        productName = productName.toLowerCase();
        String[] arrOfProductName = productName.split(" ");
        List<String> listOfSearchName = new ArrayList<>(setOfSearchName);
        int listSearchNameLength = listOfSearchName.size();
        double[] currentSimilarityRate = new double[listSearchNameLength];

        for (int i = 0; i < listSearchNameLength; i++) {
            for (String s : arrOfProductName) {
                double similarity = js.apply(listOfSearchName.get(i), s);
                if (similarity > currentSimilarityRate[i]) {
                    currentSimilarityRate[i] = similarity;
                    if (currentSimilarityRate[i] > similarityRate[i]) {
                        similarityRate[i] = similarity;
                    }
                }
            }
        }

        double sum = 0;
        for (int k = 0; k < listSearchNameLength; k++) {
            sum += currentSimilarityRate[k];
        }
        return sum / listSearchNameLength;
    }

}
