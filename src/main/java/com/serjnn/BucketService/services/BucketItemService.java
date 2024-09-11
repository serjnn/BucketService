package com.serjnn.BucketService.services;


import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class BucketItemService {

    private final BucketItemRepository bucketItemRepository;


    public BucketItem findBucketItemByProductId(Long productId) {
        return bucketItemRepository.findBucketItemByProductId(productId).orElseThrow();
    }
}
