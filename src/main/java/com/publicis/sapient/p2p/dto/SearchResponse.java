package com.publicis.sapient.p2p.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    List<Product> products;
    List<String> offerTypeListings;
    List<String> categoryListings;
    long noOfProducts;
}
