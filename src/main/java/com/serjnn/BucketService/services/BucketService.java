package com.serjnn.BucketService.services;

import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final BucketRepository bucketRepository;
    private final BucketItemService bucketItemService;


    public ResponseEntity<HttpStatus> addProduct(Long clientId, Long productId) {
        Bucket bucket = findBucketByClientId(clientId);

        Optional<BucketItem> existingBucketItem = bucket
                .getBucketItem()
                .stream()
                .filter(bucketItem -> bucketItem.getProductId()
                         == productId)
                .findFirst();


        if (existingBucketItem.isPresent()) {
            BucketItem bucketItem = bucketItemService.findBucketItemByProductId(productId);
            bucketItem.setQuantity(bucketItem.getQuantity() + 1);
            save(bucket);
            return new ResponseEntity<>(HttpStatus.OK);

        } else {
            BucketItem bucketItems = new BucketItem(productId, 1,bucket);
            bucket.getBucketItem().add(bucketItems);
            save(bucket);
            return new ResponseEntity<>(HttpStatus.OK);

        }


    }

    public void save(Bucket bucket) {
        bucketRepository.save(bucket);
    }

    private Bucket findBucketByClientId(Long clientId) {
        return bucketRepository.findBucketByClientId(clientId).orElseGet(
                () -> createBucketForClient(clientId)
        );
    }

    private Bucket createBucketForClient(Long clientId) {
        Bucket bucket = new Bucket(clientId);
        save(bucket);
        return bucket;

    }


    @Transactional
    public List<Bucket> findAll() {
        return bucketRepository.findAll();
    }


    public Bucket findById(Long id) {
        return bucketRepository.findById(id).orElseThrow();
    }
}
