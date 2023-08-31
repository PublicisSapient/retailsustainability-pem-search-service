package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.dto.SearchRequest;
import com.publicis.sapient.p2p.dto.SearchResponse;

import java.util.List;

public interface SearchService {

    SearchResponse getProducts(String query, SearchRequest searchRequest);

    List<String> getSuggestions(String query, String latitude, String longitude);
}
