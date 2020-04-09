package com.pricescraper.service;

import com.pricescraper.filter.ProductMatching;
import com.pricescraper.filter.SimilarityFilter;
import com.pricescraper.model.ProductBase;
import com.pricescraper.repository.ProductRepository;
import com.pricescraper.scrapper.AltexScraper;
import com.pricescraper.scrapper.BaseScraper;
import com.pricescraper.scrapper.EmagScraper;
import com.pricescraper.scrapper.MediaGalaxyScraper;
import com.pricescraper.types.ProductSourceType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void productClustering(String searchProduct) {
        productRepository.deleteAll();

        List<ProductBase> productListUnfiltered = crawl(searchProduct);
        List<ProductBase> productList = SimilarityFilter.getTheMostSimilarProducts(productListUnfiltered, searchProduct);

        /**
         * List1<List2<Pair<Integer, Double>>>
         *      List1 = lista de clustere
         *      List2 = lista de magazine pentru fiecare cluster (nr. elem = nr. magazine)
         *      Pair = perechea (index_produs, precizie) pentru a indentifica elementul din cluster de la magazinul
         *          corespunzator indicelui din List2
         */
        int sourcesSize = ProductSourceType.values().length;
        List<ArrayList<ImmutablePair<Integer, Double>>> clustersPrecision = new ArrayList<>();
        ArrayList<ImmutablePair<Integer, Double>> sourcesPrecision = new ArrayList<>();
        for (int i = 0; i < sourcesSize; i++) {
            sourcesPrecision.add(new ImmutablePair<>(0, (double) 0));
        }
        clustersPrecision.add(sourcesPrecision);

        int clusterCount = 1;
        int i = 0;
        int nextI = i + 1;

        while (i < productList.size() - 1) {
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
                    .set(index1, new ImmutablePair<>(i, (double) 1));

            int j = getNextFreePosition(productList, nextI, productList.size());
            while (j < productList.size() - 1) {
                String source2 = productList.get(j)
                        .getSource()
                        .toString();

                if (!source1.equals(source2)) {
                    double p = ProductMatching.isSameProductByName(productList.get(i), productList.get(j));
                    if (p != 0) {
                        int index2 = ProductSourceType.valueOf(source2)
                                .ordinal();
                        ImmutablePair<Integer, Double> pair = clustersPrecision.get(clusterCount - 1)
                                .get(index2);

                        if (p > pair.getValue()) {
                            productList.get(j)
                                    .getHistory()
                                    .get(0)
                                    .setCluster(clusterCount);

                            clustersPrecision.get(clusterCount - 1)
                                    .set(index2, new ImmutablePair<>(j, p));

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
            ArrayList<ImmutablePair<Integer, Double>> currentSourcesPrecision = new ArrayList<>();
            for (int k = 0; k < sourcesSize; k++) {
                currentSourcesPrecision.add(new ImmutablePair<>(0, (double) 0));
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
        }

        productRepository.saveAll(productList);
        System.out.println("Cluster finish!");
    }

    private List<BaseScraper> initCrawler() {
        List<BaseScraper> crawlJobs = new ArrayList<BaseScraper>();
        crawlJobs.add(new EmagScraper());
        //crawlJobs.add(new PcGarageScraper());
        crawlJobs.add(new AltexScraper());
        crawlJobs.add(new MediaGalaxyScraper());
        return crawlJobs;
    }

    private List<ProductBase> crawl(String searchProduct) {
        List<BaseScraper> crawlJobs = initCrawler();
        List<ProductBase> productList = new ArrayList<>();
        for (BaseScraper scrapper : crawlJobs) {
            productList.addAll(scrapper.getProducts(searchProduct));
        }
        System.out.println("Crawl finish!");
        return productList;
    }

    private int getNextFreePosition(List<ProductBase> productList, int startPosition, int endPosition) {
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
