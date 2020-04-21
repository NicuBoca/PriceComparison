package com.pricescraper.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class ProductHistory {
    private double price;
    private String date;
    private int stock;
    private int cluster;

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
