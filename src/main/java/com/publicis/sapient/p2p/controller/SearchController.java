package com.publicis.sapient.p2p.controller;

import com.publicis.sapient.p2p.dto.SearchRequest;
import com.publicis.sapient.p2p.dto.SearchResponse;
import com.publicis.sapient.p2p.dto.ServiceResponseDto;
import com.publicis.sapient.p2p.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    SearchService searchService;

    @PostMapping
    public ServiceResponseDto getProducts(@RequestParam(required = false) String query, @RequestBody SearchRequest searchRequest) {

        logger.info("Entered Search Controller getProducts");
        SearchResponse searchResponse =  searchService.getProducts(query, searchRequest);
        String message = searchResponse.getProducts().isEmpty() ? "No Products Found" : "Products Fetched Successfully";
        return new ServiceResponseDto(200, message, searchResponse);
    }

    @GetMapping
    public ResponseEntity<ServiceResponseDto> getSuggestions(@RequestParam String query, @RequestParam(required = false) String latitude, @RequestParam(required = false) String longitude) {

        logger.info("Entered Search Controller getSuggestions");
        List<String> suggestions =  searchService.getSuggestions(query, latitude, longitude);
        String message = suggestions.isEmpty() ? "No Suggestions Found" : "Suggestions Found";
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(new ServiceResponseDto(200, message, suggestions));
    }
}
