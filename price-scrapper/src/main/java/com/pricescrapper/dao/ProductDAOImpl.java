package com.pricescrapper.dao;

import com.pricescrapper.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class ProductDAOImpl implements ProductDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProductDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void insertProduct(ProductDTO product) {
        mongoTemplate.insert(product);
    }
}
