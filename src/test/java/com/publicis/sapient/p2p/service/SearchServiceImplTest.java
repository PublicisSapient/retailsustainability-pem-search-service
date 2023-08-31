package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.dto.Product;
import com.publicis.sapient.p2p.dto.SearchRequest;
import com.publicis.sapient.p2p.dto.SearchResponse;
import com.publicis.sapient.p2p.dto.SortBy;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.exception.util.ErrorResolver;
import com.publicis.sapient.p2p.util.ProductMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {SearchServiceImpl.class})
@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {

    @Autowired
    SearchServiceImpl searchService;

    @MockBean
    SolrClient solrClient;

    @MockBean
    ErrorResolver errorResolver;

    @Test
    @Order(1)
    void getProductsTest_NoQuery() throws Exception {

        String query = null;
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setLimit(10);
        searchRequest.setPageNumber(1);
        searchRequest.setLatitude("12.12");
        searchRequest.setLongitude("12.12");
        searchRequest.setCategoryValue(List.of("book","toy"));
        searchRequest.setOfferTypeValue(List.of("type1","type2"));
        searchRequest.setSortBy(SortBy.valueOf("DISTANCE"));

        SolrDocumentList solrDocumentList = new SolrDocumentList();

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocumentList);

        SolrDocument doc = new SolrDocument();
        doc.addField("id", "123");
        doc.addField("name", new ArrayList<String>() {{
            add("Product Name");
        }});
        doc.addField("description", new ArrayList<String>() {{
            add("description");
        }});
        doc.addField("category", new ArrayList<String>() {{
            add("CLOTH");
        }});
        doc.addField("offerType", new ArrayList<String>() {{
            add("SELL");
        }});
        doc.addField("images",new ArrayList<String>() {{
            add("image1.jpg");
            add("image2.jpg");
        }});
        doc.addField("user", new ArrayList<String>() {{
            add("2345");
        }});
        doc.addField("location", new ArrayList<String>() {{
            add("Location");
        }});
        doc.addField("geoLocation", "37.56,-122.434");
        doc.addField("createdTime", new ArrayList<Date>() {{
            add(new Date());
        }});
        doc.addField("price", 0);
        solrDocumentList.add(doc);

        SearchResponse result = searchService.getProducts(query, searchRequest);

        verify(solrClient, times(3)).query(any(SolrQuery.class));
    }

    @Test
    @Order(2)
    void getProductsTest_WithQuery() throws Exception {

        String query = "text";
        SearchRequest searchRequest = new SearchRequest();

        SolrDocumentList solrDocumentList = new SolrDocumentList();

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocumentList);

        SearchResponse result = searchService.getProducts(query, searchRequest);

        assertEquals(Collections.emptyList(), result.getProducts());

        verify(solrClient, times(3)).query(any(SolrQuery.class));
    }

    @Test
    @Order(3)
    void getProductsTest_WithException() throws Exception {

        String query = "text";
        SearchRequest searchRequest1 = new SearchRequest();
        searchRequest1.setSortBy(SortBy.valueOf("PRICE_LOW_HIGH"));
        searchRequest1.setLatitude("37.56");

        SearchRequest searchRequest2 = new SearchRequest();
        searchRequest2.setSortBy(SortBy.valueOf("PRICE_HIGH_LOW"));

        SearchRequest searchRequest3 = new SearchRequest();
        searchRequest3.setSortBy(SortBy.valueOf("DATE"));

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);

        BusinessException ex1 = Assertions.assertThrows(BusinessException.class, () -> searchService.getProducts(query, searchRequest1));
        BusinessException ex2 = Assertions.assertThrows(BusinessException.class, () -> searchService.getProducts(query, searchRequest2));
        BusinessException ex3 = Assertions.assertThrows(BusinessException.class, () -> searchService.getProducts(query, searchRequest3));
        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex1.getErrorCode());
        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex2.getErrorCode());
        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex3.getErrorCode());
    }

    @Test
    @Order(4)
    void getFacetValuesTest() throws Exception {

        SolrQuery solrQuery = new SolrQuery();

        FacetField facetField = new FacetField("field");
        facetField.add("Facet1", 3L); // Add facet values
        facetField.add("Facet2", 0L);
        facetField.add("Facet3", 2L);

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getFacetField(any(String.class))).thenReturn(facetField);

        List<String> result = searchService.getFacetValues(solrQuery, "field");

        assertEquals(Arrays.asList("Facet1", "Facet3"), result);

        verify(solrClient).query(any(SolrQuery.class));
    }

    @Test
    @Order(5)
    void getFacetValuesTest_WithException() throws Exception {

        SolrQuery solrQuery = new SolrQuery("*:*");

        when(solrClient.query(any(SolrQuery.class))).thenThrow(new SolrServerException("exception"));

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> searchService.getFacetValues(solrQuery, "field"));

        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex.getErrorCode());
    }

    @Test
    @Order(5)
    void getSuggestionsTest() throws Exception{

        QueryResponse queryResponse = mock(QueryResponse.class);
        SuggesterResponse suggesterResponse = mock(SuggesterResponse.class);

        List<String> suggestedTerms = List.of("suggestion1", "suggestion2");
        Map<String, List<String>> suggestedTermMap = new HashMap<>();
        suggestedTermMap.put("mySuggester", suggestedTerms);

        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getSuggesterResponse()).thenReturn(suggesterResponse);
        when(suggesterResponse.getSuggestedTerms()).thenReturn(suggestedTermMap);

        SolrDocumentList solrDocumentList = mock(SolrDocumentList.class);
        when(queryResponse.getResults()).thenReturn(solrDocumentList);
        when(solrDocumentList.getNumFound()).thenReturn(2L);

        List<String> suggestions = searchService.getSuggestions("keyword", null, null);
        List<String> suggestions1 = searchService.getSuggestions("keyword", "lat", null);
        List<String> suggestions2 = searchService.getSuggestions("keyword", null, "lon");
        List<String> suggestions3 = searchService.getSuggestions("keyword", "lat", "lon");

        assertEquals(suggestedTerms, suggestions);
        assertEquals(suggestedTerms, suggestions1);
        assertEquals(suggestedTerms, suggestions2);
        assertEquals(suggestedTerms, suggestions3);

        verify(solrClient, times(6)).query(any(SolrQuery.class));
    }

    @Test
    @Order(6)
    void getSuggestionsTest_WithException() throws Exception {

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> searchService.getSuggestions("keyword", "lat", "lon"));

        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex.getErrorCode());
    }

    @Test
    @Order(7)
    void getFilteredSuggestionsTest() throws Exception{

        QueryResponse queryResponse = mock(QueryResponse.class);
        List<String> suggestions = List.of("suggestion1", "suggestion2");
        SolrDocumentList solrDocumentList = mock(SolrDocumentList.class);

        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocumentList);
        when(solrDocumentList.getNumFound()).thenReturn(0L);

        List<String> result = searchService.getFilteredSuggestions(suggestions, "37.56", "-122.434");

        assertEquals(Collections.emptyList(),result);
        assertEquals(0, result.size());

        verify(solrClient, times(2)).query(any(SolrQuery.class));
    }

    @Test
    @Order(8)
    void getFilteredSuggestionsTest_WithException() throws Exception {

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(solrClient.query(any(SolrQuery.class))).thenReturn(queryResponse);

        List<String> keyword = List.of("keyword");

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> searchService.getFilteredSuggestions(keyword, "lat", "lon"));

        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex.getErrorCode());
    }
}
