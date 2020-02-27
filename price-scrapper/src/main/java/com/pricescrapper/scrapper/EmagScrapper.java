package com.pricescrapper.scrapper;

import java.util.ArrayList;
import java.util.List;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDto;
import com.pricescrapper.filter.Filter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.pricescrapper.types.ProductSourceType;


public class EmagScrapper extends BaseScrapper {

	public EmagScrapper(String product, CrawlEngine engine) {
		super(product, engine);
	}

	private String buildUrl() {
		String productUrlName = searchProduct.replaceAll("\\s+", "%20");
		String baseUrl = "https://www.emag.ro/search/";
		String lastSegment = "?ref=effective_search";
		String finalUrl = baseUrl + productUrlName + lastSegment;
		System.out.println(finalUrl);
		return finalUrl;
	}

	private String getName(Element prod) {
		String prodName = prod.select("div.card-section-mid h2.card-body a.product-title").text();
		return prodName;
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
		float prodPrice = Float.parseFloat(prodPriceString);
		return prodPrice;
	}

	private int getStock(Element prod) {
		String prodStockText = prod.select("div.card-section-btm div.card-body p.product-stock-status").text();
		int prodStock;
		if (prodStockText.equals("stoc epuizat")) {
			prodStock = 0;
		}
		else if (prodStockText.isEmpty()) {
			prodStockText = "resigilat";
			prodStock = 2;
		}
		else {
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

	@Override
	public List<ProductDto> scrap() {

		System.out.println("Emag searcing for product: " + searchProduct);
		double similarityRate;
		List<ProductDto> products = new ArrayList<ProductDto>();

		try {
			String searchUrl = buildUrl();
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

					if(prodStock==1) {
						ProductDto currentProduct = new ProductDto(prodName, prodPrice, prodStock, prodUrl, ProductSourceType.EMAG, prodImg);
						similarityRate = Filter.getSimilarityRate(searchProduct, currentProduct.getName());
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
