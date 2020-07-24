package com.pricescraper.service.impl;

import com.pricescraper.filter.SimilarityFilter;
import com.pricescraper.filter.WordFilter;
import com.pricescraper.model.Product;
import com.pricescraper.scrapper.*;
import com.pricescraper.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {

    private List<Product> productList;

    @Override
    public synchronized void addProducts(List<Product> products) {
        this.productList.addAll(products);
    }

    @Override
    public List<Product> getProductList(String searchProduct) {
        WordFilter wordFilter = new WordFilter();
        productList = Collections.synchronizedList(new ArrayList<>());
        searchProduct = wordFilter.removeSpecialChars(searchProduct);
        List<BaseScraper> crawlJobs = initCrawler(searchProduct);
        ExecutorService executor = Executors.newFixedThreadPool(crawlJobs.size());
        for (BaseScraper scrapper : crawlJobs) {
            executor.execute(scrapper);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        log.info("Crawl finished!");
        log.info("No. products (before similarity filter): " + productList.size());
        if (this.productList.size() > 0) {
            List<Product> filteredProducts = SimilarityFilter.getTheMostSimilarProducts(productList, searchProduct);
            log.info("No. products (after similarity filter): " + filteredProducts.size());
            return filteredProducts;
        }
        return null;
    }

    private List<BaseScraper> initCrawler(String product) {
        List<BaseScraper> crawlJobs = new ArrayList<>();
        crawlJobs.add(new EmagScraper(product, this));
        crawlJobs.add(new PcGarageScraper(product, this));
        crawlJobs.add(new AltexScraper(product, this));
        crawlJobs.add(new MediaGalaxyScraper(product, this));
        return crawlJobs;
    }

}
