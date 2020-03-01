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

    private List<BaseScrapper> initCrawler() {
        List<BaseScrapper> crawlJobs = new ArrayList<BaseScrapper>();
        crawlJobs.add(new EmagScrapper());
    	crawlJobs.add(new PcGarageScrapper());
        crawlJobs.add(new AltexScrapper());
		crawlJobs.add(new MediaGalaxyScrapper());
        return crawlJobs;
    }

    @Override
    public void crawl(String searchProduct) {
        productRepository.deleteAll();
        List<BaseScrapper> crawlJobs = initCrawler();
        for (BaseScrapper scrapper : crawlJobs) {
            List<ProductDTO> productList = scrapper.getProducts(searchProduct);
            productRepository.saveAll(productList);
        }
    }
}
