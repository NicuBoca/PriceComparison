package com.pricescrapper.scrapper;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDTO;

import java.util.List;

public abstract class BaseScrapper implements Runnable {
	protected CrawlEngine engine;
	protected String searchProduct;

	public BaseScrapper(String product, CrawlEngine engine) {
		this.searchProduct = product;
		this.engine = engine;
	}

	public abstract List<ProductDTO> scrap();
	
	public void run() {
		List<ProductDTO> products = scrap();
		engine.addProducts(products);
	}
	
}
