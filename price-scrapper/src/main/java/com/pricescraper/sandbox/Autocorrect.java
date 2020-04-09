package com.pricescraper.sandbox;

public class Autocorrect {

//    public List<ProductDto> similarityFilter(List<ProductDto> products, String searchProduct) {
//        List<ProductDto> filteredProducts = new ArrayList<ProductDto>();
//        String[] arrOfSearchName = searchProduct.split(" ");
//        int arrSearchNameLength = arrOfSearchName.length;
//        double similarityRate[] = new double[arrSearchNameLength];
//        String similarString[] = new String[arrSearchNameLength];
//        int prodListLength = products.size();
//        double avgSimilarity[] = new double[prodListLength];
//        String suggestion = "";
//
//        int counter = 0;
//        for (ProductDto product : products) {
//            avgSimilarity[counter] = avgSearchNameMatching(product.getName(), arrOfSearchName, similarityRate, similarString);
//            counter++;
//        }
//
//        double maxAvgSimilarity = Arrays.stream(avgSimilarity).max().getAsDouble();
//        for(int i=0; i<prodListLength; i++) {
//            if(avgSimilarity[i] == maxAvgSimilarity) {
//                filteredProducts.add(products.get(i));
//            }
//        }
//
//        if(maxAvgSimilarity!=1) {
//            for(int i=0; i<arrSearchNameLength; i++) {
//                suggestion = suggestion + " " + similarString[i];
//            }
//            System.out.println("Ati vrut sa scrieti: \"" + suggestion + " \" ?");
//        }
//        return filteredProducts;
//    }
//
//    private double avgSearchNameMatching(String productName, String[] arrOfSearchName, double similarityRate[], String similarString[]) {
//        JaccardSimilarity js = new JaccardSimilarity();
//        productName = productName.toLowerCase();
//        String[] arrOfProductName = productName.split(" ");
//        int arrSearchNameLength = arrOfSearchName.length;
//        int arrProductNameLength = arrOfProductName.length;
//        double currentSimilarityRate[] = new double[arrSearchNameLength];
//
//        for (int i = 0; i < arrSearchNameLength; i++) {
//            for (int j = 0; j < arrProductNameLength; j++) {
//                double similarity = js.apply(arrOfSearchName[i], arrOfProductName[j]);
//                if (similarity > currentSimilarityRate[i]) {
//                    currentSimilarityRate[i] = similarity;
//                    if (currentSimilarityRate[i] > similarityRate[i]) {
//                        similarityRate[i] = similarity;
//                        similarString[i] = arrOfProductName[j];
//                    }
//                }
//            }
//        }
//
//        double sum=0;
//        for (int k = 0; k < arrSearchNameLength; k++) {
//            sum += currentSimilarityRate[k];
//        }
//        double avgSimilarity = sum/arrSearchNameLength;
//        return avgSimilarity;
//    }

}
