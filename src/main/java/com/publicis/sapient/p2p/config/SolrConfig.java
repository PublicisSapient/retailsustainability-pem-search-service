package com.publicis.sapient.p2p.config;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.PreemptiveBasicAuthClientBuilderFactory;
import org.apache.solr.client.solrj.impl.SolrHttpClientBuilder;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class SolrConfig {

    @Value("${solrUrl}")
    private String solrUrl;

    @Value("${solrUsername}")
    private String solrUsername;

    @Value("${solrPassword}")
    private String solrPassword;

    @Bean
    public SolrClient solrClient() {

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set(HttpClientUtil.PROP_BASIC_AUTH_USER, solrUsername);
        params.set(HttpClientUtil.PROP_BASIC_AUTH_PASS, solrPassword);

        PreemptiveBasicAuthClientBuilderFactory.setDefaultSolrParams(params);
        PreemptiveBasicAuthClientBuilderFactory preemptiveBasicAuthClientBuilderFactory = new PreemptiveBasicAuthClientBuilderFactory();

        SolrHttpClientBuilder httpClientBuilder = preemptiveBasicAuthClientBuilderFactory
                .getHttpClientBuilder(Optional.empty());

        HttpClientUtil.setHttpClientBuilder(httpClientBuilder);

        CloseableHttpClient httpAuthClient = HttpClientUtil.createClient(params);

        return new HttpSolrClient.Builder(solrUrl).withHttpClient(httpAuthClient).build();

    }
}

