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

    @GetMapping("/addProduct/{clientId}/{productId}")
    Mono<Void> add(@PathVariable("clientId") long clientId,
                   @PathVariable("productId") long productId) {
        return bucketService.addProduct(clientId, productId);

    }

    @GetMapping("/removeProduct/{clientId}/{productId}")
    Mono<Void> remove(@PathVariable long clientId,
                      @PathVariable long productId) {
        return bucketService.removeProductFromBucket(clientId, productId);

    }

    @GetMapping("/getCompleteBucket/{clientId}")
    Flux<CompleteProduct> complete(@PathVariable("clientId") long clientId) {
        return bucketService.getCompleteProducts(clientId);
    }


    @PostMapping("/clearBucket")
    Mono<Void> clear(@RequestBody long clientId) {
        return bucketService.clearBucket(clientId);
    }

    @PostMapping("/restoreBucket")
    Mono<Void> restore(@RequestBody OrderDTO orderDTO) {
        return bucketService.restore(orderDTO);
    }


}

