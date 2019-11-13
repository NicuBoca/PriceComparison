package com.price_comparison.dto;

import com.price_comparison.types.ProductSource;

import lombok.NoArgsConstructor;

@NoArgsConstructor

public class ProductDto {
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSource source;
	private String img;
	
	public ProductDto(String name, Float price, int stock, String url, ProductSource source, String img) {
		super();
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.url = url;
		this.source = source;
		this.img = img;
	}
	
	public String getName() {
		return name;
	}

	public Float getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public String getUrl() {
		return url;
	}

	public ProductSource getSource() {
		return source;
	}

	public String getImg() {
		return img;
	}
}
