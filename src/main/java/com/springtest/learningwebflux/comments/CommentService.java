package com.springtest.learningwebflux.comments;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommentService {
    private final CommentWriterRepository repository;
    private final MeterRegistry meterRegistry;

    public CommentService(CommentWriterRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue,
//            exchange = @Exchange(value = "learning-spring-boot"),
//            key = "comments.new"
//    ))
//    public void save(Comment comment) {
//        repository
//                .save(comment)
//                .log("commentService-save")
//                .subscribe(comment1 -> {
//                    meterRegistry
//                            .counter("comments.consumed", "imageId", comment1.getImageId())
//                            .increment();
//                });
//    }

    @StreamListener
    @Output(CustomProcessor.OUTPUT)
    public Flux<Void> save(@Input(CustomProcessor.INPUT) Flux<Comment> newComments) {
        return repository
                .saveAll(newComments)
                .flatMap(comment -> {
                    meterRegistry
                            .counter("comments.consumed", "imageId", comment.getImageId())
                            .increment();
                    return Mono.empty();
                });
    }
}
