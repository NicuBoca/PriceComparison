package com.price_comparison.scrapper;

import java.util.List;

import com.price_comparison.crawler.CrawlEngine;
import com.price_comparison.dto.ProductDto;

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
