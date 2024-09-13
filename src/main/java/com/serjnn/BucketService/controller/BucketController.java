package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import com.serjnn.BucketService.services.BucketItemService;
import com.serjnn.BucketService.services.BucketService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @GetMapping("/getCompleteBucket/{clientId}")
    Mono<List<CompleteProduct>> complete(@PathVariable("clientId") Long clientId){
        return bucketService.getCompleteProducts(clientId);
    }









}

