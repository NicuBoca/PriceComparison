package com.pricescraper.repository;

import com.pricescraper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, Long>, CrudRepository<Product, Long> {

    List<Product> findBySimilarity(double similarity);

    List<Product> findAllByOrderBySimilarityDesc();

    List<Product> findDistinctByName();

}
