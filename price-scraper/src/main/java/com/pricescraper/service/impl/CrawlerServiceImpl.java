package com.pricescraper.service.impl;

import com.pricescraper.filter.SimilarityFilter;
import com.pricescraper.model.Product;
import com.pricescraper.scrapper.*;
import com.pricescraper.service.CrawlerService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Log
public class CrawlerServiceImpl implements CrawlerService {

    List<Product> productList = new ArrayList<Product>();

    @Override
    public synchronized void addProducts(List<Product> products) {
        this.productList.addAll(products);
    }

    @Override
    public List<Product> getProductList(String searchProduct) {
        List<BaseScraper> crawlJobs = initCrawler(searchProduct);
        ExecutorService executor = Executors.newFixedThreadPool(crawlJobs.size());
        for (BaseScraper scrapper : crawlJobs) {
            executor.execute(scrapper);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        log.info("Crawl finished!");
        if (this.productList.size() > 0) {
            List<Product> productListFiltered = SimilarityFilter.getTheMostSimilarProducts(productList, searchProduct);
            return productListFiltered;
        } else {
            return null;
        }
    }

    private List<BaseScraper> initCrawler(String product) {
        List<BaseScraper> crawlJobs = new ArrayList<BaseScraper>();
        crawlJobs.add(new EmagScraper(product, this));
        crawlJobs.add(new PcGarageScraper(product, this));
        crawlJobs.add(new AltexScraper(product, this));
        crawlJobs.add(new MediaGalaxyScraper(product, this));
        return crawlJobs;
    }

}
