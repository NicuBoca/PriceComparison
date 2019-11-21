package com.pricescrapper.scrapper;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDto;

import java.util.List;



public abstract class BaseScrapper implements Runnable {
	protected CrawlEngine engine;
	protected String product;

	public BaseScrapper(String product, CrawlEngine engine) {
		this.product = product;
		this.engine = engine;
	}

	public abstract List<ProductDto> scrap();
	
	public void run() {
		List<ProductDto> products = scrap();	
		engine.addProducts(products);
	}
	
}
