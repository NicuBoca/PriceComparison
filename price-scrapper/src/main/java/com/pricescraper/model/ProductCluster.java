package com.pricescraper.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class ProductCluster {
    private int cluster;
    private String name;
    private String img;
    private double priceMin;
    private double priceMax;
    private int nrProducts;
    private List<Product> products;
}
