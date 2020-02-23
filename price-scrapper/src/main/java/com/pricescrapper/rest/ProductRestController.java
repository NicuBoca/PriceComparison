package com.pricescrapper.rest;

import com.pricescrapper.crawler.CrawlEngine;
import com.pricescrapper.dto.ProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductRestController {

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(method = RequestMethod.GET, value = "/api/product/{product}")
    public ResponseEntity<List<ProductDto>> getProducts(@PathVariable String product) throws Exception {
        CrawlEngine engine = new CrawlEngine();
        List<ProductDto> productsList = engine.crawl(product);
        return new ResponseEntity<>(productsList, HttpStatus.OK);
    }

}