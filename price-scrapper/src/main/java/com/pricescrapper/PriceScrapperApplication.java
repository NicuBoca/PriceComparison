package com.pricescrapper;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.sandbox.ApacheCommonsText;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class PriceScrapperApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PriceScrapperApplication.class, args);

        //ApacheCommonsText.testSimilarity();

        Scanner scan = new Scanner(System.in);
        System.out.print("Introduceti produsul: ");
        String product= scan.nextLine();
        CrawlEngine engine = new CrawlEngine();
        engine.crawl(product);

    }

}
