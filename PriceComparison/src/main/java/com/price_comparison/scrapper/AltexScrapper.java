package com.price_comparison.scrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.price_comparison.crawler.CrawlEngine;
import com.price_comparison.dto.ProductDto;

public class AltexScrapper extends BaseScrapper {

	public AltexScrapper(String product, CrawlEngine engine) {
		super(product, engine);
	}

	public String buildUrl() {
		String productUrlName = product.replaceAll("\\s+","%2520");		
		String baseUrl = "https://fenrir.altex.ro/catalog/search/";
		String finalUrl = baseUrl + productUrlName;
		System.out.println(finalUrl);
		return finalUrl;
	}

	@Override
	public List<ProductDto> scrap() {

		System.out.println("Altex searcing for product: " + product);
		List<ProductDto> products = new ArrayList<ProductDto>();

		try {
			String searchUrl = buildUrl();
			URL obj = new URL(searchUrl);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			System.out.println("response code: " + responseCode);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//System.out.println(response.toString());
			
			JSONObject resObject = new JSONObject(response.toString());
			JSONArray productsArray = (JSONArray) resObject.get("products");
			
			for(int i=0; i<productsArray.length(); i++) {
				JSONObject prodItem = (JSONObject) productsArray.get(i);				
				//System.out.println(productItem);
				String prodName = prodItem.getString("name");
				double prodPrice = prodItem.getDouble("price");
				String prodStock;
				int stockStatus = prodItem.getInt("stock_status");
				
				if(stockStatus == 1) {
					prodStock = "in stoc";
				}
				else {
					prodStock = "stoc epuizat";					
				}
				
				System.out.println("---------------------------------------------------------------------");
				System.out.println("----- Produs Altex -----");
				System.out.println("Nume: " + prodName);
				System.out.println("Pret: " + prodPrice + " lei");
				System.out.println("Stoc: " + prodStock);
				
				// products.add(new ProductDto(prodName, prodPrice, prodStock));
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return products;
	}

}
