package com.price_comparison.scrapper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.price_comparison.crawler.CrawlEngine;
import com.price_comparison.dto.ProductDto;

public class PcGarageScrapper extends BaseScrapper {

	public PcGarageScrapper(String product, CrawlEngine engine) {
		super(product, engine);
	}

	public String buildUrl() {
		String productUrlName = product.replaceAll("\\s+","+");		
		String baseUrl = "https://www.pcgarage.ro/cauta/";
		String finalUrl = baseUrl + productUrlName;
		System.out.println(finalUrl);
		return finalUrl;
	}

	@Override
	public List<ProductDto> scrap() {

		System.out.println("PcGarage searcing for product: " + product);
		List<ProductDto> products = new ArrayList<ProductDto>();

		try {
			String searchUrl = buildUrl();
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			Document doc = Jsoup.connect(searchUrl).timeout(30 * 1000).get();

			Elements list = doc.select("div#content-wrapper div#listing-right div.grid-products div.product-box-container");

			for (Element prod : list) {
				try {
					String prodName = prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("title");
					String prodPrice = prod.select("div.product-box div.pb-price-container div.pb-price p.price").text();
					String prodStock = prod.select("div.product-box div.pb-price-container div.pb-availability").text();
									
					System.out.println("---------------------------------------------------------------------");
					System.out.println("----- Produs PcGarage -----");
					System.out.println("Nume: " + prodName);
					System.out.println("Pret: " + prodPrice);
					System.out.println("Stoc: " + prodStock);

					// products.add(new ProductDto(prodName, prodPrice, prodStock));

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
