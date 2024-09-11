package com.serjnn.BucketService.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bucket_item")
@NoArgsConstructor
public class BucketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_id")
    @JsonIgnore
    private Bucket bucket;

    private Long productId;

    private int quantity;

    public BucketItem(Long productId, int quantity, Bucket bucket) {
        this.productId = productId;
        this.quantity = quantity;
        this.bucket = bucket;
    }

    @Override
    public String toString() {
        return "BucketItem{" +
                "id=" + id +
                ", bucket_id=" + bucket.getId() +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}