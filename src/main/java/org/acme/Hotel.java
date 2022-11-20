package org.acme;

import com.univocity.parsers.annotations.Parsed;

import lombok.Data;

@Data
public class Hotel {

    //id,name,latitude,longitude,category_stars

    @Parsed(field = "id")
    private int id;

    @Parsed(field = "name")
    private String name;

    @Parsed(field = "latitude")
    private float latitude;

    @Parsed(field = "longtude")
    private float longitude;

    @Parsed(field = "category_stars")
    private float category_stars;

}
