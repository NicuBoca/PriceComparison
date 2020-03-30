package com.pricescraper.filter;

import com.pricescraper.model.Product;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.contains;

public class ProductMatching {
    private static Set<String> exceptions;
    private static Set<String> stopwords;
    private static final List<String> specialChars = Arrays.asList(",", "-", "\"", "'", "!", "(", ")", "{", "}", "[", "]", "^",
            "~", "*", "?", ":", "\\(o\\)", "\u00B0", "\\(C\\)", "\u00a9", "\\(R\\)", "\u00AE", "\\(TM\\)", "\u2122");

    public static boolean isSameProductByName(Product p1, Product p2) {
        setStopwordsAndExceptions();
        double precision = 0.8;

//        String name1 = "Laptop ultraportabil ASUS ZenBook Pro Duo UX581GV cu procesor Intel® Core™ i9-9980HK pana la 5.00 GHz Coffee Lake, 15.6\", 4K, 32GB, 1TB SSD M.2, NVIDIA GeForce RTX 2060 6GB, Windows 10 Pro, Celestial Blue";
//        String name1 = "Laptop ASUS ZenBook Pro Duo UX581GV-H2001R, Intel Core i9-9980HK pana la 5GHz, 15.6\" 4K UHD Touch, 32GB, SSD 1TB, NVIDIA GeForce RTX 2060 6GB, Windows 10 Pro, Celestial Blue";
//        String name2 = "Ultrabook ASUS 15.6'' ZenBook Pro Duo UX581GV, UHD Touch, Procesor Intel® Core™ i7-9750H (12M Cache, up to 4.50 GHz), 16GB DDR4, 1TB SSD, GeForce RTX 2060 6GB, Win 10 Pro, Celestial Blue";

//        String name1 = "Televizor QLED Smart SAMSUNG 55Q60RA, Ultra HD 4K, HDR, 139 cm";
//        String name1 = "Televizor QLED Smart Samsung, 138 cm, 55Q60RA, 4K Ultra HD";
//        String name2 = "Samsung Smart TV QLED 55Q60RA Seria Q60R 138cm negru 4K UHD HDR";

//        String name1 = "Frigider cu o usa Bosch KSV36AI3P, 348 L , Clasa A++, FreshSense, Iluminare LED, H 186 cm, Inox";
//        String name1 = "Frigider cu o usa BOSCH KSV36AI3P, 346 l, H 186 cm, Clasa A++, inox";
//        String name2 = "Congelator Bosch GSN36AI3P, 242 l, 4 sertare, Clasa A++, No Frost, H 186 cm, Inox";

//        String name1 = "Aparat foto digital Sony Cyber-Shot DSC-RX100, 20.2MP, FullHD, Black";
//        String name2 = "Camera foto digitala SONY DSC-RX100, 20.2 MP, negru";
//        String name2 = "Camera foto digitala SONY Cyber-shot RX100 V, 20.1 MP, 4K, Wi-Fi, negru";

//        String name1 = "Uscatoare de par: Remington AC9096";
//        String name2 = "Uscator de par Remington AC9096, 2400 W, 3 Trepte temperatura, 2 Viteze, Turbo, Difuzor volum, Concentrator, Rosu";
//        String name2 = "Uscator de par REMINGTON Silk AC9096, 2400W, 6 viteze, 6 trepte temperatura, rosu-negru";

        if (p1.getSource() == p2.getSource()) {
            return false;
        } else {

            String name1 = p1.getName();
            String name2 = p2.getName();
            String prod1, prod2;
            if (name1.length() < name2.length()) {
                prod1 = name1.toLowerCase();
                prod2 = name2.toLowerCase();
            } else {
                prod1 = name2.toLowerCase();
                prod2 = name1.toLowerCase();
            }

            // daca numele produsului nu contine detalii tehnice (beta)
            if (prod1.length() < prod2.length() / 2) {
                int indexEnd = prod2.indexOf(",");
                if (indexEnd != -1) {
                    prod2 = prod2.substring(0, indexEnd);
                }
            }

            for (String ch : specialChars) {
                prod1 = prod1.replace(ch, " ");
                prod2 = prod2.replace(ch, " ");
            }

//        System.out.println(prod1);
//        System.out.println(prod2);

            String[] splitStr1 = prod1.split("\\s+");
            String[] splitStr2 = prod2.split("\\s+");

            Set<String> setProd1 = filterForStopwordsAndExceptions(splitStr1);
            Set<String> setProd2 = filterForStopwordsAndExceptions(splitStr2);

//        System.out.println(setProd1.size());
//        System.out.println(setProd1);
//        System.out.println(setProd2.size());
//        System.out.println(setProd2);

//        System.out.println("------------");
            Set<String> commonWords = new HashSet<String>(setProd1);
            commonWords.retainAll(setProd2);
//        System.out.println("Cuvinte comune prod 1-2: " + commonWords.size());
//        System.out.println(commonWords);

            double nrCommon = commonWords.size();
            double nrTotalRef = setProd1.size();
            double r2 = nrCommon / nrTotalRef;
//        System.out.println(r2);

            if (r2 >= precision) {
//                System.out.println("Match");
                return true;
            } else {
                return isSameProductByDifferentWords(precision, setProd1, setProd2, nrCommon, nrTotalRef);
            }
        }
    }

    private static boolean isSameProductByDifferentWords(double precision, Set<String> setProd1, Set<String> setProd2, double nrCommon, double nrTotalRef) {
        Set<String> differentWords = new HashSet<String>(setProd1);
        for (String element : setProd2) {
            if (!differentWords.add(element)) {
                differentWords.remove(element);
            }
        }
//        System.out.println("------------");
        int diffSize = differentWords.size();
//        System.out.println("Cuvinte diferite 1-2: " + diffSize);
//        System.out.println(differentWords);

        for (String c1 : differentWords) {
            for (String c2 : differentWords) {
                if (!c2.equals(c1) &&
                        c1.length() > 1 && c2.length() > 1 &&
                        contains(c2, c1)) {
                    nrCommon++;
                }
            }
        }

//        System.out.println("-->");
//        System.out.println("Cuvinte comune (2): " + nrCommon);
        double r3 = nrCommon / nrTotalRef;
//        System.out.println(r3);
        if (r3 >= precision) {
//            System.out.println("Match");
            return true;
        } else {
//            System.out.println("NU");
            return false;
        }
    }

    private static Set<String> filterForStopwordsAndExceptions(String[] splittedName) {
        Set<String> result = new HashSet<>();
        for (String word : splittedName) {
            if (!stopwords.contains(word) && !exceptions.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }

    private static boolean getIndexJaccard(String s1, String s2) {
        JaccardSimilarity js = new JaccardSimilarity();
        double indexJaccard = js.apply(s1, s2);
        double precisionJaccard = 0.75;
        return indexJaccard > precisionJaccard;
    }

    private static void setStopwordsAndExceptions() {
        Set<String> myExceptions = new HashSet<>();
        Set<String> myStopwords = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/filter/exceptions.txt"));
            while (scanner.hasNextLine()) {
                myExceptions.add(scanner.next());
            }
            scanner = new Scanner(new File("src/main/resources/filter/stopwords.txt"));
            while (scanner.hasNextLine()) {
                myStopwords.add(scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        exceptions = myExceptions;
        stopwords = myStopwords;
    }
}
