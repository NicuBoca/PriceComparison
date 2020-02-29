package com.pricescrapper.dao;

import com.pricescrapper.dto.ProductDTO;
import com.pricescrapper.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAOImpl implements ProductDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProductDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO){
        return mongoTemplate.save(productDTO);
    }
}
