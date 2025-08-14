package com.example.opensourceproxy.service;

import com.example.opensourceproxy.dto.ProxyRequest;
import com.example.opensourceproxy.infra.failstore.FailStore;
import com.example.opensourceproxy.infra.failstore.FailStoreEntry;
import com.example.opensourceproxy.infra.kafka.KafkaProducerClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@Service
@RequiredArgsConstructor
public class ProxyRequestService {

    private final KafkaProducerClient producer;
    private final FailStore failStore;

    // 큐 용량 정책: 가득 차면 거부(offer → false)
    private final BlockingQueue<ProxyRequest> queue = new ArrayBlockingQueue<>(1000);

    /** 1) 컨트롤러에서 호출: 큐 적재 */
    public void enqueue(ProxyRequest req) {
        boolean ok = queue.offer(req);
        if (!ok) {
            // 정책: 거부 / 대기(put) / 임시 저장 중 택1
            // 여기선 간단히 거부
            throw new IllegalStateException("Queue is full. Rejecting request.");
        }
    }

    /** 2) 워커 스레드: 큐에서 꺼내 순차 전송 */
    @PostConstruct
    public void startWorker() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    ProxyRequest req = queue.take(); // 블로킹
                    producer.sendAsync(req.getTopic(), req.getKey(), req.getMessage());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 이 레벨에서는 예외만 로깅. 실제 실패 보관은 producer 내부에서 수행
                    // 필요 시 fall-back 저장 추가 가능
                }
            }
        }, "proxy-queue-worker");
        worker.setDaemon(true);
        worker.start();
    }

    /** 3) FailStore 재시도: 10초마다, 최대 5회 */
    @Scheduled(fixedDelay = 10_000)
    public void retryFailStore() {
        for (FailStoreEntry entry : failStore.findAll()) {
            if (entry.getRetryCount() >= 5) {
                // TODO: 알림/모니터링 연동 (Slack/Email)
                continue;
            }
            boolean ok = producer.sendSync(entry.getTopic(), entry.getKey(), entry.getMessage());
            if (ok) {
                failStore.remove(entry.getId());
            } else {
                failStore.bumpRetry(entry.getId(), "retry failed");
            }
        }
    }
}