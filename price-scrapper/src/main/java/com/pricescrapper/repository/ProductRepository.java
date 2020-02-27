package com.pricescrapper.repository;

import com.pricescrapper.dto.ProductDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProductRepository extends MongoRepository<ProductDto, String>, QuerydslPredicateExecutor<ProductDto> {
}
