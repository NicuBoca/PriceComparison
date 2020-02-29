package com.pricescrapper.repository;

import com.pricescrapper.dto.ProductDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends MongoRepository<ProductDTO, Long> {
    ProductDTO insert(ProductDTO product);
}
