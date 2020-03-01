package com.pricescrapper.scrapper;

import java.util.ArrayList;
import java.util.List;

import com.pricescrapper.dto.ProductDTO;
import com.pricescrapper.filter.Filter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.pricescrapper.types.ProductSourceType;

public class EmagScrapper extends BaseScrapper {

    private String buildUrl(String searchProduct) {
        String productUrlName = searchProduct.replaceAll("\\s+", "%20");
        String baseUrl = "https://www.emag.ro/search/";
        String lastSegment = "?ref=effective_search";
        String finalUrl = baseUrl + productUrlName + lastSegment;
        System.out.println(finalUrl);
        return finalUrl;
    }

    private String getName(Element prod) {
        return prod.select("div.card-section-mid h2.card-body a.product-title").text();
    }

    private float getPrice(Element prod) {
        String prodPriceString = prod.select("div.card-section-btm div.card-body p.product-new-price").text();
        String prodPriceSupString = prod.select("div.card-section-btm div.card-body p.product-new-price sup").text();
        prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 6),
                "");
        prodPriceString = prodPriceString + "," + prodPriceSupString;
        prodPriceString = prodPriceString.replace("de la ", "");
        prodPriceString = prodPriceString.replace(".", "");
        prodPriceString = prodPriceString.replace(",", ".");
        return Float.parseFloat(prodPriceString);
    }

    private int getStock(Element prod) {
        String prodStockText = prod.select("div.card-section-btm div.card-body p.product-stock-status").text();
        int prodStock;
        if (prodStockText.equals("stoc epuizat")) {
            prodStock = 0;
        } else if (prodStockText.isEmpty()) {
            prodStockText = "resigilat";
            prodStock = 2;
        } else {
            prodStock = 1;
        }
        return prodStock;
    }

    private String getUrl(Element prod) {
        String prodUrl = prod.select("div.card-section-mid h2.card-body a.product-title").attr("href");
        return prodUrl;
    }

    private String getImg(Element prod) {
        String prodImg = prod.select("div.card-section-top div.card-heading a.thumbnail-wrapper div.thumbnail img.lozad").attr("data-src");
        return prodImg;
    }

    public List<ProductDTO> scrap(String searchProduct) {

        System.out.println("Emag searcing for product: " + searchProduct);
        List<ProductDTO> products = new ArrayList<ProductDTO>();

        try {
            String searchUrl = buildUrl(searchProduct);
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document doc = Jsoup.connect(searchUrl)
                    .timeout(30 * 1000)
                    .get();

            Elements list = doc.select("div#card_grid div.card-item div.card div.card-section-wrapper");

            for (Element prod : list) {
                try {
                    String prodName = getName(prod);
                    float prodPrice = getPrice(prod);
                    int prodStock = getStock(prod);
                    String prodUrl = getUrl(prod);
                    String prodImg = getImg(prod);

                    if (prodStock == 1) {
                        double similarityCoefficient = Filter.getSimilarityCoefficient(searchProduct, prodName);

                        ProductDTO currentProduct = ProductDTO.builder()
                                .name(prodName)
                                .price(prodPrice)
                                .stock(prodStock)
                                .url(prodUrl)
                                .source(ProductSourceType.EMAG)
                                .img(prodImg)
                                .similarity(similarityCoefficient)
                                .build();

                        products.add(currentProduct);
                    }

                } catch (Exception e) {
                    System.out.println("Error Emag : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

}
