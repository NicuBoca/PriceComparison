package com.pricescraper.service;

import com.pricescraper.model.Product;

import java.util.List;

public interface CrawlerService {
   List<Product> crawl(String searchProduct);
}
