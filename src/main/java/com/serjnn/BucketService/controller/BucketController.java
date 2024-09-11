package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import com.serjnn.BucketService.services.BucketItemService;
import com.serjnn.BucketService.services.BucketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BucketController {

    private final BucketService bucketService;

    private final BucketItemRepository bucketItemRepository;


    @GetMapping("/add/{clientId}/{productId}")
    void add(@PathVariable("clientId") Long clientId, @PathVariable("productId") Long productId){
         bucketService.addProduct(clientId,productId);

    }

    @GetMapping("/items/{bucketId}")
      BucketItem get(@PathVariable("bucketId") Long bucketId) {
        return bucketItemRepository.findById(bucketId).orElseThrow();
    }

    @GetMapping("/all")
    List<Bucket> all2(){
        return bucketService.findAll();
    }

//    @GetMapping("/some/{id}")
//    void some(@PathVariable("id") Long id){
//       Bucket bucket = bucketService.findById(id);
//        BucketItem bucketItem = new BucketItem(22L,1);
//       bucket.getBucketItem().add(bucketItem    );
//       bucketService.save(bucket);
//    }








}

