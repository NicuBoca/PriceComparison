package com.pricescrapper.repository;

import com.pricescrapper.dto.ProductDTO;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductDTO, Long> {

}
