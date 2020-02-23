package com.pricescrapper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.pricescrapper.types.ProductSourceType;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ProductDto {
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSourceType source;
	private String img;
}
