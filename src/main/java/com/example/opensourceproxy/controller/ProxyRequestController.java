package com.example.opensourceproxy.controller;

import com.example.opensourceproxy.dto.ProxyRequest;
import com.example.opensourceproxy.service.ProxyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class ProxyRequestController {

    private final ProxyRequestService proxyRequestService;

    @PostMapping
    public ResponseEntity<String> enqueue(@RequestBody ProxyRequest req) {
        proxyRequestService.enqueue(req);
        return ResponseEntity.accepted().body("queued");
    }
}