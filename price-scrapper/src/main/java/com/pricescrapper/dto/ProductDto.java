package com.pricescrapper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.pricescrapper.types.ProductSourceType;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Document(collection = "product")
public class ProductDto {
	@Id
	private String id;
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSourceType source;
	private String img;
}
