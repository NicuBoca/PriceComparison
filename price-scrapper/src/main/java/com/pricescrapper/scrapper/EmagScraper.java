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

public class EmagScraper extends BaseScraper {

    @Override
    public List<ProductDTO> scrap(String searchProduct) {

        System.out.println("Emag searcing for product: " + searchProduct);
        List<ProductDTO> productsList = new ArrayList<ProductDTO>();

        try {
            String searchUrlTest = buildUrl(searchProduct, 1);
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document docTest = Jsoup.connect(searchUrlTest)
                    .timeout(30 * 1000)
                    .get();

            List<ProductDTO> productsCurrentPage1 = extractData(docTest, searchProduct);
            productsList.addAll(productsCurrentPage1);

            int nrOfPages = getNumberOfPages(docTest);
            System.out.println("[EMAG] Numarul de pagini: " + nrOfPages);

            if(nrOfPages > 1) {
                for(int i=2; i<=nrOfPages; i++) {
                    String searchUrl = buildUrl(searchProduct, i);
                    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
                    Document doc = Jsoup.connect(searchUrl)
                            .timeout(30 * 1000)
                            .get();

                    List<ProductDTO> productsCurrentPage = extractData(doc, searchProduct);
                    productsList.addAll(productsCurrentPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("[EMAG] Numarul de produse (in stoc): " + productsList.size());
        return productsList;
    }

    private List<ProductDTO> extractData(Document doc, String searchProduct) {
        List<ProductDTO> products = new ArrayList<ProductDTO>();
        Elements list = doc.select("div#card_grid div.card-item div.card div.card-section-wrapper");

        for (Element prod : list) {
            try {
                String prodName = getProductName(prod);
                float prodPrice = getProductPrice(prod);
                int prodStock = getProductStock(prod);
                String prodUrl = getProductUrl(prod);
                String prodImg = getProductImg(prod);

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
                } else if(prodStock == 0){
                    break;
                }
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
        int nrOfPages = 0;
        Elements pageList = doc.select("div.listing-panel-footer div.row ul#listing-paginator li");
        for (Element page : pageList) {
            String data = page.select("a").attr("data-page");
            if(data!="") {
                int currentNr = Integer.parseInt(data);
                if(currentNr > nrOfPages) {
                    nrOfPages = currentNr;
                }
            }
        }
        return nrOfPages;
    }

    private String getProductName(Element prod) {
        return prod.select("div.card-section-mid h2.card-body a.product-title").text();
    }

    private float getProductPrice(Element prod) {
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

    private int getProductStock(Element prod) {
        String prodStockText = prod.select("div.card-section-btm div.card-body p.product-stock-status").text();
        int prodStock;
        if (prodStockText.equals("stoc epuizat") || prodStockText.equals("indisponibil")) {
            prodStock = 0;
        } else if(prodStockText.isEmpty()) {
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
