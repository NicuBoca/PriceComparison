package com.pricescraper.dao;

import com.pricescraper.model.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class ProductDaoImpl implements ProductDao {

    @Autowired
    MongoTemplate mongoTemplate;

    final String COLLECTION = "products";

    @Override
    public void saveProduct(ProductDTO product) {
        mongoTemplate.save(product);
    }
}
