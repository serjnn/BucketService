package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.models.BucketItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BucketItemRepository extends ReactiveCrudRepository<BucketItem, Long> {
    Mono<BucketItem> findBucketItemByBucketId(Long bucketId);

    Flux<BucketItem> findAllByBucketId(Long bucketid);


}