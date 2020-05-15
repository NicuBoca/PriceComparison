package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductHistory;
import com.pricescraper.service.CrawlerService;
import com.pricescraper.types.ProductSourceType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EmagScraper extends BaseScraper {

    public EmagScraper(String product, CrawlerService engine) {
        super(product, engine);
    }

    @Override
    public List<Product> scrap(String searchProduct) throws IOException {

        Random rand = new Random();
        Connection.Response response = null;
        int statusCode;

        System.out.println("Emag searcing for product: " + searchProduct);
        List<Product> productsList = new ArrayList<Product>();

        String searchUrlTest = buildUrl(searchProduct, 1);
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        try {
            response = Jsoup.connect(searchUrlTest)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36")
                    .timeout(30 * 1000)
                    .execute();
        } catch (IOException e) {
            System.out.println("EMAG: " + e);
        }

        assert response != null;
        statusCode = response.statusCode();

        if (statusCode != 200) {
            System.out.println("Eroare conexiune Emag!");
            return productsList;
        } else {
            Document docTest = response.parse();

            List<Product> productsCurrentPage1 = extractData(docTest);
            productsList.addAll(productsCurrentPage1);

            int nrOfPages = getNumberOfPages(docTest);
            System.out.println("[EMAG] Numarul de pagini (total): " + nrOfPages);

            if (nrOfPages > 1) {
                for (int i = 2; i <= nrOfPages; i++) {
                    String searchUrl = buildUrl(searchProduct, i);
                    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

                    int rand_time = rand.nextInt(3) + 1;
                    try {
                        TimeUnit.SECONDS.sleep(rand_time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Document doc = Jsoup.connect(searchUrl)
                            .userAgent(RandomUserAgent.getRandomUserAgent())
                            .timeout(30 * 1000)
                            .get();

                    List<Product> productsCurrentPage = extractData(doc);
                    if (productsCurrentPage.isEmpty()) {
                        break;
                    }
                    productsList.addAll(productsCurrentPage);
                }
            }
            System.out.println("[EMAG] Numarul de produse: " + productsList.size());
            return productsList;
        }
    }

    private List<Product> extractData(Document doc) {
        List<Product> products = new ArrayList<>();
        Elements list = doc.select("div#card_grid div.card-item div.card div.card-section-wrapper");

        for (Element prod : list) {
            try {
                int prodStock = getProductStock(prod);
                String prodName = getProductName(prod);
                double prodPrice = getProductPrice(prod);
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
                        .source(ProductSourceType.EMAG)
                        .url(prodUrl)
                        .img(prodImg)
                        .history(productHistories)
                        .build();

                products.add(newProduct);

            } catch (Exception e) {
                System.out.println("Error Emag : " + e.getMessage());
            }
        }
        return products;
    }

    private String buildUrl(String searchProduct, int pageNumber) {
        String productUrlName = searchProduct.replaceAll("\\s+", "%20");
        String baseUrl = "https://www.emag.ro/search/vendor/emag/";
        String finalUrl = baseUrl + productUrlName + "/p" + pageNumber;
        System.out.println(finalUrl);
        return finalUrl;
    }

    private int getNumberOfPages(Document doc) {
        int nrOfPages = 1;
        Elements pageList = doc.select("div.listing-panel-footer div.row ul#listing-paginator li");
        for (Element page : pageList) {
            String data = page.select("a").attr("data-page");
            if (!data.equals("")) {
                int currentNr = Integer.parseInt(data);
                if (currentNr > nrOfPages) {
                    nrOfPages = currentNr;
                }
            }
        }
        if (nrOfPages < 10) {
            return nrOfPages;
        } else {
            return 10;
        }
    }

    private String getProductName(Element prod) {
        return prod.select("div.card-section-mid h2.card-body a.product-title").text();
    }

    private double getProductPrice(Element prod) {
        String prodPriceString = prod.select("div.card-section-btm div.card-body p.product-new-price").text();
        String prodPriceSupString = prod.select("div.card-section-btm div.card-body p.product-new-price sup").text();
        prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 6),
                "");
        prodPriceString = prodPriceString + "," + prodPriceSupString;
        prodPriceString = prodPriceString.replace("de la ", "");
        prodPriceString = prodPriceString.replace(".", "");
        prodPriceString = prodPriceString.replace(",", ".");
        double prodPriceBeforeFormat = Double.parseDouble(prodPriceString);
        String prodPriceAfterFormat = decimalFormat.format(prodPriceBeforeFormat);
        return Double.parseDouble(prodPriceAfterFormat);
    }

    private int getProductStock(Element prod) {
        String prodStockText = prod.select("div.card-section-btm div.card-body p.product-stock-status").text();
        int prodStock;
        if (prodStockText.equals("stoc epuizat") || prodStockText.equals("indisponibil")) {
            prodStock = 0;
        } else if (prodStockText.isEmpty()) {
            prodStock = 2; // resigilat
        } else {
            prodStock = 1;
        }
        return prodStock;
    }

    private String getProductUrl(Element prod) {
        String prodUrl = prod.select("div.card-section-mid h2.card-body a.product-title").attr("href");
        return prodUrl;
    }

    private String getProductImg(Element prod) {
        String prodImg = prod.select("div.card-section-top div.card-heading a.thumbnail-wrapper div.thumbnail img.lozad").attr("data-src");
        return prodImg;
    }

}
