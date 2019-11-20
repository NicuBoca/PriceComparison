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
					
					String prodPriceString = prod.select("div.product-box div.pb-price-container div.pb-price p.price").text();
					prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 4),
							"");
					prodPriceString = prodPriceString.replace(".", "");					
					prodPriceString = prodPriceString.replace(",", ".");
					float prodPrice = Float.parseFloat(prodPriceString);
					
					String prodStock = prod.select("div.product-box div.pb-price-container div.pb-availability").text();
					int prodStockStatus;
					if (prodStock.equals("Nu este in stoc")) {
						prodStockStatus = 0;
					}
					else {
						prodStockStatus = 1;
					}
					
					String prodUrl = prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("href");
					
					Elements prodImgElem = prod.select("div.product-box div.pb-image a");
					String prodImg = prodImgElem.select("img").attr("src");
									
					System.out.println("---------------------------------------------------------------------");
					System.out.println("----- Produs PcGarage -----");
					System.out.println("Nume: " + prodName);
					System.out.println("Pret: " + prodPrice);
					System.out.println("Stoc: " + prodStock);
					System.out.println("Status: " + prodStockStatus);
					System.out.println("URL: " + prodUrl);
					System.out.println("Img: " + prodImg);

					products.add(new ProductDto(prodName, prodPrice, prodStockStatus, prodUrl, ProductSource.PCGARAGE, prodImg));

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
