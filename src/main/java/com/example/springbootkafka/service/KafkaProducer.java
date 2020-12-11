package com.example.springbootkafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random rnd = new Random();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void startSending() {
        this.executor.schedule(() -> sendMessage("spring-kafka-topic", "Hello, World!"), 1, SECONDS);
    }


    public void sendMessage(String topicName, String msg) {
        log.info("sending payload='{}' to topic='{}'", msg, topicName);
        kafkaTemplate.send(topicName, msg);
        executor.schedule(() -> sendMessage(topicName, msg), rnd.nextInt(5000), MILLISECONDS);
    }
}
