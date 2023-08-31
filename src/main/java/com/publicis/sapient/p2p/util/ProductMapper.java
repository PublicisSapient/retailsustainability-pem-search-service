package com.publicis.sapient.p2p.util;

import com.publicis.sapient.p2p.dto.Category;
import com.publicis.sapient.p2p.dto.GeoLocation;
import com.publicis.sapient.p2p.dto.OfferType;
import com.publicis.sapient.p2p.dto.Product;
import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;
import java.util.Date;

public class ProductMapper {

    private ProductMapper() {

    }

    public static Product mapToProduct(SolrDocument doc) {

        Product product = new Product();
        product.setId((String) (doc.getFieldValue("id")));
        product.setName(((ArrayList)doc.getFieldValue("name")).get(0).toString());
        if(doc.getFieldValue("description") != null)
            product.setDescription(((ArrayList)doc.getFieldValue("description")).get(0).toString());
        product.setCategory(Category.valueOf(((ArrayList)doc.getFieldValue("category")).get(0).toString()));
        product.setOfferType(OfferType.valueOf(((ArrayList)doc.getFieldValue("offerType")).get(0).toString()));
        product.setImages(((ArrayList)doc.getFieldValue("images")));
        product.setLocation(((ArrayList)doc.getFieldValue("location")).get(0).toString());
        String[] geo = doc.getFieldValue("geoLocation").toString().split(",");
        product.setGeoLocation(new GeoLocation(geo[0], geo[1]));
        product.setUser(((ArrayList)doc.getFieldValue("user")).get(0).toString());
        product.setPrice(doc.getFieldValue("price").toString());
        product.setCreatedTime((Date)((ArrayList)doc.getFieldValue("createdTime")).get(0));
        return product;
    }
}
