package com.serjnn.BucketService.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table("bucket")
public class Bucket {
    @Id
    private Long id;

    private Long clientId;


    public Bucket(Long clientId) {
        this.clientId = clientId;
    }
}
