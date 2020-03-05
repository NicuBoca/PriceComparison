package com.pricescrapper.service;

import com.pricescrapper.dto.ProductDTO;
import com.pricescrapper.repository.ProductRepository;
import com.pricescrapper.scrapper.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    private ProductRepository productRepository;

    private List<BaseScraper> initCrawler() {
        List<BaseScraper> crawlJobs = new ArrayList<BaseScraper>();
        crawlJobs.add(new EmagScraper());
    	//crawlJobs.add(new PcGarageScraper());
        crawlJobs.add(new AltexScraper());
		crawlJobs.add(new MediaGalaxyScraper());
        return crawlJobs;
    }

    @Override
    public void crawl(String searchProduct) {
        productRepository.deleteAll();
        List<BaseScraper> crawlJobs = initCrawler();
        for (BaseScraper scrapper : crawlJobs) {
            List<ProductDTO> productList = scrapper.getProducts(searchProduct);
            productRepository.saveAll(productList);
        }
        System.out.println("Finish!");
    }
}
