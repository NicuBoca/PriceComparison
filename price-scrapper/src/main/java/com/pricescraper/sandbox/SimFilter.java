package com.pricescraper.sandbox;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SimFilter {
    private static Set<String> excepwords;
    private static Set<String> stopwords;

    public static void productSim() {
        addExceptionsToSet();

        List<String> specialChars = Arrays.asList(",", "-", "\"", "'", "!", "(", ")", "{", "}", "[", "]", "^",
                "~", "*", "?", ":", "\\(o\\)", "\u00B0", "\\(C\\)", "\u00a9", "\\(R\\)", "\u00AE", "\\(TM\\)", "\u2122");

//        String prod1 = "Laptop ultraportabil ASUS ZenBook Pro Duo UX581GV cu procesor Intel® Core™ i9-9980HK pana la 5.00 GHz Coffee Lake, 15.6\", 4K, 32GB, 1TB SSD M.2, NVIDIA GeForce RTX 2060 6GB, Windows 10 Pro, Celestial Blue";
//        String prod2 = "Laptop ASUS ZenBook Pro Duo UX581GV-H2001R, Intel Core i9-9980HK pana la 5GHz, 15.6\" 4K UHD Touch, 32GB, SSD 1TB, NVIDIA GeForce RTX 2060 6GB, Windows 10 Pro, Celestial Blue";
//        String prod3 = "Ultrabook ASUS 15.6'' ZenBook Pro Duo UX581GV, UHD Touch, Procesor Intel® Core™ i7-9750H (12M Cache, up to 4.50 GHz), 16GB DDR4, 1TB SSD, GeForce RTX 2060 6GB, Win 10 Pro, Celestial Blue";

        String prod1 = "Televizor QLED Smart SAMSUNG 55Q60RA, Ultra HD 4K, HDR, 139 cm";
        String prod2 = "Televizor QLED Smart Samsung, 138 cm, 55Q60RA, 4K Ultra HD";
        String prod3 = "Samsung Smart TV QLED 55Q60RA Seria Q60R 138cm negru 4K UHD HDR";

        prod1 = prod1.toLowerCase();
        prod2 = prod2.toLowerCase();
        prod3 = prod3.toLowerCase();

        for (String ch : specialChars) {
            prod1 = prod1.replace(ch, " ");
            prod2 = prod2.replace(ch, " ");
            prod3 = prod3.replace(ch, " ");
        }

        System.out.println(prod1);
        System.out.println(prod2);
        System.out.println(prod3);

        String[] splitStr1 = prod1.split("\\s+");
        String[] splitStr2 = prod2.split("\\s+");
        String[] splitStr3 = prod3.split("\\s+");

        Set<String> setProd1 = new HashSet<>();
        Set<String> setProd2 = new HashSet<>();
        Set<String> setProd3 = new HashSet<>();

        for (String word : splitStr1) {
            if (!stopwords.contains(word) && !excepwords.contains(word)) {
                setProd1.add(word);
            }
        }
        for (String word : splitStr2) {
            if (!stopwords.contains(word) && !excepwords.contains(word)) {
                setProd2.add(word);
            }
        }
        for (String word : splitStr3) {
            if (!stopwords.contains(word) && !excepwords.contains(word)) {
                setProd3.add(word);
            }
        }

        System.out.println(setProd1.size());
        System.out.println(setProd1);
        System.out.println(setProd2.size());
        System.out.println(setProd2);
        System.out.println(setProd3.size());
        System.out.println(setProd3);

        System.out.println("------------");
        Set<String> commonWords2 = new HashSet<String>(setProd1);
        commonWords2.retainAll(setProd2);
        System.out.println("Cuvinte comune prod 1-2: " + commonWords2.size());
        System.out.println(commonWords2);
        double r2 = (double) commonWords2.size() / (double) setProd2.size();
        System.out.println(r2);

        System.out.println("------------");
        Set<String> commonWords3 = new HashSet<String>(setProd1);
        commonWords3.retainAll(setProd3);
        System.out.println("Cuvinte comune prod 1-3: " + commonWords3.size());
        System.out.println(commonWords3);
        double r3 = (double) commonWords3.size() / (double) setProd3.size();
        System.out.println(r3);

        Set<String> differentWords = new HashSet<String>(setProd1);
        for (String element : setProd3) {
            // .add() returns false if element already exists
            if (!differentWords.add(element)) {
                differentWords.remove(element);
            }
        }
        System.out.println("------------");
        System.out.println("Cuvinte diferite 1-3: " + differentWords.size());
        System.out.println(differentWords);

    }

    private static void addExceptionsToSet() {
        Set<String> myExcepwords = new HashSet<>();
        Set<String> myStopwords = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File("src/main/java/com/pricescraper/sandbox/excepwords.txt"));
            while (scanner.hasNextLine()) {
                myExcepwords.add(scanner.next());
            }

            scanner = new Scanner(new File("src/main/java/com/pricescraper/sandbox/stopwords.txt"));
            while (scanner.hasNextLine()) {
                myStopwords.add(scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        excepwords = myExcepwords;
        stopwords = myStopwords;
    }
}
