package com.pricescraper.filter;

import com.pricescraper.model.Product;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.contains;

public class ProductMatching {

    public static double getSimilarityForProductMatching(Product p1, Product p2) {
        WordFilter wordFilter = new WordFilter();
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

//        System.out.println(prodName1);
//        System.out.println(prodName2);

        String[] splitStr1 = prodName1.split("\\s+");
        String[] splitStr2 = prodName2.split("\\s+");

        Set<String> setProd1 = wordFilter.filterForStopwordsAndExceptions(splitStr1);
        Set<String> setProd2 = wordFilter.filterForStopwordsAndExceptions(splitStr2);

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
//            System.out.println("Match");
            return r2;
        } else {
            return getSimilarityByDifferentWords(precision, setProd1, setProd2, nrCommon, nrTotalRef);
        }

    }

    private static double getSimilarityByDifferentWords(double precision, Set<String> setProd1, Set<String> setProd2, double nrCommon, double nrTotalRef) {

        Set<String> differentWords1 = new HashSet<String>(setProd1);
        for (String element : setProd2) {
            differentWords1.remove(element);
        }
        Set<String> differentWords2 = new HashSet<String>(setProd2);
        for (String element : setProd1) {
            differentWords2.remove(element);
        }

//        System.out.println("------------");
//        System.out.println("Cuvinte diferite 1: " + differentWords1);
//        System.out.println(differentWords);
//        System.out.println("------------");
//        System.out.println("Cuvinte diferite 2: " + differentWords2);
//        System.out.println(differentWords);

        for (String c1 : differentWords1) {
            for (String c2 : differentWords2) {
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
            return r3;
        } else {
//            System.out.println("NU");
            return 0;
        }
    }

}
