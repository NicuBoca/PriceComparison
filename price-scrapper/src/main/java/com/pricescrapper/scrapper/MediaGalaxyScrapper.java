package com.pricescrapper.scrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dao.ProductDAO;
import com.pricescrapper.dto.ProductDTO;

import com.pricescrapper.filter.Filter;
import com.pricescrapper.types.ProductSourceType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class MediaGalaxyScrapper extends BaseScrapper {

    @Autowired
    private ProductDAO productDAO;

    public MediaGalaxyScrapper(String product, CrawlEngine engine) {
        super(product, engine);
    }

    private String buildUrl() {
        String productUrlName = searchProduct.replaceAll("\\s+","%2520");
        String baseUrl = "https://cerberus.mediagalaxy.ro/catalog/search/";
        String finalUrl = baseUrl + productUrlName;
        System.out.println(finalUrl);
        return finalUrl;
    }

    private String getUrl(JSONObject prodItem) {
        String prodUrlBase = "https://mediagalaxy.ro/";
        String prodUrlPath = prodItem.getString("url_key");
        String prodUrl = prodUrlBase + prodUrlPath;
        return prodUrl;
    }

    private String getImg(JSONObject prodItem) {
        String prodImgBase = "https://lcdn.altex.ro/resize";
        String prodImgPath = prodItem.getString("image");
        String prodImgPathMiddlePart = "/16fa6a9aef7ffd6209d5fd9338ffa0b1";
        String prodImgPathSecondPart = prodImgPath.substring(26);
        String prodImgPathFirstPart = prodImgPath.replace(prodImgPathSecondPart, "");
        String prodImg = prodImgBase + prodImgPathFirstPart + prodImgPathMiddlePart + prodImgPathSecondPart;
        return prodImg;
    }

    @Override
    public List<ProductDTO> scrap() {

        System.out.println("MediaGalaxy searcing for product: " + searchProduct);
        List<ProductDTO> products = new ArrayList<ProductDTO>();

        try {
            String searchUrl = buildUrl();
            URL obj = new URL(searchUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            //int responseCode = con.getResponseCode();
            //System.out.println("response code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //System.out.println(response.toString());

            JSONObject resObject = new JSONObject(response.toString());
            JSONArray productsArray = (JSONArray) resObject.get("products");

            for(int i=0; i<productsArray.length(); i++) {
                JSONObject prodItem = (JSONObject) productsArray.get(i);
                String prodName = prodItem.getString("name");
                float prodPrice = prodItem.getFloat("price");
                int prodStock = prodItem.getInt("stock_status");
                String prodUrl = getUrl(prodItem);
                String prodImg = getImg(prodItem);

                if(prodStock==1) {
                    double similarityCoefficient = Filter.getSimilarityCoefficient(searchProduct, prodName);

                    ProductDTO currentProduct = ProductDTO.builder()
                            .name(prodName)
                            .price(prodPrice)
                            .stock(prodStock)
                            .url(prodUrl)
                            .source(ProductSourceType.MEDIAGALAXY)
                            .img(prodImg)
                            .similarity(similarityCoefficient)
                            .build();

                    productDAO.insertProduct(currentProduct);
                    products.add(currentProduct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

}
