package com.pricescraper;

import com.pricescraper.filter.ProductMatching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PriceScraperApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PriceScraperApplication.class, args);

        //test
        //ProductMatching.isSameProductByName();
    }

}
