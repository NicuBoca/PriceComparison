package com.pricescraper.scrapper;

import com.pricescraper.model.ProductBase;
import com.pricescraper.model.ProductDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class BaseScraper {

    protected static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public abstract List<ProductBase> scrap(String seachProduct);

    public List<ProductBase> getProducts(String searchProduct) {
        List<ProductBase> products = scrap(searchProduct);
        return products;
    }

}
