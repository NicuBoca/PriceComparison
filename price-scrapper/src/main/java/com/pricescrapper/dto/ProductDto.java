package com.pricescrapper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import com.pricescrapper.types.ProductSourceType;

@NoArgsConstructor
@Getter
public class ProductDto {
	private String name;
	private Float price;
	private int stock;
	private String url;
	private ProductSourceType source;
	private String img;
	
	public ProductDto(String name, Float price, int stock, String url, ProductSourceType source, String img) {
		super();
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.url = url;
		this.source = source;
		this.img = img;
	}

	public void display() {
		System.out.println("---------------------------------------------------------------------");
		System.out.println("----- " + this.source + "-----");
		System.out.println("Nume: " + this.name);
		System.out.println("Pret: " + this.price + " lei");
		System.out.println("Stoc: " + this.stock);

		if(this.stock == 1) {
			System.out.println("Status: in stoc");
		}
		else if(this.stock == 2) {
			System.out.println("Status: resigilat");
		}
		else {
			System.out.println("Status: stoc epuizat");
		}

		System.out.println("URL: " + this.url);
		System.out.println("Img: " + this.img);
	}
}
