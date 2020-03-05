package com.pricescrapper.scrapper;
import com.pricescrapper.dto.ProductDTO;

import java.util.List;

public abstract class BaseScraper {

	public abstract List<ProductDTO> scrap(String seachProduct);

	public List<ProductDTO> getProducts(String searchProduct) {
		List<ProductDTO> products = scrap(searchProduct);
		return products;
	}

}
