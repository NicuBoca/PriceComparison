package com.pricescraper.repository;

import com.pricescraper.model.ProductBase;
import com.pricescraper.model.ProductDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductBase, Long>, CrudRepository<ProductBase, Long> {

//    List<ProductDTO> findBySimilarity(double similarity);
//
//    List<ProductDTO> findAllByOrderBySimilarityDesc();

    List<ProductDTO> findDistinctByName();

    List<ProductBase> findBy();

}
