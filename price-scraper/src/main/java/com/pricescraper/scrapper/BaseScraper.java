package com.pricescraper.scrapper;

import com.pricescraper.model.Product;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class BaseScraper {

    protected static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    protected static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public abstract List<Product> scrap(String seachProduct) throws IOException;

    public List<Product> getProducts(String searchProduct) throws IOException {
        List<Product> products = scrap(searchProduct);
        return products;
    }

}
