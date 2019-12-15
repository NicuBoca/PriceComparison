package com.pricescrapper.sandbox;

import org.apache.commons.text.similarity.JaccardSimilarity;


public class ApacheCommonsText {
    public static void testSimilarity() {
        JaccardSimilarity js = new JaccardSimilarity();

        double c1 = js.apply("catuse", "cartuse");
        double c2 = js.apply("catuse", "uscator");
        //double c3 = js.apply("laptop", "potpal");
        double c4 = js.apply("asus zenbook", "Laptop Ultraportabil ASUS ZenBook Edition 30 UX334FL cu procesor Intel® Core™ i7".toLowerCase());
        System.out.println(c1);
        System.out.println(c2);
        //System.out.println(c3);
        System.out.println(c4);

        String str1 = "laptop asus zen";
        String str2 = "Laptop Ultraportabil ASUS ZenBook Edition 30 UX334FL cu procesor Intel® Core™ i7".toLowerCase();
        String[] arrOfStr1 = str1.split(" ");
        String[] arrOfStr2 = str2.split(" ");

        double sim[] = new double[arrOfStr1.length];
        String simStr[] = new String[arrOfStr1.length];
        int count[] = new int[arrOfStr1.length];

        for (int i=0; i<arrOfStr1.length; i++) {
            //System.out.println(arrOfStr1[i]);
            for (int j=0; j<arrOfStr2.length; j++) {
                //System.out.println(arrOfStr2[j]);
                double p = js.apply(arrOfStr1[i],arrOfStr2[j]);
                if(p>sim[i]) {
                    sim[i] = p;
                    simStr[i] = arrOfStr2[j];
                }
                if(p==1) {
                    sim[i] = p;
                    simStr[i] = arrOfStr2[j];
                    count[i]++;
                }
            }
        }

        for (int k=0; k<arrOfStr1.length; k++) {
            if(sim[k]==1){
                System.out.println(arrOfStr1[k] + " apare de " + count[k] + " ori");
            }
            else {
                System.out.println(arrOfStr1[k] + " are scor de " + sim[k]);
                System.out.println("similar pentru " + arrOfStr1[k] + ": " + simStr[k]);
            }
        }

    }
}
