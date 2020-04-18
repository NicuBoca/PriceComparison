package com.pricescraper.dao;

import com.pricescraper.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAllProducts();

    List<Product> findProductsByNameAndSource(List<Product> productList);

    void insertProductAndUpdateHistory(Product product);
}
