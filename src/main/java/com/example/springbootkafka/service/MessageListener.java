package com.example.springbootkafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageListener {

    @KafkaListener(topics = "spring-kafka-topic", groupId = "foo")
    public void listenGroupFoo(String message) {
        log.info("message received: '{}'", message);
        System.out.println("Received Message in group foo: " + message);
    }
}
