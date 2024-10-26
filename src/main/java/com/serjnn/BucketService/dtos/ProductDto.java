package com.serjnn.BucketService.dtos;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductDto {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;


}
