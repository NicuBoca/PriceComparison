package com.pricescrapper.crawler;

import com.pricescrapper.dto.ProductDto;
import com.pricescrapper.scrapper.AltexScrapper;
import com.pricescrapper.scrapper.BaseScrapper;
import com.pricescrapper.scrapper.EmagScrapper;
import com.pricescrapper.scrapper.PcGarageScrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlEngine {
	List<ProductDto> products = new ArrayList<ProductDto>();
	
	public synchronized void addProducts(List<ProductDto> products) {
		this.products.addAll(products); 
	}

	public List<BaseScrapper> initCrawler(String product) {
		List<BaseScrapper> crawlJobs = new ArrayList<BaseScrapper>();
		crawlJobs.add(new EmagScrapper(product, this));
		crawlJobs.add(new PcGarageScrapper(product, this));
		crawlJobs.add(new AltexScrapper(product, this));
		return crawlJobs;
	}

	public List<ProductDto> crawl(String product) throws Exception {
		List<BaseScrapper> crawlJobs = initCrawler(product);
		ExecutorService executor = Executors.newFixedThreadPool(crawlJobs.size());
		for (BaseScrapper scrapper : crawlJobs) {
			executor.execute((Runnable) scrapper);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		for(ProductDto currentProduct : products) {
			currentProduct.display();
		}

		return products;
	}
}
