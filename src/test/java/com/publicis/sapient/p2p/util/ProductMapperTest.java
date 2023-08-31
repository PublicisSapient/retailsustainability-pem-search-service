package com.publicis.sapient.p2p.util;

import com.publicis.sapient.p2p.dto.Product;
import org.apache.solr.common.SolrDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProductMapper.class})
@ExtendWith(SpringExtension.class)
class ProductMapperTest {

    @Test
    void mapToProductTest() {

        SolrDocument doc = mock(SolrDocument.class);

        when(doc.getFieldValue("id")).thenReturn("123");
        when(doc.getFieldValue("name")).thenReturn(new ArrayList<String>() {{
            add("Product Name");
        }});
        when(doc.getFieldValue("description")).thenReturn(null);
        when(doc.getFieldValue("category")).thenReturn(new ArrayList<String>() {{
            add("CLOTH");
        }});
        when(doc.getFieldValue("offerType")).thenReturn(new ArrayList<String>() {{
            add("SELL");
        }});
        when(doc.getFieldValue("images")).thenReturn(new ArrayList<String>() {{
            add("image1.jpg");
            add("image2.jpg");
        }});
        when(doc.getFieldValue("location")).thenReturn(new ArrayList<String>() {{
            add("Location");
        }});
        when(doc.getFieldValue("geoLocation")).thenReturn("1.23456,2.34567");
        when(doc.getFieldValue("user")).thenReturn(new ArrayList<String>() {{
            add("2345");
        }});
        when(doc.getFieldValue("price")).thenReturn(9.99);
        when(doc.getFieldValue("createdTime")).thenReturn(new ArrayList<Date>() {{
            add(new Date());
        }});

        Product product = ProductMapper.mapToProduct(doc);

        assertEquals("123", product.getId());

    }
}
