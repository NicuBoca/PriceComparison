package com.pricescrapper;

import com.pricescrapper.crawler.CrawlEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class PriceScrapperApplication {
//
//    @Autowired
//    private ProductRepository repository;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PriceScrapperApplication.class, args);

        //ApacheCommonsText.testSimilarity();

        Scanner scan = new Scanner(System.in);
        System.out.print("Introduceti produsul: ");
        String product = scan.nextLine();
        CrawlEngine engine = new CrawlEngine();
        engine.crawl(product);


    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        repository.deleteAll();
//
//        ProductDTO currentProduct = ProductDTO.builder()
//                .name("prodName")
//                .price((float) 12.2)
//                .stock(1)
//                .url("prodUrl")
//                .source(ProductSourceType.EMAG)
//                .img("prodImg")
//                .similarity(0.8)
//                .build();
//        repository.save(currentProduct);
//
//        // fetch all customers
//        System.out.println("Customers found with findAll():");
//        System.out.println("-------------------------------");
//        for (ProductDTO customer : repository.findAll()) {
//            System.out.println(customer);
//        }
//        System.out.println();
//    }
}
