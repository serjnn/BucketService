package com.serjnn.BucketService.services;


import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BucketItemService {
    private  final BucketItemRepository bucketItemRepository;

    public Flux<BucketItem> findAllByBucketId(Long bucketId){
        return bucketItemRepository.findAllByBucketId(bucketId);
    }

    public Mono<Void> addAll(List<BucketItem> bucketItems) {
        return bucketItemRepository.saveAll(bucketItems).then();
    }

    public Mono<Void> save(BucketItem bucketItem) {
         return bucketItemRepository.save(bucketItem).then();
    }

    public Mono<Void> deleteAll(List<BucketItem> items){
        return  bucketItemRepository.deleteAll(items);
    }

    public  Mono<Void> deleteOne(Long bucketItemId ){
        return bucketItemRepository.deleteById(bucketItemId);
    }
}
