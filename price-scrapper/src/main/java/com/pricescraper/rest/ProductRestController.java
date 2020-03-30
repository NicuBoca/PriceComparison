package com.pricescraper.rest;

import com.pricescraper.repository.ProductRepository;
import com.pricescraper.model.Product;
import com.pricescraper.service.CrawlerService;
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
    public ResponseEntity<List<Product>> getAllProducts(@PathVariable String product) {
        crawlerService.crawl(product);
        crawlerService.cluster();
        return new ResponseEntity<>(productRepository.findAllByOrderBySimilarityDesc(), HttpStatus.OK);
    }
}