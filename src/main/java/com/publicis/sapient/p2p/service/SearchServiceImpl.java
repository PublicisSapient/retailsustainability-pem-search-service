package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.dto.Product;
import com.publicis.sapient.p2p.dto.SearchRequest;
import com.publicis.sapient.p2p.dto.SearchResponse;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.util.ProductMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService{

    private final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    private static final String ERR_SERVICE = "Error from Solr Server";
    private static final String ERR_MESSAGE = "Error while fetching data from Solr : {0} : {1}";
    @Autowired
    SolrClient solr;

    @Value("${distance}")
    String distance;

    @Override
    public SearchResponse getProducts(String query, SearchRequest searchRequest) {

        logger.info("Entered Search Service getProducts");
        SolrDocumentList docs;
        List<Product> productList = new ArrayList<>();
        List<String> offerTypeFacets;
        List<String> categoryFacets;
        long noOfProducts;
        try {
            SolrQuery searchQuery = createQuery(query, searchRequest);

            if (searchRequest.getLatitude() != null && searchRequest.getLongitude() != null) {
                searchQuery.set("fq", "{!geofilt sfield=geoLocation pt=" + searchRequest.getLatitude() + "," + searchRequest.getLongitude() + " d="+distance+"}");
            }
            searchQuery.setFacet(true);
            searchQuery.addFacetField("category");
            searchQuery.addFacetField("offerType");
            if (!searchRequest.getOfferTypeValue().isEmpty()) {
                List<String> offerTypes = searchRequest.getOfferTypeValue();
                StringBuilder q = new StringBuilder(offerTypes.get(0));
                for(int i=1; i<offerTypes.size(); i++) {
                    q.append(" OR ");
                    q.append(offerTypes.get(i));
                }
                searchQuery.addFilterQuery("offerType:("+q+")");
            }
            categoryFacets = getFacetValues(searchQuery, "category");
            Collections.sort(categoryFacets);
            logger.info("category: {}",categoryFacets);
            if (!searchRequest.getCategoryValue().isEmpty()) {
                List<String> categories = searchRequest.getCategoryValue();
                StringBuilder q = new StringBuilder(categories.get(0));
                for(int i=1; i<categories.size(); i++) {
                    q.append(" OR ");
                    q.append(categories.get(i));
                }
                searchQuery.addFilterQuery("category:("+q+")");
            }
            offerTypeFacets = getFacetValues(searchQuery, "offerType");
            Collections.sort(offerTypeFacets);
            logger.info("offer type: {}",offerTypeFacets);
            if(searchRequest.getSortBy().getValue() == 2) {
                searchQuery.set("sort", "price asc");
            }
            else if(searchRequest.getSortBy().getValue() == 3) {
                searchQuery.set("sort", "price desc");
            }
            else if(searchRequest.getSortBy().getValue() == 1) {
                searchQuery.set("sort", "createdTime desc");
            }
            else if(searchRequest.getSortBy().getValue() == 4) {
                searchQuery.set("sort", "geodist(geoLocation,"+ searchRequest.getLatitude() + "," + searchRequest.getLongitude() +") asc");
            }
            else {
                searchQuery.set("sort", "score desc");
            }
            QueryResponse queryResponse = solr.query(searchQuery);
            docs = queryResponse.getResults();
            logger.info("Products fetched...");
            noOfProducts = queryResponse.getResults().getNumFound();
            for(SolrDocument doc : docs) {
                productList.add(ProductMapper.mapToProduct(doc));
            }
        }
        catch (Exception e) {
            logger.error(MessageFormat.format(ERR_MESSAGE, e.getMessage(), e.getClass()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, ERR_SERVICE);
        }
        return new SearchResponse(productList, offerTypeFacets, categoryFacets, noOfProducts);
    }

    public SolrQuery createQuery(String query, SearchRequest searchRequest) {

        SolrQuery searchQuery = new SolrQuery();
        if (query == null) {
            searchQuery.setQuery("*:*");
        } else {
            searchQuery.setQuery("*"+query+"*");
            searchQuery.set("defType", "edismax");
            searchQuery.set("qf", "name^2.0 description^1.5 category offerType");
        }
        int start = searchRequest.getLimit() * (searchRequest.getPageNumber() - 1);
        searchQuery.setStart(start);
        searchQuery.setRows(searchRequest.getLimit());
        logger.info("Query created...");
        return searchQuery;
    }

    public List<String> getFacetValues(SolrQuery query, String field) {

        logger.info("Entered getFacetValues");
        List<String> result = new ArrayList<>();
        try {
            QueryResponse queryResponse = solr.query(query);
            FacetField facetField = queryResponse.getFacetField(field);
            if (facetField != null) {
                List<FacetField.Count> facetCounts = facetField.getValues();
                for (FacetField.Count facetCount : facetCounts) {
                    if (facetCount.getCount() > 0) {
                        result.add(facetCount.getName());
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(MessageFormat.format(ERR_MESSAGE, e.getMessage(), e.getClass()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, ERR_SERVICE);
        }
        return result;
    }

    public List<String> getSuggestions(String keyword, String latitude, String longitude) {

        logger.info("Entered Search Service getSuggestions");
        List<String> suggestions;
        try {
            SolrQuery query = new SolrQuery();
            query.setRequestHandler("/suggest");
            query.set("suggest.q", keyword);
            query.set("suggest", true);
            query.set("suggest.dictionary", "mySuggester");
            query.set("suggest.count", 5);
            query.set("wt", "json");
            QueryResponse response = solr.query(query);
            List<String> list = new ArrayList<>(new HashSet<>(response.getSuggesterResponse().getSuggestedTerms().get("mySuggester")));
            suggestions = latitude!=null && longitude!=null ? getFilteredSuggestions(list, latitude, longitude) : list;
        }
        catch (Exception e) {
            logger.error(MessageFormat.format(ERR_MESSAGE, e.getMessage(), e.getClass()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, ERR_SERVICE);
        }
        logger.info("Suggestions: {}",suggestions);
        return suggestions;
    }

    public List<String> getFilteredSuggestions(List<String> suggestions, String latitude, String longitude) {

        logger.info("Entered getFilteredSuggestions");
        List<String> filteredSuggestions = new ArrayList<>();
        try {
            for (String suggestion : suggestions) {
                SolrQuery query = new SolrQuery();
                query.set("q", "name:" + suggestion);
                query.set("fq", "{!geofilt sfield=geoLocation pt=" + latitude + "," + longitude + " d="+distance+"}");
                QueryResponse response = solr.query(query);
                if(response.getResults().getNumFound() > 0) {
                    filteredSuggestions.add(suggestion);
                }
            }
        }
        catch (Exception e) {
            logger.error(MessageFormat.format(ERR_MESSAGE, e.getMessage(), e.getClass()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, ERR_SERVICE);
        }
        return filteredSuggestions;
    }
}
