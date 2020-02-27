package com.pricescrapper.crawler;

import com.pricescrapper.dto.ProductDto;
import com.pricescrapper.filter.Filter;
import com.pricescrapper.scrapper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlEngine {
	List<ProductDto> products = new ArrayList<ProductDto>();
	
	public synchronized void addProducts(List<ProductDto> products) {
		this.products.addAll(products); 
	}

	public List<BaseScrapper> initCrawler(String searchProduct) {
		List<BaseScrapper> crawlJobs = new ArrayList<BaseScrapper>();
		crawlJobs.add(new EmagScrapper(searchProduct, this));
		crawlJobs.add(new PcGarageScrapper(searchProduct, this));
		crawlJobs.add(new AltexScrapper(searchProduct, this));
		crawlJobs.add(new MediaGalaxyScrapper(searchProduct, this));
		return crawlJobs;
	}

	public List<ProductDto> crawl(String searchProduct) throws Exception {
		List<BaseScrapper> crawlJobs = initCrawler(searchProduct);
		ExecutorService executor = Executors.newFixedThreadPool(crawlJobs.size());
		for (BaseScrapper scrapper : crawlJobs) {
			executor.execute((Runnable) scrapper);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

//		Filter productsFilter = new Filter(products, searchProduct);
//		List<ProductDto> filteredProducts = productsFilter.similarityFilter();
//
//		for(ProductDto currentProduct : filteredProducts) {
//			System.out.println(currentProduct);
//		}
//
//		return filteredProducts;

		return products;
	}
}
