package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service

public class BucketService {
    private final BucketRepository bucketRepository;

    private final WebClient.Builder webClientBuilder;

    private final BucketItemService bucketItemService;

    public BucketService(BucketRepository bucketRepository,
                         WebClient.Builder webClientBuilder,
                         BucketItemService bucketItemService) {
        this.bucketRepository = bucketRepository;
        this.webClientBuilder = webClientBuilder;
        this.bucketItemService = bucketItemService;
    }


    public Flux<CompleteProduct> getCompleteProducts(Long clientId) {
        return findBucketByClientId(clientId)
                .flatMapMany(bucket -> {
                    return bucketItemService.findAllByBucketId(bucket.getId())
                            .map(BucketItem::getProductId)
                            .collectList()
                            .flatMapMany(productIds -> {

                                Map<String, List<Long>> requestBody = new HashMap<>();
                                requestBody.put("ids", productIds);

                                return webClientBuilder.build()
                                        .post()
                                        .uri("lb://product/api/v1/by-ids")
                                        .bodyValue(requestBody)
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                                        })
                                        .flatMapMany(productDtos -> {
                                            return mapToCompleteProducts(bucketItemService.findAllByBucketId
                                                    (bucket.getId()), productDtos);

                                        });
                            });
                });
    }


    private Flux<CompleteProduct> mapToCompleteProducts(Flux<BucketItem> bucketItems, List<ProductDto> productDtos) {
        return bucketItems.collectList().flatMapMany(items -> {
            return Flux.fromIterable(productDtos)
                    .flatMap(product -> getQuantity(product.getId(), items)
                            .map(quantity ->
                                    new CompleteProduct(product.getId(),
                                            quantity,
                                            product.getName(),
                                            product.getDescription(),
                                            product.getPrice(),
                                            product.getCategory())));
        });
    }


    private Mono<Integer> getQuantity(Long id, List<BucketItem> bucketItem) {
        return Flux.fromIterable(bucketItem)
                .filter(bucketItem2 -> Objects.equals(bucketItem2.getProductId(), id))
                .next()
                .map(BucketItem::getQuantity)
                .defaultIfEmpty(0);

    }

    private Mono<Bucket> findBucketByClientId(Long clientId) {
        return bucketRepository.findBucketByClientId(clientId)
                .switchIfEmpty(createBucketForClient(clientId));
    }

    private Mono<Bucket> createBucketForClient(Long clientId) {
        Bucket bucket = new Bucket(clientId);
        save(bucket);
        return Mono.just(bucket);

    }

    public Mono<Void> save(Bucket bucket) {
        return bucketRepository.save(bucket).then();
    }


//    public void addProduct(Long clientId, Long productId) {
//        Bucket bucket = findBucketByClientId(clientId);
//
//        BucketItem existingBucketItem = bucket
//                .getBucketItem()
//                .stream()
//                .filter(bucketItem -> Objects.equals(bucketItem.getProductId(), productId))
//                .findFirst().orElse(null);
//
//
//        if (existingBucketItem != null) {
//
//            existingBucketItem.setQuantity(existingBucketItem.getQuantity() + 1);
//            save(bucket);
//
//        } else {
//            BucketItem bucketItems = new BucketItem(productId, 1, bucket.getId());
//            bucket.getBucketItem().add(bucketItems);
//            save(bucket);
//
//        }
//
//
//    }
//

//

//

//
//
//    public void removeProductFromBucket(Long clientId, Long productId) {
//        Bucket bucket = findBucketByClientId(clientId);
//
//        BucketItem existingBucketItem = bucket
//                .getBucketItem()
//                .stream()
//                .filter(bucketItem -> Objects.equals(bucketItem.getProductId(), productId))
//                .findFirst().orElse(null);
//
//
//        if (existingBucketItem != null && existingBucketItem.getQuantity() != 0) {
//
//            existingBucketItem.setQuantity(existingBucketItem.getQuantity() - 1);
//
//            if (existingBucketItem.getQuantity() == 0) {
//                bucket.getBucketItem().remove(existingBucketItem);
//            }
//            save(bucket);
//
//        }
//
//
//    }
//
//    public Mono<Void> clear(Long clientID) {
//        return findBucketByClientId(clientID)
//                .flatMap(bucket -> {
//                    bucket.getBucketItems().clear();
//                    return save(bucket);
//
//                })
//                .then();
//
//
//    }
//
//
//    public void restore(OrderDTO orderDTO) {
//        Bucket bucket = findBucketByClientId(orderDTO.getClientID());
//        List<BucketItem> bucketItems = orderDTO
//                .getItems()
//                .stream()
//                .map(item -> new BucketItem(item.getId(), item.getQuantity(), bucket.getId()))
//                .collect(Collectors.toCollection(ArrayList::new));
//
//        bucket.getBucketItem().addAll(bucketItems);
//        save(bucket);
//    }
}

