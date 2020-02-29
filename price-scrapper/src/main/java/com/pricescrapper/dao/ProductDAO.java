package com.pricescrapper.dao;

import com.pricescrapper.dto.ProductDTO;

public interface ProductDAO {
   ProductDTO saveProduct(ProductDTO productDTO);
}
