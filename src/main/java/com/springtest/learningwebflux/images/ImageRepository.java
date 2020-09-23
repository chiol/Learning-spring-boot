package com.springtest.learningwebflux.images;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ImageRepository extends ReactiveMongoRepository<Image, String> {
    Mono<Image> findByName(String name);
}
