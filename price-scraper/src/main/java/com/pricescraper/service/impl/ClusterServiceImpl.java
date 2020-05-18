package com.pricescraper.service.impl;

import com.pricescraper.dao.ProductDao;
import com.pricescraper.filter.ProductMatching;
import com.pricescraper.model.Product;
import com.pricescraper.model.ProductCluster;
import com.pricescraper.service.ClusterService;
import com.pricescraper.types.ProductSourceType;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@NoArgsConstructor
@Component
@Log
public class ClusterServiceImpl implements ClusterService {

    @Autowired
    private ProductDao productDao;

    private List<ProductCluster> productClusterList;

    @Override
    public void computeProductClusterList(List<Product> productList) {
        int nrClusters = clustering(productList);

        for (Product product : productList) {
            productDao.insertProductAndUpdateHistory(product);
        }
        productClusterList = getProductClusters(productList, nrClusters);
        log.info("Cluster finished!");
    }

    @Override
    public List<ProductCluster> getProductClusterList() {
        return productClusterList;
    }

    @Override
    public ProductCluster getProductClusterById(int id) {
        for(ProductCluster cluster : productClusterList) {
            if(cluster.getId() == id) {
                return cluster;
            }
        }
        return null;
    }

    private int clustering(List<Product> productList) {
        /**
         * List1<List2<Pair<Integer, Double>>>
         *      List1 = lista de clustere
         *      List2 = lista de magazine pentru fiecare cluster (nr. elem = nr. magazine)
         *      Pair = perechea (index_produs, precizie) pentru a indentifica elementul din cluster de la magazinul
         *          corespunzator indicelui din List2
         */

        int sourcesSize = ProductSourceType.values().length;
        List<ArrayList<Map.Entry<Integer, Double>>> clustersPrecision = new ArrayList<>();
        ArrayList<Map.Entry<Integer, Double>> sourcesPrecision = new ArrayList<>();
        for (int i = 0; i < sourcesSize; i++) {
            sourcesPrecision.add(new AbstractMap.SimpleEntry<>(0, (double) 0));
        }
        clustersPrecision.add(sourcesPrecision);

        int clusterCount = 1;
        int i = 0;

        while (i < productList.size() - 1) {
            // setez clusterul curent cu un element de referinta
            productList.get(i)
                    .getHistory()
                    .get(0)
                    .setCluster(clusterCount);
            String source1 = productList.get(i)
                    .getSource()
                    .toString();
            int index1 = ProductSourceType.valueOf(source1)
                    .ordinal();
            clustersPrecision.get(clusterCount - 1)
                    .set(index1, new AbstractMap.SimpleEntry<>(i, (double) 1));

            // caut urmatoarea pozitie libera (fara cluster setat) in lista de produse
            int j = getNextFreePosition(productList, 0, productList.size());
            while (j < productList.size() - 1) {
                String source2 = productList.get(j)
                        .getSource()
                        .toString();

                // verific daca cele doua produse de comparat au sursa diferita
                if (!source1.equals(source2)) {
                    double p = ProductMatching.getSimilarityForProductMatching(productList.get(i), productList.get(j));
                    // daca cele doua produse au procentul de similaritate peste pragul setat
                    if (p != 0) {
                        int index2 = ProductSourceType.valueOf(source2)
                                .ordinal();
                        Map.Entry<Integer, Double> pair = clustersPrecision.get(clusterCount - 1)
                                .get(index2);

                        if (p > pair.getValue()) {
                            productList.get(j)
                                    .getHistory()
                                    .get(0)
                                    .setCluster(clusterCount);

                            clustersPrecision.get(clusterCount - 1)
                                    .set(index2, new AbstractMap.SimpleEntry<>(j, p));

                            if (pair.getValue() != 0) {
                                productList.get(pair.getKey())
                                        .getHistory()
                                        .get(0)
                                        .setCluster(0);
                            }
                        }
                    }
                }
                j = getNextFreePosition(productList, j + 1, productList.size());
            }
            i = getNextFreePosition(productList, 0, productList.size());
            clusterCount++;
            ArrayList<Map.Entry<Integer, Double>> currentSourcesPrecision = new ArrayList<>();
            for (int k = 0; k < sourcesSize; k++) {
                currentSourcesPrecision.add(new AbstractMap.SimpleEntry<>(0, (double) 0));
            }
            clustersPrecision.add(currentSourcesPrecision);
        }
        if (productList.get(productList.size() - 1)
                .getHistory()
                .get(0)
                .getCluster() == 0) {
            productList.get(productList.size() - 1)
                    .getHistory()
                    .get(0)
                    .setCluster(clusterCount);
            clusterCount++;
        }
        return clusterCount;
    }

    private List<ProductCluster> getProductClusters(List<Product> productList, int nrClusters) {
        List<Product> productListAfterMerge = productDao.findProductsByNameAndSource(productList);
        List<ProductCluster> productClusterList = new ArrayList<>();
        boolean isSet;

        for (int k = 1; k < nrClusters; k++) {
            ProductCluster productCluster = new ProductCluster();
            List<Product> productsFromCluster = new ArrayList<>();
            productCluster.setId(k);
            isSet = false;
            for (int i=0; i<productListAfterMerge.size(); i++) {
                if (productListAfterMerge.get(i).getHistory().get(productListAfterMerge.get(i).getHistory().size() - 1).getCluster() == k) {
                    if (!isSet) {
                        productCluster.setName(productListAfterMerge.get(i).getName());
                        productCluster.setImg(productListAfterMerge.get(i).getImg());
                        isSet = true;
                    }
                    productsFromCluster.add(productListAfterMerge.get(i));
                }
            }

            if(productsFromCluster.size() != 0) {
                Product prodMax = Collections.max(productsFromCluster, Comparator.comparing(p -> p.getHistory().get(p.getHistory().size() - 1).getPrice()));
                Product prodMin = Collections.min(productsFromCluster, Comparator.comparing(p -> p.getHistory().get(p.getHistory().size() - 1).getPrice()));

                productCluster.setPriceMax(prodMax.getHistory().get(prodMax.getHistory().size() - 1).getPrice());
                productCluster.setPriceMin(prodMin.getHistory().get(prodMin.getHistory().size() - 1).getPrice());

                productCluster.setNrProducts(productsFromCluster.size());
                productCluster.setProducts(productsFromCluster);

                productClusterList.add(productCluster);
            }
        }
        return productClusterList;
    }

    private int getNextFreePosition(List<Product> productList, int startPosition, int endPosition) {
        int freePosition = endPosition - 1;
        for (int i = startPosition; i < endPosition; i++) {
            if (productList.get(i)
                    .getHistory()
                    .get(0)
                    .getCluster() == 0) {
                freePosition = i;
                break;
            }
        }
        return freePosition;
    }
}
