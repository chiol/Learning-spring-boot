package com.springtest.learningwebflux.comments;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomProcessor {
    String INPUT = "input";
    String OUTPUT = "emptyOutput";

    @Input(CustomProcessor.INPUT)
    SubscribableChannel input();

    @Output(CustomProcessor.OUTPUT)
    MessageChannel output();
}
