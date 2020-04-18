package com.pricescraper.service;

import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;

import java.util.List;

public interface ClusterService {
    List<ProductCluster> productClustering(List<Product> productList);
}
