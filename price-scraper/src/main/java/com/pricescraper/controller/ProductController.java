package com.pricescraper.controller;

import com.pricescraper.dao.ProductDao;
import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;
import com.pricescraper.service.ClusterService;
import com.pricescraper.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    ProductDao productDao;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private ClusterService clusterService;

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(method = RequestMethod.GET, value = "/api/product/{product}")
    public ResponseEntity<List<ProductCluster>> getProducts(@PathVariable String product) {
        List<Product> productList = crawlerService.getProductList(product);
        List<ProductCluster> productClusterList = clusterService.getProductClusterList(productList);
        return new ResponseEntity<>(productClusterList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public String getProducts(@RequestParam String product, Model model) {
        List<Product> productList = crawlerService.getProductList(product);
        List<ProductCluster> productClusterList = clusterService.getProductClusterList(productList);
        model.addAttribute("productsCluster", productClusterList);
        return "products_cluster";
    }
}