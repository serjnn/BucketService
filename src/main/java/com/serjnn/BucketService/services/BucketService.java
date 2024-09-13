package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.models.Bucket;
import com.serjnn.BucketService.models.BucketItem;
import com.serjnn.BucketService.repository.BucketRepository;
import com.serjnn.BucketService.dtos.CompleteProduct;
import jakarta.transaction.Transactional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service

public class BucketService {
    private final BucketRepository bucketRepository;
    private final BucketItemService bucketItemService;
    private final WebClient.Builder webClientBuilder;

    public BucketService(BucketRepository bucketRepository,
                         BucketItemService bucketItemService,
                         WebClient.Builder webClientBuilder) {
        this.bucketRepository = bucketRepository;
        this.bucketItemService = bucketItemService;
        this.webClientBuilder = webClientBuilder;
    }


    public Mono<List<CompleteProduct>> getCompleteProducts(Long clientId) {
        Bucket bucket = findBucketByClientId(clientId);
        List<Long> productIds = bucket.getBucketItem().stream().mapToLong(BucketItem::getProductId).boxed().toList();
        java.util.Map<String, List<Long>> requestBody = new HashMap<>();

        requestBody.put("ids", productIds);
        return webClientBuilder.build()

                .post()
                .uri("lb://product/api/v1/by-ids")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {})
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
        BucketItem bucketItem1 =  bucketItem.stream().filter(i -> i.getProductId() == id).findFirst().orElse(null);
        return bucketItem1.getQuantity();
    }


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
            BucketItem bucketItems = new BucketItem(productId, 1, bucket);
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
