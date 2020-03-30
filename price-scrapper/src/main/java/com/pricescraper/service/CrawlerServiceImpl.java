package com.pricescraper.service;

import com.pricescraper.filter.ProductMatching;
import com.pricescraper.model.Product;
import com.pricescraper.repository.ProductRepository;
import com.pricescraper.scrapper.*;

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
            List<Product> productList = scrapper.getProducts(searchProduct);
            productRepository.saveAll(productList);
        }
        System.out.println("Crawl finish!");
    }

    @Override
    public void cluster() {
        List<Product> productList = productRepository.findAll();
        productRepository.deleteAll();

        int count = 1;
        int i = 0;
        int nextI = i + 1;
        boolean ok;

        while (i < productList.size() - 1) {
            productList.get(i).setCluster(count);
            int j = getNextFreePosition(productList, nextI, productList.size());
            ok = true;
            while (j < productList.size() - 1) {
                if (ProductMatching.isSameProductByName(productList.get(i), productList.get(j))) {
                    productList.get(j).setCluster(count);
                } else if (ok) {
                    nextI = j;
                    ok = false;
                }
                j = getNextFreePosition(productList, j + 1, productList.size());
            }
            if (!ok) {
                i = nextI;
            } else {
                i = productList.size() - 1;
            }
            count++;
        }
        if(productList.get(productList.size()-1).getCluster() == 0) {
            productList.get(productList.size()-1).setCluster(count);
        }

        productRepository.saveAll(productList);
        System.out.println("Cluster finish!");
    }

    private int getNextFreePosition(List<Product> productList, int startPosition, int endPosition) {
        int freePosition = endPosition - 1;
        for (int i = startPosition; i < endPosition; i++) {
            if (productList.get(i).getCluster() == 0) {
                freePosition = i;
                break;
            }
        }
        return freePosition;
    }
}
