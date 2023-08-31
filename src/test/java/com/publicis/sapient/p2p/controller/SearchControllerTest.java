package com.publicis.sapient.p2p.controller;

import com.publicis.sapient.p2p.dto.Product;
import com.publicis.sapient.p2p.dto.SearchRequest;
import com.publicis.sapient.p2p.dto.SearchResponse;
import com.publicis.sapient.p2p.dto.ServiceResponseDto;
import com.publicis.sapient.p2p.service.SearchService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {SearchController.class})
@ExtendWith(SpringExtension.class)
class SearchControllerTest {

    @MockBean
    SearchService searchService;

    @Autowired
    SearchController searchController;

    @Test
    @Order(1)
    void getProductsTest_NoProductsFound() {

        String query = "some query";
        SearchRequest searchRequest = new SearchRequest();
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProducts(Collections.emptyList());

        when(searchService.getProducts(query, searchRequest)).thenReturn(searchResponse);
        ServiceResponseDto result = searchController.getProducts(query, searchRequest);

        assertEquals(200, result.getStatusCode());
        assertEquals("No Products Found", result.getMessage());
        assertEquals(searchResponse, result.getData());

        verify(searchService).getProducts(query, searchRequest);
    }

    @Test
    @Order(2)
    void getProductsTest_ProductsFound() {

        String query = "some query";
        SearchRequest searchRequest = new SearchRequest();
        List<Product> products = Arrays.asList(new Product(), new Product());
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProducts(products);

        when(searchService.getProducts(query, searchRequest)).thenReturn(searchResponse);
        ServiceResponseDto result = searchController.getProducts(query, searchRequest);

        assertEquals(200, result.getStatusCode());
        assertEquals("Products Fetched Successfully", result.getMessage());
        assertEquals(searchResponse, result.getData());

        verify(searchService).getProducts(query, searchRequest);
    }

    @Test
    @Order(3)
    void getSuggestionsTest_NoProductsFound() {

        String query = "some query";
        List<String> suggestions = Collections.emptyList();

        when(searchService.getSuggestions(query, null, null)).thenReturn(suggestions);
        ResponseEntity<ServiceResponseDto> response = searchController.getSuggestions(query, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No Suggestions Found", response.getBody().getMessage());
        assertEquals(suggestions, response.getBody().getData());

        verify(searchService).getSuggestions(query, null, null);
    }

    @Test
    @Order(4)
    void getSuggestionsTest_ProductsFound() {

        String query = "some query";
        List<String> suggestions = Arrays.asList("suggestion1", "suggestion2");

        when(searchService.getSuggestions(query, null, null)).thenReturn(suggestions);
        ResponseEntity<ServiceResponseDto> response = searchController.getSuggestions(query, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Suggestions Found", response.getBody().getMessage());
        assertEquals(suggestions, response.getBody().getData());

        verify(searchService).getSuggestions(query, null, null);
    }
}
