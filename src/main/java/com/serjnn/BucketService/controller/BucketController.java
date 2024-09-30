package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.services.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BucketController {

    private final BucketService bucketService;


//    @GetMapping("/add/{clientId}/{productId}")
//    void add(@PathVariable("clientId") Long clientId, @PathVariable("productId") Long productId) {
//        bucketService.addProduct(clientId, productId);
//
//    }
//
//    @GetMapping("/remove/{clientId}/{productId}")
//    void remove(@PathVariable("clientId") Long clientId, @PathVariable("productId") Long productId) {
//        bucketService.removeProductFromBucket(clientId, productId);
//
//    }

    @GetMapping("/getCompleteBucket/{clientId}")
    Flux<CompleteProduct> complete(@PathVariable("clientId") Long clientId) {
        return bucketService.getCompleteProducts(clientId);
    }


//    @PostMapping("/clear")
//    void clear(@RequestBody Long clientId) {
//        bucketService.clear(clientId);
//    }
//
//    @PostMapping("/restore")
//    void restore(@RequestBody OrderDTO orderDTO) {
//        bucketService.restore(orderDTO);
//    }


}

