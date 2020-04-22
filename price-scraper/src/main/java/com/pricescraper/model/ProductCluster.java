package com.pricescraper.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ProductCluster {
    private int id;
    private String name;
    private String img;
    private double priceMin;
    private double priceMax;
    private int nrProducts;
    private List<Product> products;
}
