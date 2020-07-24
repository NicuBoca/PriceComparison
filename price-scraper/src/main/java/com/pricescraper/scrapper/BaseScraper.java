package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import com.pricescraper.service.CrawlerService;
import lombok.SneakyThrows;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class BaseScraper implements Runnable {

    protected static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    protected static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    protected CrawlerService crawlerService;
    protected String product;

    public BaseScraper(String product, CrawlerService crawlerService) {
        this.product = product;
        this.crawlerService = crawlerService;
    }

    public abstract List<Product> scrap(String searchProduct) throws IOException;

    public void run() {
        List<Product> products = null;
        try {
            products = scrap(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
        crawlerService.addProducts(products);
    }

}
