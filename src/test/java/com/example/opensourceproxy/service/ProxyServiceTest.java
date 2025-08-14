package com.example.opensourceproxy.service;

import com.example.opensourceproxy.dto.ProxyRequest;
import com.example.opensourceproxy.infra.failstore.FailStore;
import com.example.opensourceproxy.infra.kafka.KafkaProducerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProxyServiceTest {

    private ProxyRequestService proxyService;

    private KafkaProducerClient kafkaProducerClient;
    private FailStore failStore;

    @BeforeEach
    void setup() {
        kafkaProducerClient = new KafkaProducerClient(null, null); // Mock 안해도 null-safe
        failStore = null; // FailStore는 실제 DB 없이 테스트 가능
        proxyService = new ProxyRequestService(kafkaProducerClient, failStore);
    }

    @Test
    void enqueue_whenQueueFull_shouldThrow() {
        // 큐 용량 1000으로 가정
        for (int i = 0; i < 1000; i++) {
            proxyService.enqueue(new ProxyRequest("topic", null, "msg" + i));
        }

        // 1001번째 요청 → 예외 발생 확인
        assertThatThrownBy(() -> proxyService.enqueue(new ProxyRequest("topic", null, "overflow")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Queue is full");
    }
}