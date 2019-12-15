package com.pricescrapper.scrapper;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDto;
import com.pricescrapper.filter.Filter;

import java.util.List;

public abstract class BaseScrapper implements Runnable {
	protected CrawlEngine engine;
	protected String searchProduct;

	public BaseScrapper(String product, CrawlEngine engine) {
		this.searchProduct = product;
		this.engine = engine;
	}

	public abstract List<ProductDto> scrap();
	
	public void run() {
		List<ProductDto> products = scrap();
		List<ProductDto> filteredProducts = Filter.filter(products, searchProduct);
		engine.addProducts(filteredProducts);
	}
	
}
