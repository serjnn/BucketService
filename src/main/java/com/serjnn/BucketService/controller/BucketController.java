package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.services.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BucketController {

    private final BucketService bucketService;


    @GetMapping("/add/{clientId}/{productId}")
    Mono<Void> add(@PathVariable("clientId") Long clientId, @PathVariable("productId") Long productId) {
        return bucketService.addProduct(clientId, productId);

    }

    @GetMapping("/remove/{clientId}/{productId}")
    Mono<Void> remove(@PathVariable("clientId") Long clientId, @PathVariable("productId") Long productId) {
        return bucketService.removeProductFromBucket(clientId, productId);

    }

    @GetMapping("/getCompleteBucket/{clientId}")
    Flux<CompleteProduct> complete(@PathVariable("clientId") Long clientId) {
        return bucketService.getCompleteProducts(clientId);
    }


    @PostMapping("/clear")
    Mono<Void> clear(@RequestBody Long clientId) {
        return bucketService.clear(clientId);
    }

    @PostMapping("/restore")
    Mono<Void> restore(@RequestBody OrderDTO orderDTO) {
        return bucketService.restore(orderDTO);
    }


}

