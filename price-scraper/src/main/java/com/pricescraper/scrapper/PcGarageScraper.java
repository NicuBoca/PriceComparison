package com.pricescraper.scrapper;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductHistory;
import com.pricescraper.service.CrawlerService;
import com.pricescraper.types.ProductSourceType;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class PcGarageScraper extends BaseScraper {

    public PcGarageScraper(String product, CrawlerService engine) {
        super(product, engine);
    }

    @Override
    public List<Product> scrap(String searchProduct) throws IOException {

        Random rand = new Random();
        Connection.Response response = null;
        int statusCode;

        log.info(this.getClass().getSimpleName() + " searcing for product: " + searchProduct);
        List<Product> productsList = new ArrayList<Product>();

        String productUrlName = searchProduct.replaceAll("\\s+", "%2B");
        String searchUrlTest = "https://www.pcgarage.ro/cauta/" + productUrlName;
        log.info(this.getClass().getSimpleName() + " current URL: " + searchUrlTest);
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        try {
            response = Jsoup.connect(searchUrlTest)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36")
                    .timeout(30 * 1000)
                    .execute();
        } catch (IOException e) {
            log.error(this.getClass().getSimpleName() + ": " + e);
        }

        assert response != null;
        statusCode = response.statusCode();

        if (statusCode != 200) {
            System.out.println("Eroare conexiune PcGarage!");
            return productsList;
        } else {
            Document docTest = response.parse();

            List<Product> productsCurrentPage1 = extractData(docTest, searchProduct);
            productsList.addAll(productsCurrentPage1);

            int nrOfPages = getNumberOfPages(docTest);

            String baseUrl = getPageUrl(docTest);
            if (baseUrl == null) {
                baseUrl = searchUrlTest;
            }

            if (nrOfPages > 1) {
                for (int i = 2; i <= nrOfPages; i++) {
                    String searchUrl = buildSearchUrl(baseUrl, i);
                    log.info(this.getClass().getSimpleName() + " current URL: " + searchUrl);
                    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

                    int rand_time = rand.nextInt(4) + 2;
                    try {
                        TimeUnit.SECONDS.sleep(rand_time);
                    } catch (InterruptedException e) {
                        log.error(this.getClass().getSimpleName() + ": " + e);
                    }

                    Document doc = Jsoup.connect(searchUrl)
                            .userAgent(RandomUserAgent.getRandomUserAgent())
                            .timeout(300 * 1000)
                            .get();

                    List<Product> productsCurrentPage = extractData(doc, searchProduct);
                    if (productsCurrentPage.isEmpty()) {
                        break;
                    }
                    productsList.addAll(productsCurrentPage);
                }
            }
            return productsList;
        }
    }

    private List<Product> extractData(Document doc, String searchProduct) {
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
                log.error(this.getClass().getSimpleName() + ": " + e);
            }
        }
        return products;
    }

    private String buildSearchUrl(String baseUrl, int pageNumber) {
        return baseUrl + "/pagina" + pageNumber;
    }

    private String getPageUrl(Document doc) {
        Elements lastPageElement = doc.select("div#container div.main-content div#listing-right div.lr-options div.lr-pagination ul li:last-child a");
        if (lastPageElement != null) {
            String lastPageUrl = lastPageElement.attr("href");
            int endIndex = 0;
            for (int i = lastPageUrl.length() - 2; i >= 0; i--) {
                if (lastPageUrl.charAt(i) == '/') {
                    endIndex = i;
                    break;
                }
            }
            return lastPageUrl.substring(0, endIndex);
        }
        return null;
    }

    private int getNumberOfPages(Document doc) {
        int nrOfPages = 1;
        Elements lastPageElement = doc.select("div#container div.main-content div#listing-right div.lr-options div.lr-pagination ul li:last-child a");
        if (lastPageElement != null) {
            String lastPageUrl = lastPageElement.attr("href");
            StringBuilder lastPageNumber = new StringBuilder();
            for (int i = lastPageUrl.length() - 2; i >= 0; i--) {
                if (lastPageUrl.charAt(i) >= '0' && lastPageUrl.charAt(i) <= '9') {
                    lastPageNumber.append(lastPageUrl.charAt(i));
                } else {
                    break;
                }
            }
            lastPageNumber = lastPageNumber.reverse();
            nrOfPages = Integer.parseInt(String.valueOf(lastPageNumber));
        }
        return Math.min(nrOfPages, 5);
    }

    private String getProductName(Element prod) {
        return prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("title");
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
        return prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("href");
    }

    private String getProductImg(Element prod) {
        Elements prodImgElem = prod.select("div.product-box div.pb-image a");
        return prodImgElem.select("img").attr("src");
    }

}
