package com.example.opensourceproxy.service;


import com.example.opensourceproxy.infra.failstore.FailStore;
import com.example.opensourceproxy.infra.failstore.FailStoreEntry;
import com.example.opensourceproxy.infra.kafka.KafkaProducerClient;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaProducerClientTest {

    @Test
    void sendAsync_whenKeyNotNullAndException_shouldSaveFailStore() {
        // Mock FailStore
        FailStore failStore = mock(FailStore.class);

        // Mock KafkaTemplate
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        // CompletableFuture로 실패 시뮬레이션
        CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

        // send() 호출 시 실패 CompletableFuture 반환
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(failedFuture);

        KafkaProducerClient client = new KafkaProducerClient(kafkaTemplate, failStore);

        // key != null
        client.sendAsync("topic", "key", "message");

        // FailStore.save 호출 여부 확인
        verify(failStore, atLeastOnce()).save(any(FailStoreEntry.class));
    }

    @Test
    void sendAsync_whenKeyNullAndException_shouldSaveFailStore() {
        // Mock FailStore
        FailStore failStore = mock(FailStore.class);

        // Mock KafkaTemplate
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        // CompletableFuture로 실패 시뮬레이션
        CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));



        // key == null일 때 호출되는 send()를 실패 Future로 반환
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(failedFuture);

        KafkaProducerClient client = new KafkaProducerClient(kafkaTemplate, failStore);

        // key == null
        client.sendAsync("topic", null, "message");

        // FailStore.save 호출 여부 확인
        verify(failStore, atLeastOnce()).save(any(FailStoreEntry.class));
    }


    @Test
    void sendAsync_whenKeyNotNullAndException_shouldSaveFailStore1() {
        // Mock FailStore
        FailStore failStore = mock(FailStore.class);

        // Mock KafkaTemplate
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        // send() 호출 시 즉시 실패 CompletableFuture 반환
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
                    future.completeExceptionally(new RuntimeException("Kafka error"));
                    return future;
                });

        KafkaProducerClient client = new KafkaProducerClient(kafkaTemplate, failStore);

        // key != null
        client.sendAsync("topic", "key", "message");

        // FailStore.save 호출 여부 확인
        verify(failStore, atLeastOnce()).save(any(FailStoreEntry.class));
    }

    @Test
    void sendAsync_whenKeyNullAndException_shouldSaveFailStore2() {
        // Mock FailStore
        FailStore failStore = mock(FailStore.class);

        // Mock KafkaTemplate
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        // key == null일 때 호출되는 send()를 즉시 실패 CompletableFuture 반환
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenAnswer(invocation -> {
                    CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
                    future.completeExceptionally(new RuntimeException("Kafka error"));
                    return future;
                });

        KafkaProducerClient client = new KafkaProducerClient(kafkaTemplate, failStore);

        // key == null
        client.sendAsync("topic", null, "message");

        // FailStore.save 호출 여부 확인
        verify(failStore, atLeastOnce()).save(any(FailStoreEntry.class));
    }
}