package com.pricescraper.controller;

import com.pricescraper.dao.ProductDao;
import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;
import com.pricescraper.model.ProductList;
import com.pricescraper.service.ClusterService;
import com.pricescraper.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
        List<Product> productList = crawlerService.getProductList(product);
        List<ProductCluster> productClusterList = clusterService.getProductClusterList(productList);
        model.addAttribute("clusters", productClusterList);
        return "clusters_view";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/clusters/products")
//    public String getProducts(@ModelAttribute("productList") ProductList productList, BindingResult result, Model model) {
    public String getProducts(@ModelAttribute("productList") ProductList productList, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "error";
        }
        model.addAttribute("products", productList.getProducts());
        return "products_view";
    }

    @RequestMapping(value = "product/{prodId}", method = RequestMethod.GET)
    public ModelAndView showProductCluster(@PathVariable("prodId") String prodId) {
        return null;
    }
}