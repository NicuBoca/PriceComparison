package com.pricescrapper.scrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pricescrapper.dto.ProductDTO;

import com.pricescrapper.filter.Filter;
import com.pricescrapper.types.ProductSourceType;
import org.json.JSONArray;
import org.json.JSONObject;

public class AltexScraper extends BaseScraper {

	@Override
	public List<ProductDTO> scrap(String searchProduct) {

		System.out.println("Altex searcing for product: " + searchProduct);
		List<ProductDTO> productsList = new ArrayList<ProductDTO>();
		boolean prodExists = true;
		int pageCounter = 1;

		try {

			while(prodExists) {
				String searchUrl = buildUrl(searchProduct, pageCounter);
				URL obj = new URL(searchUrl);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = con.getResponseCode();
				//System.out.println("response code: " + responseCode);

				if(responseCode == 200) {
					List<ProductDTO> productsCurrentPage = extractData(con, searchProduct);
					if(productsCurrentPage.isEmpty()) {
						prodExists = false;
					}
					productsList.addAll(productsCurrentPage);

				} else {
					System.out.println("Altex response code: " + responseCode);
				}
				pageCounter++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("[ALTEX] Numarul de pagini (parcurse): " + (pageCounter-1));
		System.out.println("[ALTEX] Numarul de produse (in stoc): " + productsList.size());
		return productsList;
	}

	private List<ProductDTO> extractData(HttpURLConnection con, String searchProduct) throws IOException {
		List<ProductDTO> products = new ArrayList<ProductDTO>();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		JSONObject resObject = new JSONObject(response.toString());
		JSONArray productsArray = (JSONArray) resObject.get("products");

		for(int i=0; i<productsArray.length(); i++) {
			JSONObject prodItem = (JSONObject) productsArray.get(i);
			String prodName = prodItem.getString("name");
			float prodPrice = prodItem.getFloat("price");
			int prodStock = prodItem.getInt("stock_status");
			String prodUrl = getProductUrl(prodItem);
			String prodImg = getProductImg(prodItem);

			if (prodStock == 1) {
				double similarityCoefficient = Filter.getSimilarityCoefficient(searchProduct, prodName);

				ProductDTO currentProduct = ProductDTO.builder()
						.name(prodName)
						.price(prodPrice)
						.stock(prodStock)
						.url(prodUrl)
						.source(ProductSourceType.ALTEX)
						.img(prodImg)
						.similarity(similarityCoefficient)
						.build();

				products.add(currentProduct);
			}
		}
		return products;
	}

	private String buildUrl(String searchProduct, int pageNumber) {
		String productUrlName = searchProduct.replaceAll("\\s+","%2520");
		String baseUrl = "https://fenrir.altex.ro/catalog/search/";
		String finalUrl = baseUrl + productUrlName + "?page=" + pageNumber;
		System.out.println(finalUrl);
		return finalUrl;
	}

	private String getProductUrl(JSONObject prodItem) {
		String prodUrlBase = "https://altex.ro/";
		String prodUrlPath = prodItem.getString("url_key");
		String prodUrl = prodUrlBase + prodUrlPath;
		return prodUrl;
	}

	private String getProductImg(JSONObject prodItem) {
		String prodImgBase = "https://cdna.altex.ro/resize";
		String prodImgPath = prodItem.getString("image");
		String prodImgPathMiddlePart = "/16fa6a9aef7ffd6209d5fd9338ffa0b1";
		String prodImgPathSecondPart = prodImgPath.substring(26);
		String prodImgPathFirstPart = prodImgPath.replace(prodImgPathSecondPart, "");
		String prodImg = prodImgBase + prodImgPathFirstPart + prodImgPathMiddlePart + prodImgPathSecondPart;
		return prodImg;
	}

}
