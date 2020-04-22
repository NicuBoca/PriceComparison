package com.pricescraper.service;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;

import java.util.List;

public interface ClusterService {
    void computeProductClusterList(List<Product> productList);

    List<ProductCluster> getProductClusterList();

    ProductCluster getProductClusterById(int id);
}
