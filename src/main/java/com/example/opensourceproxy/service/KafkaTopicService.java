package com.example.opensourceproxy.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaTopicService {

    private final AdminClient adminClient;

    // 토픽 생성
    public void createTopic(String topicName, int numPartitions, short replicationFactor, long retentionMs) throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor)
                .configs(Map.of("retention.ms", String.valueOf(retentionMs)));
        adminClient.createTopics(List.of(newTopic)).all().get(); // blocking
    }

    // 토픽 삭제
    public void deleteTopic(String topicName) throws ExecutionException, InterruptedException {
        adminClient.deleteTopics(List.of(topicName)).all().get();
    }

    // 현재 클러스터 토픽 조회
    public List<String> listTopics() throws ExecutionException, InterruptedException {
        return new ArrayList<>(adminClient.listTopics().names().get());
    }
}