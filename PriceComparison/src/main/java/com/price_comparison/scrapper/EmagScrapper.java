package com.price_comparison.scrapper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.price_comparison.crawler.CrawlEngine;
import com.price_comparison.dto.ProductDto;
import com.price_comparison.types.ProductSource;

public class EmagScrapper extends BaseScrapper {

	public EmagScrapper(String product, CrawlEngine engine) {
		super(product, engine);
	}

	public String buildUrl() {
		String productUrlName = product.replaceAll("\\s+", "%20");
		String baseUrl = "https://www.emag.ro/search/";
		String lastSegment = "?ref=effective_search";
		String finalUrl = baseUrl + productUrlName + lastSegment;
		System.out.println(finalUrl);
		return finalUrl;
	}

	@Override
	public List<ProductDto> scrap() {

		System.out.println("Emag searcing for product: " + product);
		List<ProductDto> products = new ArrayList<ProductDto>();

		try {
			String searchUrl = buildUrl();
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			Document doc = Jsoup.connect(searchUrl).timeout(30 * 1000).get();

			Elements list = doc.select("div#card_grid div.card-item div.card div.card-section-wrapper");

			for (Element prod : list) {
				try {
					String prodName = prod.select("div.card-section-mid h2.card-body a.product-title").text();

					String prodPriceString = prod.select("div.card-section-btm div.card-body p.product-new-price")
							.text();
					String prodPriceSupString = prod
							.select("div.card-section-btm div.card-body p.product-new-price sup").text();
					prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 6),
							"");
					prodPriceString = prodPriceString + "," + prodPriceSupString;
					prodPriceString = prodPriceString.replace("de la ", "");
					prodPriceString = prodPriceString.replace(".", "");					
					prodPriceString = prodPriceString.replace(",", ".");
					float prodPrice = Float.parseFloat(prodPriceString);

					String prodStock = prod.select("div.card-section-btm div.card-body p.product-stock-status").text();
					int prodStockStatus;
					if (prodStock.equals("stoc epuizat")) {
						prodStockStatus = 0;
					}
					else if (prodStock.isEmpty()) {
						prodStock = "resigilat";
						prodStockStatus = 2;
					}
					else {
						prodStockStatus = 1;
					}
					
					String prodUrl = prod.select("div.card-section-mid h2.card-body a.product-title").attr("href");
					
					String prodImg = prod.select("div.card-section-top div.card-heading a.thumbnail-wrapper div.thumbnail img.lozad").attr("data-src");

					System.out.println("---------------------------------------------------------------------");
					System.out.println("----- Produs Emag -----");
					System.out.println("Nume: " + prodName);
					System.out.println("Pret: " + prodPrice + " lei");
					System.out.println("Stoc: " + prodStock);
					System.out.println("Status: " + prodStockStatus);
					System.out.println("URL: " + prodUrl);
					System.out.println("Img: " + prodImg);

					products.add(new ProductDto(prodName, prodPrice, prodStockStatus, prodUrl, ProductSource.EMAG, prodImg));

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
