package com.springtest.learningwebflux.comments;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentWriterRepository extends Repository<Comment, String> {
    Mono<Comment> save(Comment newComment);

    Mono<Comment> findById(String id);

    Flux<Comment> saveAll(Flux<Comment> newComments);
}
