package com.pricescraper.rest;

import com.pricescraper.dao.ProductDao;
import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;
import com.pricescraper.service.ClusterService;
import com.pricescraper.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductRestController {
    @Autowired
    ProductDao productDao;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private ClusterService clusterService;

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(method = RequestMethod.GET, value = "/api/product/{product}")
    public ResponseEntity<List<ProductCluster>> getProducts(@PathVariable String product) {
        List<Product> productList = crawlerService.crawl(product);
        List<ProductCluster> productClusterList = clusterService.productClustering(productList);
        return new ResponseEntity<>(productClusterList, HttpStatus.OK);
    }
}