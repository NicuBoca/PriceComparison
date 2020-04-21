package com.pricescraper.service.impl;

import com.pricescraper.filter.SimilarityFilter;
import com.pricescraper.model.Product;
import com.pricescraper.scrapper.AltexScraper;
import com.pricescraper.scrapper.BaseScraper;
import com.pricescraper.scrapper.EmagScraper;
import com.pricescraper.scrapper.MediaGalaxyScraper;
import com.pricescraper.service.CrawlerService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlerServiceImpl implements CrawlerService {

    @Override
    public List<Product> getProductList(String searchProduct) {
        List<BaseScraper> crawlJobs = initCrawler();
        List<Product> productList = new ArrayList<>();
        for (BaseScraper scrapper : crawlJobs) {
            productList.addAll(scrapper.getProducts(searchProduct));
        }
        List<Product> productListFiltered = SimilarityFilter.getTheMostSimilarProducts(productList, searchProduct);
        System.out.println("Crawl finish!");
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