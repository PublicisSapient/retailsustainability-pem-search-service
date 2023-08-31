package com.publicis.sapient.p2p.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private String id;
    private String name;
    private String description;
    private Category category;
    private OfferType offerType;
    private List<String> images;
    private String location;
    private GeoLocation geoLocation;
    private String user;
    private String price;
    private Date createdTime;
}
