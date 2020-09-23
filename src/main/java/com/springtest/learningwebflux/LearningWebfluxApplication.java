package com.springtest.learningwebflux;

import com.springtest.learningwebflux.comments.Comment;
import com.springtest.learningwebflux.images.Image;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class LearningWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningWebfluxApplication.class, args);
    }

    @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    CommandLineRunner init(MongoOperations operations) {
        return args -> {
            operations.dropCollection(Image.class);
            operations.dropCollection(Comment.class);
            operations.insert(new Image("1", "learning-spring.jpg"));
            operations.insert(new Image("2", "learning-spring.png"));
            operations.insert(new Image("3", "learning-spring.svg"));

            operations.findAll(Image.class).forEach(image -> {
                System.out.println(image.toString());
            });
        };
    }
    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
