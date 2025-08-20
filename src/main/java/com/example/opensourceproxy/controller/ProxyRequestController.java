package com.example.opensourceproxy.controller;

import com.example.opensourceproxy.dto.ProxyRequest;
import com.example.opensourceproxy.service.KafkaTopicService;
import com.example.opensourceproxy.service.ProxyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class ProxyRequestController {

    private final ProxyRequestService proxyRequestService;
    private final KafkaTopicService topicService;

    @PostMapping
    public ResponseEntity<String> enqueue(@RequestBody ProxyRequest req) {
        proxyRequestService.enqueue(req);
        return ResponseEntity.accepted().body("queued");
    }

    @GetMapping
    public ResponseEntity<List<String>> getTopics() throws Exception {
        return ResponseEntity.ok(topicService.listTopics());
    }

    @PostMapping("/create-topic")
    public ResponseEntity<String> createTopic(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int partitions,
            @RequestParam(defaultValue = "1") short replication,
            @RequestParam(defaultValue = "604800000") long retentionMs) {
        try {
            topicService.createTopic(name, partitions, replication, retentionMs);
            return ResponseEntity.ok("토픽 생성 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("토픽 생성 실패: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTopic(@RequestParam String name) {
        try {
            topicService.deleteTopic(name);
            return ResponseEntity.ok("토픽 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토픽 삭제 실패: " + e.getMessage());
        }
    }
}