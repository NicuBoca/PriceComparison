package com.pricescraper.scrapper;

import com.pricescraper.model.ProductBase;
import com.pricescraper.model.ProductHistory;
import com.pricescraper.types.ProductSourceType;
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

public class MediaGalaxyScraper extends BaseScraper {

    @Override
    public List<ProductBase> scrap(String searchProduct) {

        System.out.println("MediaGalaxy searcing for product: " + searchProduct);
        List<ProductBase> productsList = new ArrayList<ProductBase>();
        boolean prodExists = true;
        int pageCounter = 1;

        try {

            while (prodExists) {
                String searchUrl = buildUrl(searchProduct, pageCounter);
                URL obj = new URL(searchUrl);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                int responseCode = con.getResponseCode();
                //System.out.println("response code: " + responseCode);

                if (responseCode == 200) {
                    List<ProductBase> productsCurrentPage = extractData(con, searchProduct);
                    if (productsCurrentPage.isEmpty()) {
                        prodExists = false;
                    }
                    productsList.addAll(productsCurrentPage);

                } else {
                    System.out.println("MediaGalaxy response code: " + responseCode);
                }
                pageCounter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("[MEDIAGALAXY] Numarul de pagini (parcurse): " + (pageCounter - 1));
        System.out.println("[MEDIAGALAXY] Numarul de produse: " + productsList.size());
        return productsList;
    }

    private List<ProductBase> extractData(HttpURLConnection con, String searchProduct) throws IOException {
        List<ProductBase> products = new ArrayList<ProductBase>();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

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
            double prodPrice = prodItem.getFloat("price");
            String prodUrl = getProductUrl(prodItem);
            String prodImg = getProductImg(prodItem);
            String date = dateFormat.format(new Date());

            ProductHistory newProductHistory = ProductHistory.builder()
                    .price(prodPrice)
                    .date(date)
                    .stock(prodStock)
                    .build();
            List<ProductHistory> productHistories = new ArrayList<ProductHistory>();
            productHistories.add(newProductHistory);

            ProductBase newProduct = ProductBase.builder()
                    .name(prodName)
                    .source(ProductSourceType.MEDIAGALAXY)
                    .url(prodUrl)
                    .img(prodImg)
                    .history(productHistories)
                    .build();

            products.add(newProduct);
        }
        return products;
    }

    private String buildUrl(String searchProduct, int pageNumber) {
        String productUrlName = searchProduct.replaceAll("\\s+", "%2520");
        String baseUrl = "https://cerberus.mediagalaxy.ro/catalog/search/";
        String finalUrl = baseUrl + productUrlName + "?page=" + pageNumber;
        System.out.println(finalUrl);
        return finalUrl;
    }

    private String getProductUrl(JSONObject prodItem) {
        String prodUrlBase = "https://mediagalaxy.ro/";
        String prodUrlPath = prodItem.getString("url_key");
        String prodUrl = prodUrlBase + prodUrlPath;
        return prodUrl;
    }

    private String getProductImg(JSONObject prodItem) {
        String prodImgBase = "https://lcdn.altex.ro/resize";
        String prodImgPath = prodItem.getString("image");
        String prodImgPathMiddlePart = "/16fa6a9aef7ffd6209d5fd9338ffa0b1";
        String prodImgPathSecondPart = prodImgPath.substring(26);
        String prodImgPathFirstPart = prodImgPath.replace(prodImgPathSecondPart, "");
        String prodImg = prodImgBase + prodImgPathFirstPart + prodImgPathMiddlePart + prodImgPathSecondPart;
        return prodImg;
    }

}
