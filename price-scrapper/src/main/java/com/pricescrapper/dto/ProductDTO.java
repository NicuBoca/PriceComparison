package com.pricescrapper.dto;

import lombok.*;
import com.pricescrapper.types.ProductSourceType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString

@Document(collection = "product")
public class ProductDTO {
	@Id
	private String id;
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSourceType source;
	private String img;
	private double similarity;
}
