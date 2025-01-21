package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BucketService {
    private final BucketRepository bucketRepository;
    private final WebClient.Builder webClientBuilder;
    private final BucketItemRepository bucketItemRepository;

    public Flux<CompleteProduct> getCompleteProducts(long clientId) {
        return findBucketByClientId(clientId)
                .flatMapMany(bucket -> bucketItemRepository.findAllByBucketId(bucket.getId())
                        .map(BucketItem::getProductId)
                        .collectList()
                        .flatMapMany(productIds -> {

                            Map<String, List<Long>> requestBody = new HashMap<>();
                            requestBody.put("ids", productIds);

                            return webClientBuilder.build()
                                    .post()
                                    .uri("lb://product/api/v1/all/by-ids")
                                    .bodyValue(requestBody)
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                                    })
                                    .flatMapMany(productDto ->
                                            mapToCompleteProducts(bucketItemRepository
                                                    .findAllByBucketId(bucket.getId()), productDto));
                        }));
    }


    private Flux<CompleteProduct> mapToCompleteProducts(Flux<BucketItem> bucketItems,
                                                        List<ProductDto> productDto) {
        return bucketItems
                .collectList()
                .flatMapMany(items -> Flux.fromIterable(productDto)
                .flatMap(product -> getProductQuantity(product.getId(), items)
                        .map(quantity ->
                                new CompleteProduct(product.getId(),
                                        quantity,
                                        product.getName(),
                                        product.getDescription(),
                                        product.getPrice(),
                                        product.getCategory()))));
    }


    private Mono<Integer> getProductQuantity(long id, List<BucketItem> bucketItem) {
        return Flux.fromIterable(bucketItem)
                .filter(bucketItem2 -> Objects.equals(bucketItem2.getProductId(), id))
                .next()
                .map(BucketItem::getQuantity)
                .defaultIfEmpty(0);

    }

    private Mono<Bucket> findBucketByClientId(long clientId) {
        return bucketRepository.findBucketByClientId(clientId)
                .switchIfEmpty(createBucketForClient(clientId));
    }

    private Mono<Bucket> createBucketForClient(long clientId) {
        Bucket bucket = new Bucket(clientId);
        return save(bucket)
                .then(Mono.just(bucket));

    }

    private Mono<Void> save(Bucket bucket) {
        return bucketRepository.save(bucket).then();
    }


    public Mono<Void> restore(OrderDTO orderDTO) {
        return findBucketByClientId(orderDTO.getClientId())
                .flatMap(bucket -> Flux.fromIterable(orderDTO.getItems())
                        .map(item -> new BucketItem(bucket.getId(), item.getId(), item.getQuantity()))
                        .collectList()
                        .flatMap(bucketItems -> bucketItemRepository
                                .saveAll(bucketItems)
                                .then()));
    }


    public Mono<Void> addProduct(long clientId, long productId) {
        return findBucketByClientId(clientId)
                .flatMap(bucket -> bucketItemRepository.findByBucketId(bucket.getId())
                        .flatMap(existingBucketItem -> {
                            existingBucketItem.setQuantity(existingBucketItem.getQuantity() + 1);
                            return bucketItemRepository.save(existingBucketItem).then(Mono.just(bucket));
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            BucketItem bucketItem = new BucketItem(bucket.getId(), productId, 1);
                            return bucketItemRepository.save(bucketItem)
                                    .then(Mono.just(bucket));
                        }))
                        .flatMap(this::save));
    }


    public Mono<Void> removeProductFromBucket(long clientId, long productId) {
        return findBucketByClientId(clientId)
                .flatMap(bucket -> bucketItemRepository.findAllByBucketId(bucket.getId())
                        .filter(item -> item.getProductId() == productId)
                        .next()
                        .flatMap(existingBucketItem -> {
                            if (existingBucketItem.getQuantity() > 0) {
                                existingBucketItem.setQuantity(existingBucketItem.getQuantity() - 1);

                                if (existingBucketItem.getQuantity() == 0) {
                                    return bucketItemRepository.deleteById(existingBucketItem.getId());
                                } else {
                                    return bucketItemRepository.save(existingBucketItem).then();
                                }
                            }
                            return Mono.empty();
                        }));
    }

    public Mono<Void> clearBucket(long clientID) {
        return findBucketByClientId(clientID)
                .flatMap(bucket -> bucketItemRepository.findAllByBucketId(bucket.getId())
                        .collectList()
                        .flatMap(bucketItemRepository::deleteAll));

    }
}

