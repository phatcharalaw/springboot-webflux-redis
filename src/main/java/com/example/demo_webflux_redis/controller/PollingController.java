package com.example.demo_webflux_redis.controller;

import com.example.demo_webflux_redis.service.PollingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/polling")
@RequiredArgsConstructor
public class PollingController {

    final PollingService pollingService;

    @GetMapping("/data")
    public ResponseEntity<?> pollData(@RequestHeader HttpHeaders httpHeaders){
        return ResponseEntity.ok(pollingService.pollData(httpHeaders));
    }

    /*@GetMapping("/data")
    public ResponseEntity<?> pollData(@RequestHeader HttpHeaders httpHeaders){
        return pollingService.pollData(httpHeaders)
                .map(receiveMessage ->
                        ResponseEntity.ok()
                                .header("X-Callback-Channel", "callbackChannel")
                                .body(receiveMessage)
                )
                .onErrorResume(TimeoutException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("Polling timed out"))
                )
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred"))
                ).block();
    }*/

    @PostMapping("/callback")
    public ResponseEntity<Void> callBack(@RequestHeader(name = "channel_key") String channel,
                                         @RequestBody String message){
        pollingService.receiveData(channel);
        return ResponseEntity.ok().build();
    }
}
