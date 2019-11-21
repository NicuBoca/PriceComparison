package com.pricescrapper.rest;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductRestController {

    @RequestMapping(method = RequestMethod.GET, value = "/api/product/{product}")
    public ResponseEntity<List<ProductDto>> getProducts(@PathVariable String product) throws Exception {
        CrawlEngine engine = new CrawlEngine();
        List<ProductDto> productsList = engine.crawl(product);
        return new ResponseEntity<>(productsList, HttpStatus.OK);
    }

}