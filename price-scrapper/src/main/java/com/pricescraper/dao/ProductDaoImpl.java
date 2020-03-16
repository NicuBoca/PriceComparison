package com.pricescraper.dao;

import com.pricescraper.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class ProductDaoImpl implements ProductDao {

    @Autowired
    MongoTemplate mongoTemplate;

    final String COLLECTION = "products";

    @Override
    public void saveProduct(Product product) {
        mongoTemplate.save(product);
    }
}
