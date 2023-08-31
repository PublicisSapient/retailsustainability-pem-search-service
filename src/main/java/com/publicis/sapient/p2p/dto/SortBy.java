package com.publicis.sapient.p2p.dto;

public enum SortBy {

     RELEVANCE(0), DATE(1), PRICE_LOW_HIGH(2), PRICE_HIGH_LOW(3), DISTANCE(4);

    private final int value;

    SortBy(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}
