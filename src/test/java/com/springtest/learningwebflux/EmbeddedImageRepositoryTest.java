package com.springtest.learningwebflux;

import com.springtest.learningwebflux.images.Image;
import com.springtest.learningwebflux.images.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class EmbeddedImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    MongoOperations operations;

    @BeforeEach
    public void setUp() {
        operations.dropCollection(Image.class);
        operations.insert(new Image("1","1.jpg"));
        operations.insert(new Image("2","2.jpg"));
        operations.insert(new Image("3","bazinga.jpg"));
        operations.findAll(Image.class).forEach(image -> {
            System.out.println(image.toString());
        });
    }

    @Test
    public void findAllShouldWork() {
        Flux<Image> images = imageRepository.findAll();
        StepVerifier.create(images)
            .recordWith(ArrayList::new)
            .expectNextCount(3)
            .consumeRecordedWith(results -> {
                assertThat(results).hasSize(3);
                assertThat(results)
                    .extracting(Image::getName)
                    .contains(
                        "1.jpg",
                        "2.jpg",
                        "bazinga.jpg"
                    );
            })
        .expectComplete()
        .verify();
    }

    @Test
    public void findByNameShouldWork() {
        Mono<Image> image = imageRepository.findByName("bazinga.png");
        StepVerifier.create(image)
                .expectNextMatches(results -> {
                    assertThat(results.getName()).isEqualTo("bazinga.png");
                    assertThat(results.getId()).isEqualTo("3");
                    return true;
                });
    }

}