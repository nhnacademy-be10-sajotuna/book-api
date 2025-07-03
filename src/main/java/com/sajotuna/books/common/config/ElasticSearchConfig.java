//package com.sajotuna.books.common.config;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//
//@Configuration
////@EnableElasticsearchRepositories(basePackages = "com.sajotuna.books.search.repository")
//public class ElasticSearchConfig extends ElasticsearchConfiguration {
//
//    @Value("${spring.elasticsearch.username}")
//    private String username;
//
//    @Value("${spring.elasticsearch.password}")
//    private String password;
//
//    @Value("${spring.elasticsearch.uris}")
//    private String uri;
//
//    @Override
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo(uri)
//                .withBasicAuth(username, password)
//                .build();
//
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("Authorization", "ApiKey " + apiKey);
////        return ClientConfiguration.builder()
////                .connectedTo(uri)
////                .withDefaultHeaders(headers)
////                .build();
//    }
//
//
//}