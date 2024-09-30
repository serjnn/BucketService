package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.models.Bucket;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface BucketRepository extends ReactiveCrudRepository<Bucket, Long> {

    Mono<Bucket> findBucketByClientId(Long clientId);


}