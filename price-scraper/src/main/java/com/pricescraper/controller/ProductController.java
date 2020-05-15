package com.pricescraper.controller;

import com.pricescraper.dao.ProductDao;
import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;
import com.pricescraper.service.ClusterService;
import com.pricescraper.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    ProductDao productDao;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private ClusterService clusterService;

    @RequestMapping(method = RequestMethod.GET, value = "/clusters")
    public String getClusters(@RequestParam String product, Model model) throws IOException {
        List<Product> productList = crawlerService.getProductList(product.toLowerCase());
        if (productList != null) {
            clusterService.computeProductClusterList(productList);
            List<ProductCluster> productClusterList = clusterService.getProductClusterList();
            model.addAttribute("clusters", productClusterList);
            return "clusters_view";
        } else {
            return "no_result";
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clusters/products/{id}")
    public String getProducts(@PathVariable("id") Integer clusterId, Model model) {
        ProductCluster cluster = clusterService.getProductClusterById(clusterId);
        model.addAttribute("cluster", cluster);
        return "products_view";
    }
}