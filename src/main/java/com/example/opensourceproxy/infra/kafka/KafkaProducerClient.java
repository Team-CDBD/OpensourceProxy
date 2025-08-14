package com.example.opensourceproxy.infra.kafka;

import com.example.opensourceproxy.infra.failstore.FailStore;
import com.example.opensourceproxy.infra.failstore.FailStoreEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KafkaProducerClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FailStore failStore;

    public void sendAsync(String topic, String key, String message) {
        if (key == null || key.isBlank()) {
            kafkaTemplate.send(topic, message).whenComplete((result, ex) -> {
                if (ex != null) {
                    failStore.save(FailStoreEntry.of(topic, null, message, ex.getMessage()));
                }
            });
        } else {
            kafkaTemplate.send(topic, key, message).whenComplete((result, ex) -> {
                if (ex != null) {
                    failStore.save(FailStoreEntry.of(topic, key, message, ex.getMessage()));
                }
            });
        }
    }

    /** 재시도용 동기 전송 */
    public boolean sendSync(String topic, String key, String message) {
        try {
            if (key == null || key.isBlank()) {
                kafkaTemplate.send(topic, message).get();
            } else {
                kafkaTemplate.send(topic, key, message).get();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
