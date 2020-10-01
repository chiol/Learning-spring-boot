package com.springtest.learningwebflux.comments;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;


@Controller
@EnableBinding(Source.class)
public class CommentController {

    private FluxSink<Message<Comment>> commentSink;
    private Flux<Message<Comment>> flux;

    public CommentController() {
        this.flux = Flux.<Message<Comment>>create(
                emitter -> this.commentSink = emitter,
                FluxSink.OverflowStrategy.IGNORE)
                .publish()
                .autoConnect();
    }
    @PostMapping("/comments")
    public Mono<String> addComment(Mono<Comment> newComment) {
        if (commentSink != null) {
            return newComment.map(comment -> commentSink.next(MessageBuilder
            .withPayload(comment)
            .build()))
            .then(Mono.just("redirect:/"));
        } else {
            return Mono.just("redirect:/");
        }
    }
    @StreamEmitter
    public void emit(@Output(Source.OUTPUT) FluxSender output) {
        output.send(this.flux);
    }

}
