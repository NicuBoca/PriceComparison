package com.pricescraper.service.impl;

import com.pricescraper.filter.SimilarityFilter;
import com.pricescraper.model.Product;
import com.pricescraper.scrapper.AltexScraper;
import com.pricescraper.scrapper.BaseScraper;
import com.pricescraper.scrapper.EmagScraper;
import com.pricescraper.scrapper.MediaGalaxyScraper;
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
            productList.addAll(scrapper.getProducts(searchProduct));
        }
        List<Product> productListFiltered = SimilarityFilter.getTheMostSimilarProducts(productList, searchProduct);
        log.info("Crawl finished!");
        return productListFiltered;
    }

    private List<BaseScraper> initCrawler() {
        List<BaseScraper> crawlJobs = new ArrayList<BaseScraper>();
        crawlJobs.add(new EmagScraper());
        //crawlJobs.add(new PcGarageScraper());
        crawlJobs.add(new AltexScraper());
        crawlJobs.add(new MediaGalaxyScraper());
        return crawlJobs;
    }

}
