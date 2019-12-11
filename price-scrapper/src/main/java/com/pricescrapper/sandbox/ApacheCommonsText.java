package com.pricescrapper.sandbox;

import org.apache.commons.text.similarity.JaccardSimilarity;


public class ApacheCommonsText {
    public static void testSimilarity() {
        JaccardSimilarity js = new JaccardSimilarity();

        double c1 = js.apply("catuse", "cartuse");
        double c2 = js.apply("catuse", "uscator");
        double c3 = js.apply("laptop", "potpal");
        double c4 = js.apply("asus zenbook", "Laptop Ultraportabil ASUS ZenBook Edition 30 UX334FL cu procesor Intel® Core™ i7".toLowerCase());
        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);
        System.out.println(c4);

        String str1 = "laptop asus zen";
        String str2 = "Laptop Ultraportabil ASUS ZenBook Edition 30 UX334FL cu procesor Intel® Core™ i7".toLowerCase();
        String[] arrOfStr1 = str1.split(" ");
        String[] arrOfStr2 = str2.split(" ");

        double sim[] = new double[str1.length()];
        double coresp[] = new double[str2.length()];
        int count[] = new int[str1.length()];

        int i=0;
        for (String a : arrOfStr1) {
            //System.out.println(a);
            int j=0;
            for (String b : arrOfStr2) {
                //System.out.println(b);
                double p = js.apply(a,b);
                if(p>sim[i]) {
                    sim[i] = p;
                    coresp[j] = i;
                }
                if(p==1) {
                    sim[i] = p;
                    coresp[j] = i;
                    count[i]++;
                }
                j++;
            }
            i++;
        }
        for (int x=0; x<arrOfStr1.length; x++) {
            if(sim[x]==1){
                System.out.println(arrOfStr1[x] + " apare de " + count[x] + " ori");
            }
            else {
                System.out.println(arrOfStr1[x] + " are scor de " + sim[x]);
                System.out.print("similare pentru " + arrOfStr1[x] + ": ");
                for(int y=0; y<arrOfStr2.length; y++) {
                    if(coresp[y]==x) {
                        System.out.print(arrOfStr2[y] + " ");
                    }
                }
            }
        }

    }
}
