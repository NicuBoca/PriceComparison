package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductHistory;
import com.pricescraper.service.CrawlerService;
import com.pricescraper.types.ProductSourceType;
import org.json.JSONObject;

import java.util.List;

public class AltexScraper extends AMGScraper {

    public AltexScraper(String product, CrawlerService engine) {
        super(product, engine);
    }

    @Override
    protected Product buildProduct(String prodName, String prodUrl, String prodImg, List<ProductHistory> productHistories) {
        return Product.builder()
                        .name(prodName)
                        .source(ProductSourceType.ALTEX)
                        .url(prodUrl)
                        .img(prodImg)
                        .history(productHistories)
                        .build();
    }

    @Override
    protected String buildSearchUrl(String searchProduct, int pageNumber) {
        String productUrlName = searchProduct.replaceAll("\\s+", "%2520");
        String baseUrl = "https://fenrir.altex.ro/catalog/search/";
        return baseUrl + productUrlName + "?page=" + pageNumber;
    }

    @Override
    protected String getProductUrl(JSONObject prodItem) {
        String prodUrlBase = "https://altex.ro/";
        String prodUrlPath = prodItem.getString("url_key");
        return prodUrlBase + prodUrlPath;
    }

    @Override
    protected String getProductImg(JSONObject prodItem) {
        String prodImgBase = "https://cdna.altex.ro/resize";
        String prodImgPath = prodItem.getString("image");
        String prodImgPathMiddlePart = "/16fa6a9aef7ffd6209d5fd9338ffa0b1";
        String prodImgPathSecondPart = prodImgPath.substring(26);
        String prodImgPathFirstPart = prodImgPath.replace(prodImgPathSecondPart, "");
        return prodImgBase + prodImgPathFirstPart + prodImgPathMiddlePart + prodImgPathSecondPart;
    }

}
