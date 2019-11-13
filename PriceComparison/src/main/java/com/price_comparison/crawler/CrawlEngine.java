package com.price_comparison.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.price_comparison.dto.ProductDto;
import com.price_comparison.scrapper.AltexScrapper;
import com.price_comparison.scrapper.BaseScrapper;
import com.price_comparison.scrapper.EmagScrapper;
import com.price_comparison.scrapper.PcGarageScrapper;

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

	public void crawl(String product) throws Exception {
		List<BaseScrapper> crawlJobs = initCrawler(product);
		ExecutorService executor = Executors.newFixedThreadPool(crawlJobs.size());
		for (BaseScrapper scrapper : crawlJobs) {
			executor.execute((Runnable) scrapper);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		
		//return products;
	}
}
