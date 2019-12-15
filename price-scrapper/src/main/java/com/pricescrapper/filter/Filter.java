package com.pricescrapper.filter;

import com.pricescrapper.dto.ProductDto;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    public static List<ProductDto> filter(List<ProductDto> products, String searchProduct) {
        List<ProductDto> filteredProducts = new ArrayList<ProductDto>();
        for (ProductDto product : products) {
            if (!isSearchNameMatcing(product.getName(), searchProduct)) {
                continue;
            }
            filteredProducts.add(product);
        }
       return filteredProducts;
    }

    public static boolean isSearchNameMatcing(String productName, String searchName) {
        JaccardSimilarity js = new JaccardSimilarity();
        boolean result = true;

        productName = productName.toLowerCase();
        searchName = searchName.toLowerCase();
        String[] arrOfStr1 = searchName.split(" ");
        String[] arrOfStr2 = productName.split(" ");
        double sim[] = new double[arrOfStr1.length];
        String simStr[] = new String[arrOfStr1.length];

        for (int i = 0; i < arrOfStr1.length; i++) {
            for (int j = 0; j < arrOfStr2.length; j++) {
                double p = js.apply(arrOfStr1[i], arrOfStr2[j]);
                if (p > sim[i]) {
                    sim[i] = p;
                    simStr[i] = arrOfStr2[j];
                }
            }
        }

        double s=0;
        for (int k = 0; k < arrOfStr1.length; k++) {
            s += sim[k];
        }
        double medie = s/arrOfStr1.length;
        if(medie < 0.7) {
            result = false;
        }

        return result;
    }
}
