package com.pricescraper.service.impl;

import com.pricescraper.filter.SimilarityFilter;
import com.pricescraper.model.Product;
import com.pricescraper.scrapper.*;
import com.pricescraper.service.CrawlerService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Log
public class CrawlerServiceImpl implements CrawlerService {

    @Override
    public List<Product> getProductList(String searchProduct) throws IOException {
        List<BaseScraper> crawlJobs = initCrawler();
        List<Product> productList = new ArrayList<>();
        for (BaseScraper scrapper : crawlJobs) {
            List<Product> currentProductList = scrapper.getProducts(searchProduct);
            if (currentProductList != null) {
                productList.addAll(currentProductList);
            }
        }
        log.info("Crawl finished!");
        if (productList.size() > 0) {
            List<Product> productListFiltered = SimilarityFilter.getTheMostSimilarProducts(productList, searchProduct);
            return productListFiltered;
        } else {
            return null;
        }
    }

    private List<BaseScraper> initCrawler() {
        List<BaseScraper> crawlJobs = new ArrayList<BaseScraper>();
        crawlJobs.add(new EmagScraper());
        crawlJobs.add(new PcGarageScraper());
        crawlJobs.add(new AltexScraper());
        crawlJobs.add(new MediaGalaxyScraper());
        return crawlJobs;
    }

}
