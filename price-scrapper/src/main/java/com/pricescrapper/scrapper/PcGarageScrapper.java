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

public class PcGarageScrapper extends BaseScrapper {

	private String buildUrl(String searchProduct) {
		String productUrlName = searchProduct.replaceAll("\\s+","+");
		String baseUrl = "https://www.pcgarage.ro/cauta/";
		String finalUrl = baseUrl + productUrlName;
		System.out.println(finalUrl);
		return finalUrl;
	}

	private String getName(Element prod) {
		String prodName = prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("title");
		return prodName;
	}

	private float getPrice(Element prod) {
		String prodPriceString = prod.select("div.product-box div.pb-price-container div.pb-price p.price").text();
		prodPriceString = prodPriceString.replace(prodPriceString.substring(prodPriceString.length() - 4),"");
		prodPriceString = prodPriceString.replace(".", "");
		prodPriceString = prodPriceString.replace(",", ".");
		float prodPrice = Float.parseFloat(prodPriceString);
		return prodPrice;
	}

	private int getStock(Element prod) {
		String prodStockText = prod.select("div.product-box div.pb-price-container div.pb-availability").text();
		int prodStock;
		if (prodStockText.equals("Nu este in stoc")) {
			prodStock = 0;
		} else {
			prodStock = 1;
		}
		return prodStock;
	}

	private String getUrl(Element prod) {
		String prodUrl = prod.select("div.product-box div.pb-specs-container div.pb-name a").attr("href");
		return prodUrl;
	}

	private String getImg(Element prod) {
		Elements prodImgElem = prod.select("div.product-box div.pb-image a");
		String prodImg = prodImgElem.select("img").attr("src");
		return prodImg;
	}

	@Override
	public List<ProductDTO> scrap(String searchProduct) {

		System.out.println("PcGarage searcing for product: " + searchProduct);
		List<ProductDTO> products = new ArrayList<ProductDTO>();

		try {
			String searchUrl = buildUrl(searchProduct);
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			Document doc = Jsoup.connect(searchUrl)
					.timeout(30 * 1000)
					.get();

			Elements list = doc.select("div#content-wrapper div#listing-right div.grid-products div.product-box-container");

			for (Element prod : list) {
				try {
					String prodName = getName(prod);
					float prodPrice = getPrice(prod);
					int prodStock = getStock(prod);
					String prodUrl = getUrl(prod);
					String prodImg = getImg(prod);

					if(prodStock==1) {
						double similarityCoefficient = Filter.getSimilarityCoefficient(searchProduct, prodName);

						ProductDTO currentProduct = ProductDTO.builder()
								.name(prodName)
								.price(prodPrice)
								.stock(prodStock)
								.url(prodUrl)
								.source(ProductSourceType.PCGARAGE)
								.img(prodImg)
								.similarity(similarityCoefficient)
								.build();

						//productDAO.insertProduct(currentProduct);
						products.add(currentProduct);
					}

				} catch (Exception e) {
					System.out.println("Error PcGarage : " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return products;
	}

}
