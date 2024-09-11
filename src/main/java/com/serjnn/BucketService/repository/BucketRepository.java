package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.models.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BucketRepository extends JpaRepository<Bucket, Long> {

    Optional<Bucket> findBucketByClientId(Long clientId);
}