package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class PcGarageScraper extends BaseScraper {

    @Override
    public List<Product> scrap(String searchProduct) {

        System.out.println("PcGarage searcing for product: " + searchProduct);
        List<Product> products = new ArrayList<>();

        try {
            String searchUrl = buildUrl(searchProduct);
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document doc = Jsoup.connect(searchUrl)
                    .timeout(30 * 1000)
                    .get();

            Elements list = doc.select("div#content-wrapper div#listing-right div.grid-products div.product-box-container");

            for (Element prod : list) {
                try {
                    String prodName = getProductName(prod);
                    float prodPrice = getProductPrice(prod);
                    int prodStock = getProductStock(prod);
                    String prodUrl = getProductUrl(prod);
                    String prodImg = getProductImg(prod);

                    if (prodStock == 1) {
//                        double similarityCoefficient = SearchSimilarity.getSimilarityBetweenSearchAndFoundName(searchProduct, prodName);

//                        ProductDTO currentProduct = ProductDTO.builder()
//                                .name(prodName)
//                                .price(prodPrice)
//                                .stock(prodStock)
//                                .url(prodUrl)
//                                .source(ProductSourceType.PCGARAGE)
//                                .img(prodImg)
//                                .similarity(similarityCoefficient)
//                                .build();

//                        products.add(currentProduct);
                    }

                } catch (Exception e) {
                    System.out.println("Error PcGarage : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String buildUrl(String searchProduct) {
        String productUrlName = searchProduct.replaceAll("\\s+", "+");
        String baseUrl = "https://www.pcgarage.ro/cauta/";
        String finalUrl = baseUrl + productUrlName;
        System.out.println(finalUrl);
        return finalUrl;
    }

    private String getProductName(Element prod) {
        String prodName = prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("title");
        return prodName;
    }

    private float getProductPrice(Element prod) {
        String prodPriceString = prod.select("div.product-box div.pb-price-container div.pb-price p.price").text();
        prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 4), "");
        prodPriceString = prodPriceString.replace(".", "");
        prodPriceString = prodPriceString.replace(",", ".");
        float prodPrice = Float.parseFloat(prodPriceString);
        return prodPrice;
    }

    private int getProductStock(Element prod) {
        String prodStockText = prod.select("div.product-box div.pb-price-container div.pb-availability").text();
        int prodStock;
        if (prodStockText.equals("Nu este in stoc")) {
            prodStock = 0;
        } else {
            prodStock = 1;
        }
        return prodStock;
    }

    private String getProductUrl(Element prod) {
        String prodUrl = prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("href");
        return prodUrl;
    }

    private String getProductImg(Element prod) {
        Elements prodImgElem = prod.select("div.product-box div.pb-image a");
        String prodImg = prodImgElem.select("img").attr("src");
        return prodImg;
    }

}
