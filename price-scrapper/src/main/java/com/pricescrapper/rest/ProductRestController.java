package com.pricescrapper.rest;

import com.pricescrapper.repository.ProductRepository;
import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductRestController {
    @Autowired
    ProductRepository repository;

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(method = RequestMethod.GET, value = "/api/product/{product}")
    public ResponseEntity<List<ProductDTO>> getProducts(@PathVariable String product) throws Exception {
        CrawlEngine engine = new CrawlEngine();
        List<ProductDTO> productsList = engine.crawl(product);
        //repository.saveAll(productsList);
        return new ResponseEntity<>(productsList, HttpStatus.OK);
    }
}