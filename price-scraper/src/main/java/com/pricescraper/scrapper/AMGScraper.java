package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductHistory;
import com.pricescraper.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class AMGScraper extends BaseScraper {
    public AMGScraper(String product, CrawlerService crawlerService) {
        super(product, crawlerService);
    }

    @Override
    public List<Product> scrap(String searchProduct) {

        log.info(this.getClass().getSimpleName() + " searcing for product: " + searchProduct);
        List<Product> productsList = new ArrayList<>();
        boolean prodExists = true;
        int pageCounter = 1;

        try {

            while (prodExists) {
                String searchUrl = buildSearchUrl(searchProduct, pageCounter);
                log.info(this.getClass().getSimpleName() + " current URL: " + searchUrl);
                URL obj = new URL(searchUrl);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                int responseCode = con.getResponseCode();

                if (responseCode == 200) {
                    List<Product> productsCurrentPage = extractData(con);
                    if (productsCurrentPage.isEmpty()) {
                        prodExists = false;
                    }
                    productsList.addAll(productsCurrentPage);

                } else {
                    log.warn(this.getClass().getSimpleName() + " response code: " + responseCode);
                }
                pageCounter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsList;
    }

    private List<Product> extractData(HttpURLConnection con) throws IOException {
        List<Product> products = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject resObject = new JSONObject(response.toString());
        JSONArray productsArray = (JSONArray) resObject.get("products");

        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject prodItem = (JSONObject) productsArray.get(i);
            int prodStock = prodItem.getInt("stock_status");

            String prodName = prodItem.getString("name");
            double prodPrice = prodItem.getDouble("price");
            String prodUrl = getProductUrl(prodItem);
            String prodImg = getProductImg(prodItem);
            String date = dateFormat.format(new Date());

            ProductHistory newProductHistory = ProductHistory.builder()
                    .price(prodPrice)
                    .date(date)
                    .stock(prodStock)
                    .build();

            List<ProductHistory> productHistories = new ArrayList<>();
            productHistories.add(newProductHistory);
            Product newProduct = buildProduct(prodName, prodUrl, prodImg, productHistories);
            products.add(newProduct);
        }
        return products;
    }

    protected abstract Product buildProduct(String prodName, String prodUrl, String prodImg, List<ProductHistory> productHistories);

    protected abstract String buildSearchUrl(String searchProduct, int pageNumber);

    protected abstract String getProductUrl(JSONObject prodItem);

    protected abstract String getProductImg(JSONObject prodItem);
}
