package com.pricescraper.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.pricescraper.dao.ProductDao;
import com.pricescraper.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    MongoTemplate mongoTemplate;

    final String COLLECTION = "product";

    @Override
    public List<Product> findProductsByNameAndSource(List<Product> productList) {
        List<Product> result = new ArrayList<>();
        for (Product product : productList) {
            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("name").is(product.getName()),
                    Criteria.where("source").is(product.getSource())));
            Product currentProduct = mongoTemplate.findOne(query, Product.class);
            result.add(currentProduct);
        }
        return result;
    }

    @Override
    public void insertProductAndUpdateHistory(Product product) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("name").is(product.getName()),
                Criteria.where("source").is(product.getSource())));
        Update update = new Update();
        update.addToSet("history", product.getHistory().get(0));
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, COLLECTION);
        if (updateResult.getModifiedCount() == 0) {
            mongoTemplate.insert(product);
        }
    }
}
