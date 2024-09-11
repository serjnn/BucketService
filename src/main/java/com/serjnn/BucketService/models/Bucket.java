package com.serjnn.BucketService.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bucket")
@NoArgsConstructor
public class Bucket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long clientId;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<BucketItem> bucketItem = new ArrayList<>();

    public Bucket(Long clientId) {
        this.clientId = clientId;
    }
}