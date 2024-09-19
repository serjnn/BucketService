package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class BucketService {
    private final BucketRepository bucketRepository;

    private final WebClient.Builder webClientBuilder;

    public BucketService(BucketRepository bucketRepository,
                         WebClient.Builder webClientBuilder) {
        this.bucketRepository = bucketRepository;
        this.webClientBuilder = webClientBuilder;
    }


    public Mono<List<CompleteProduct>> getCompleteProducts(Long clientId) {
        Bucket bucket = findBucketByClientId(clientId);
        List<Long> productIds = bucket.getBucketItem().stream().mapToLong(BucketItem::getProductId).boxed().toList();
        Map<String, List<Long>> requestBody = new HashMap<>();

        requestBody.put("ids", productIds);
        return webClientBuilder.build()

                .post()
                .uri("lb://product/api/v1/by-ids")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                })
                .map(productDto -> mapToCompleteProducts(bucket.getBucketItem(), productDto));

    }

    private List<CompleteProduct> mapToCompleteProducts(List<BucketItem> bucketItem, List<ProductDto> productDto) {


        return productDto.stream()
                .map(product -> new CompleteProduct(product.getId(),
                        getQuantity(product.getId(), bucketItem),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory())


                ).toList();

    }

    private Integer getQuantity(Long id, List<BucketItem> bucketItem) {
        BucketItem bucketItem1 = bucketItem.stream().filter(i -> Objects.equals(i.getProductId(), id)).findFirst().orElse(null);

        return bucketItem1 != null ? bucketItem1.getQuantity() : 0;
    }


    public void addProduct(Long clientId, Long productId) {
        Bucket bucket = findBucketByClientId(clientId);

        BucketItem existingBucketItem = bucket
                .getBucketItem()
                .stream()
                .filter(bucketItem -> Objects.equals(bucketItem.getProductId(), productId))
                .findFirst().orElse(null);


        if (existingBucketItem != null) {

            existingBucketItem.setQuantity(existingBucketItem.getQuantity() + 1);
            save(bucket);

        } else {
            BucketItem bucketItems = new BucketItem(productId, 1, bucket);
            bucket.getBucketItem().add(bucketItems);
            save(bucket);

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


    public void removeProductFromBucket(Long clientId, Long productId) {
        Bucket bucket = findBucketByClientId(clientId);

        BucketItem existingBucketItem = bucket
                .getBucketItem()
                .stream()
                .filter(bucketItem -> Objects.equals(bucketItem.getProductId(), productId))
                .findFirst().orElse(null);


        if (existingBucketItem != null && existingBucketItem.getQuantity() != 0) {

            existingBucketItem.setQuantity(existingBucketItem.getQuantity() - 1);

            if (existingBucketItem.getQuantity() == 0) {
                bucket.getBucketItem().remove(existingBucketItem);
            }
            save(bucket);

        }


    }

    public void clear(Long clientID) {
        Bucket bucket = findBucketByClientId(clientID);
        bucket.getBucketItem().clear();
        save(bucket);
    }


    public void restore(OrderDTO orderDTO) {
        Bucket bucket = findBucketByClientId(orderDTO.getClientID());
        List<BucketItem> bucketItems = orderDTO
                .getItems()
                .stream()
                .map(item -> new BucketItem(item.getId(), item.getQuantity(), bucket))
                .collect(Collectors.toCollection(ArrayList::new));
        System.out.println(bucketItems);
        bucket.getBucketItem().addAll(bucketItems);
        save(bucket);
    }
}

