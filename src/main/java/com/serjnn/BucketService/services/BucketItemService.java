package com.serjnn.BucketService.services;


import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BucketItemService {
    private  final BucketItemRepository bucketItemRepository;
    public Mono<BucketItem> findBucketItemByBucketId(Long bucketId){
        return bucketItemRepository.findBucketItemByBucketId(bucketId);
    }

    public Flux<BucketItem> findAllByBucketId(Long bucketId){
        return bucketItemRepository.findAllByBucketId(bucketId);
    }

}
