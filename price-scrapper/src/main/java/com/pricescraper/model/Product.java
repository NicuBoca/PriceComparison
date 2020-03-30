package com.pricescraper.model;

import lombok.*;
import com.pricescraper.types.ProductSourceType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Document(collection = "product")
public class Product {
	@Id
	private String id;
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSourceType source;
	private String img;
	private double similarity;
	private int cluster;

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
}
