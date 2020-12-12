package com.example.springbootkafka.service;

import com.example.springbootkafka.model.Greeting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
    private final KafkaTemplate<String, Greeting> greetingKafkaTemplate;
    private final Random rnd = new Random();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Value(value = "${message.topic.name}")
    private String messageTopicName;

    @Value(value = "${partitioned.topic.name}")
    private String partitionedTopicName;

    @Value(value = "${filtered.topic.name}")
    private String filteredTopicName;

    @Value(value = "${greeting.topic.name}")
    private String greetingTopicName;

    @PostConstruct
    public void startSending() {
        this.executor.schedule(() -> send(messageTopicName, "Hello, World!"), 1, SECONDS);
    }

    public void send(String topicName, String msg) {

        sendMessage(topicName, msg);

        for (int i = 0; i < 5; i++) {
            sendMessageToPartition("Hello To Partitioned Topic!", i);
        }

        sendMessageToFiltered("Hello Kafka!");
        sendMessageToFiltered("Hello World!");

        sendGreetingMessage(new Greeting("Greetings", "World!"));

        executor.schedule(() -> send(topicName, msg), rnd.nextInt(5000), MILLISECONDS);
    }

    public void sendMessage(String topicName, String msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, msg);

        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=['{}'] with offset=['{}']", msg, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send message=['{}'] due to : '{}'", msg, ex.getMessage());
            }
        });
    }

    public void sendMessageToPartition(String message, int partition) {
        kafkaTemplate.send(partitionedTopicName, partition, null, message);
    }

    public void sendMessageToFiltered(String message) {
        kafkaTemplate.send(filteredTopicName, message);
    }

    public void sendGreetingMessage(Greeting greeting) {
        greetingKafkaTemplate.send(greetingTopicName, greeting);
    }
}
