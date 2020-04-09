package com.pricescraper.dao;

import com.pricescraper.model.ProductDTO;

public interface ProductDao {
    void saveProduct(ProductDTO product);
}
