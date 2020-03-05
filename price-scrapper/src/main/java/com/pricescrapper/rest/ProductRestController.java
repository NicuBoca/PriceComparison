package com.pricescrapper.rest;

import com.pricescrapper.repository.ProductRepository;
import com.pricescrapper.dto.ProductDTO;
import com.pricescrapper.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductRestController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    private CrawlerService crawlerService;

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(method = RequestMethod.GET, value = "/api/product/{product}")
    public ResponseEntity<List<ProductDTO>> getAllProducts(@PathVariable String product) {
        crawlerService.crawl(product);
        return new ResponseEntity<>(productRepository.findAllByOrderBySimilarityDesc(), HttpStatus.OK);
    }
}