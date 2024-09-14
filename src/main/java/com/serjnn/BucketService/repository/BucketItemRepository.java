package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.models.BucketItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {
    Optional<BucketItem> findById(Long id);

    Optional<BucketItem> findBucketItemByProductId(Long id);


}