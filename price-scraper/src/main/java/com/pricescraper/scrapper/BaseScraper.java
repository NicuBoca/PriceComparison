package com.pricescraper.scrapper;

import com.pricescraper.model.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class BaseScraper {

    protected static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public abstract List<Product> scrap(String seachProduct);

    public List<Product> getProducts(String searchProduct) {
        List<Product> products = scrap(searchProduct);
        return products;
    }

}
