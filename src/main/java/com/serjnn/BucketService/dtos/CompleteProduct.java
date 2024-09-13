package com.serjnn.BucketService.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteProduct {
    private Long id;

    private int quantity;

    private String name;

    private String description;

    private int price;

    private String category;

}
