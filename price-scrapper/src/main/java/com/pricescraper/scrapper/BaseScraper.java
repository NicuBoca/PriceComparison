package com.pricescraper.scrapper;

import com.pricescraper.model.Product;

import java.util.List;

public abstract class BaseScraper {

    public abstract List<Product> scrap(String seachProduct);

    public List<Product> getProducts(String searchProduct) {
        List<Product> products = scrap(searchProduct);
        return products;
    }

}
