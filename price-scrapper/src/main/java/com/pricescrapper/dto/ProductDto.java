package com.pricescrapper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import com.pricescrapper.types.ProductSourceType;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ProductDto {
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSourceType source;
	private String img;
	
	public ProductDto(String name, Float price, int stock, String url, ProductSourceType source, String img) {
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.url = url;
		this.source = source;
		this.img = img;
	}

}
