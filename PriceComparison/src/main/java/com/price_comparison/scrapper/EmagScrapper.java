package com.price_comparison.scrapper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.price_comparison.crawler.CrawlEngine;
import com.price_comparison.dto.ProductDto;

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
					String prodPrice = prod.select("div.card-section-btm div.card-body p.product-new-price").text();
					String prodPriceSup = prod.select("div.card-section-btm div.card-body p.product-new-price sup")
							.text();
					prodPrice = prodPrice.replace(prodPrice.substring(prodPrice.length() - 6), "");
					prodPrice = prodPrice + "," + prodPriceSup;
					String prodStock = prod.select("div.card-section-btm div.card-body p.product-stock-status").text();
					if (prodStock.isEmpty()) {
						prodStock = "resigilat";
					}

					System.out.println("---------------------------------------------------------------------");
					System.out.println("----- Produs Emag -----");
					System.out.println("Nume: " + prodName);
					System.out.println("Pret: " + prodPrice + " lei");
					System.out.println("Stoc: " + prodStock);

//					 products.add(new ProductDto(prodName, prodPrice, prodStock));

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
