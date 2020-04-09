package com.pricescraper.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Document(collection = "product_cluster")
public class ProductCluster {
    @Id
    private int cluster;
    private String name;
    private String img;
    private double priceMin;
    private double similarity;
    private int nrProducts;
    private ProductDTO[] products;
}
