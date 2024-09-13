package com.serjnn.BucketService.dtos;


import jakarta.persistence.*;
import lombok.Getter;

@Getter
public class ProductDto {


        private Long id;

        private String name;

        private String description;

        private int price;

        private String category;




}
