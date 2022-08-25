package com.zuzex.gatewayservice.receiver;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Component
@Data
public class KafkaReactiveConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReactiveConsumer.class);

    private static final String BOOTSTRAP_SERVERS = "localhost:29092";
    private static final String TOPIC = "bookRs";

    private final KafkaReceiver<String,String> kafkaReceiver;
    private final Map<String,String> resultMap = new ConcurrentHashMap<>();

    @Autowired
    public KafkaReactiveConsumer(KafkaReceiver<String, String> kafkaReceiver) {
        this.kafkaReceiver = kafkaReceiver;
    }

    @EventListener(ContextRefreshedEvent.class)
    public Disposable getFlux() {
        return kafkaReceiver.receive()
                .doOnNext(r -> {
                    resultMap.put(r.headers().lastHeader("u.id").value().toString(), r.value());
                    r.receiverOffset().acknowledge();
                })
                .doOnError(Throwable::printStackTrace)
                .map(r -> {
                    String value = r.value();
                    System.out.println(value);
                    return value;
                })
                .publishOn(Schedulers.newParallel("lol"))
                .subscribe();
    }
}
