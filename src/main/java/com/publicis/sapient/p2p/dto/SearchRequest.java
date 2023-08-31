package com.publicis.sapient.p2p.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchRequest {

    int limit = 5;
    int pageNumber = 1;
    String latitude;
    String longitude;
    List<String> offerTypeValue = new ArrayList<>();
    List<String> categoryValue = new ArrayList<>();
    SortBy sortBy = SortBy.valueOf("RELEVANCE");
}
