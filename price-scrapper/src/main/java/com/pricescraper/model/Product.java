package com.pricescraper.model;

import com.pricescraper.types.ProductSourceType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Document(collection = "product")
public class Product {
    @Id
    private String id;
    private String name;
    private ProductSourceType source;
    private String url;
    private String img;
    private List<ProductHistory> history;
}
