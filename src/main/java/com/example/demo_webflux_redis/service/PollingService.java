package com.example.demo_webflux_redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollingService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisConnectionFactory connectionFactory;
    private final ReactiveRedisOperations<String, Object> redisOperations;

    public String pollData(HttpHeaders httpHeaders) {
        String callbackChannel = "callback:" + UUID.randomUUID();
        log.info(callbackChannel);

        return redisTemplate.listenToChannel(callbackChannel)
                .doOnSubscribe(subscription -> log.info("Subscribed to channel: {}", callbackChannel))
                .doOnNext(msg -> log.info("Message received: {}", msg))
                .next() // Take only the first message
                .timeout(Duration.ofSeconds(60), Mono.error(new TimeoutException("Callback not received within timeout")))
                .map(message -> {
                    // Process the message here if needed
                    log.info("Processing message from channel: {}", callbackChannel);
                    return message.toString();
                })
                .doFinally(signalType -> {
                    // Unsubscribe and clean up
                    log.info("Finalizing subscription for channel: {} {}", callbackChannel, signalType);
                    if (signalType == SignalType.ON_COMPLETE || signalType == SignalType.ON_ERROR) {
                        // Perform any cleanup or additional processing
                    }
                })
                .onErrorResume(ex ->{
                        log.error("Error", ex);
                        throw new RuntimeException("");}
                ).block();
    }

    public void receiveData(String callbackChannel) {

        log.info("Received data {}", callbackChannel);
        redisOperations.convertAndSend(callbackChannel, "READY").then().block();
    }
}
