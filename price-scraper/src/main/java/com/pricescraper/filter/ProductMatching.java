package com.pricescraper.filter;

import com.pricescraper.model.Product;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.contains;

public class ProductMatching {

    private final static double precision = 0.8;

    public static double getSimilarityForProductMatching(Product p1, Product p2) {
        WordFilter wordFilter = new WordFilter();
        String prodName1, prodName2;

        if (p1.getName().length() < p2.getName().length()) {
            prodName1 = p1.getName().toLowerCase();
            prodName2 = p2.getName().toLowerCase();
        } else {
            prodName1 = p2.getName().toLowerCase();
            prodName2 = p1.getName().toLowerCase();
        }

        // daca numele produsului nu contine detalii tehnice (beta)
        if (prodName1.length() < prodName2.length() / 2) {
            int indexEnd = prodName2.indexOf(",");
            if (indexEnd != -1) {
                prodName2 = prodName2.substring(0, indexEnd);
                if(prodName2.length() < prodName1.length()) {
                    String temp = prodName1;
                    prodName1 = prodName2;
                    prodName2 = temp;
                }
            }
        }

        prodName1 = wordFilter.removeSpecialChars(prodName1);
        prodName2 = wordFilter.removeSpecialChars(prodName2);

        String[] splitStr1 = prodName1.split("\\s+");
        String[] splitStr2 = prodName2.split("\\s+");

        Set<String> setProd1 = wordFilter.filterForStopwordsAndExceptions(splitStr1);
        Set<String> setProd2 = wordFilter.filterForStopwordsAndExceptions(splitStr2);

        Set<String> commonWords = new HashSet<>(setProd1);
        commonWords.retainAll(setProd2);

        double nrCommon = commonWords.size();
        double nrTotalRef = setProd1.size();
        double r2 = nrCommon / nrTotalRef;

        if (r2 >= precision) {
            return r2;
        }
        return getSimilarityByDifferentWords(precision, setProd1, setProd2, nrCommon, nrTotalRef);

    }

    private static double getSimilarityByDifferentWords(double precision, Set<String> setProd1, Set<String> setProd2, double nrCommon, double nrTotalRef) {

        Set<String> differentWords1 = new HashSet<>(setProd1);
        for (String element : setProd2) {
            differentWords1.remove(element);
        }
        Set<String> differentWords2 = new HashSet<>(setProd2);
        for (String element : setProd1) {
            differentWords2.remove(element);
        }

        for (String c1 : differentWords1) {
            for (String c2 : differentWords2) {
                if (!c2.equals(c1) &&
                        c1.length() > 1 && c2.length() > 1 &&
                        contains(c2, c1)) {
                    nrCommon++;
                }
            }
        }

        double r3 = nrCommon / nrTotalRef;
        if (r3 >= precision) {
            return r3;
        }
        return 0;
    }

}
