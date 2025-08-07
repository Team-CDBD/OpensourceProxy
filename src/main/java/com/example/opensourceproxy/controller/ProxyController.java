package com.example.opensourceproxy.controller;

import com.example.opensourceproxy.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/send-order")
public class ProxyController {

    private final KafkaProducerService producerService;

    public ProxyController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<String> sendOrder(@RequestBody Map<String, Object> payload) {
        producerService.sendMessage(payload);
        return ResponseEntity.ok("Sent to Kafka");
    }
}