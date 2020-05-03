package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductHistory;
import com.pricescraper.types.ProductSourceType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PcGarageScraper extends BaseScraper {

    @Override
    public List<Product> scrap(String searchProduct) {

        System.out.println("PcGarage searcing for product: " + searchProduct);
        List<Product> productsList = new ArrayList<>();

        // de extras numarul de pagini

        try {
            String searchUrl = buildUrl(searchProduct);
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document doc = Jsoup.connect(searchUrl)
                    .timeout(30 * 1000)
                    .get();

            List<Product> productsCurrentPage = extractData(doc);
            if (productsCurrentPage.isEmpty()) {
                return null;
            }
            productsList.addAll(productsCurrentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsList;
    }

    private List<Product> extractData(Document doc) {
        List<Product> products = new ArrayList<>();
        Elements list = doc.select("div#content-wrapper div#listing-right div.grid-products div.product-box-container");

        for (Element prod : list) {
            try {
                String prodName = getProductName(prod);
                double prodPrice = getProductPrice(prod);
                int prodStock = getProductStock(prod);
                String prodUrl = getProductUrl(prod);
                String prodImg = getProductImg(prod);
                String date = dateFormat.format(new Date());

                ProductHistory newProductHistory = ProductHistory.builder()
                        .price(prodPrice)
                        .date(date)
                        .stock(prodStock)
                        .build();
                List<ProductHistory> productHistories = new ArrayList<ProductHistory>();
                productHistories.add(newProductHistory);

                Product newProduct = Product.builder()
                        .name(prodName)
                        .source(ProductSourceType.PCGARAGE)
                        .url(prodUrl)
                        .img(prodImg)
                        .history(productHistories)
                        .build();

                products.add(newProduct);

            } catch (Exception e) {
                System.out.println("Error PcGarage : " + e.getMessage());
            }
        }
        return products;
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

    private double getProductPrice(Element prod) {
        String prodPriceString = prod.select("div.product-box div.pb-price-container div.pb-price p.price").text();
        prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 4), "");
        prodPriceString = prodPriceString.replace(".", "");
        prodPriceString = prodPriceString.replace(",", ".");
        double prodPriceBeforeFormat = Double.parseDouble(prodPriceString);
        String prodPriceAfterFormat = decimalFormat.format(prodPriceBeforeFormat);
        return Double.parseDouble(prodPriceAfterFormat);
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
