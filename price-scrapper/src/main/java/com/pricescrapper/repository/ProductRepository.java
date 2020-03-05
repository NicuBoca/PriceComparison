package com.pricescrapper.repository;

import com.pricescrapper.dto.ProductDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductDTO, Long>, CrudRepository<ProductDTO, Long> {

    List<ProductDTO> findBySimilarity(double similarity);

    List<ProductDTO> findAllByOrderBySimilarityDesc();

    List<ProductDTO> findDistinctByName();

}
