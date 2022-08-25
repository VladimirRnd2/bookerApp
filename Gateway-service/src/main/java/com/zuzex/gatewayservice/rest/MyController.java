package com.zuzex.gatewayservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.gatewayservice.model.AuthorRequest;
import com.zuzex.gatewayservice.model.AuthorRs;
import com.zuzex.gatewayservice.model.BookRequest;
import com.zuzex.gatewayservice.model.BookRs;
import com.zuzex.gatewayservice.receiver.KafkaReactiveConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("user/book/{service}")
public class MyController {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final KafkaReactiveConsumer reactiveConsumer;

    public MyController(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate, KafkaReactiveConsumer reactiveConsumer) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.reactiveConsumer = reactiveConsumer;
    }

//    @PostMapping(value = "/search")
//    public ResponseEntity<BookRs> createNewGoogleBook(@RequestBody @Valid BookRequest bookRequest,
//                                                      HttpServletRequest request,
//                                                      @PathVariable(name = "service") String service) {
//        BookRs book = null;
//        try {
//            String rq = objectMapper.writeValueAsString(bookRequest);
//            ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/search",rq);
//            producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//            producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//            producerRecord.headers().add(new RecordHeader("user.name",request.getHeader("principal").getBytes(StandardCharsets.UTF_8)));
//
//            RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//
//            ConsumerRecord<String,String> record = future.get();
//            book = objectMapper.readValue(record.value(), BookRs.class);
//        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        return new ResponseEntity<BookRs>(book, HttpStatus.CREATED);
//    }
//
//    @PostMapping(value = "/authorsearch")
//    public List<BookRs> createGoogleBooksByAuthor(@RequestBody @Valid AuthorRequest authorRequest,
//                                                  HttpServletRequest request,
//                                                  @PathVariable(name = "service") String service) {
//        List<BookRs> bookRsList = new ArrayList<>();
//        try {
//            String rq = objectMapper.writeValueAsString(authorRequest);
//            ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/authorsearch",rq);
//            producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//            producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//            producerRecord.headers().add(new RecordHeader("user.name",request.getAttribute("principal").toString().getBytes(StandardCharsets.UTF_8)));
//            RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//
//            ConsumerRecord<String,String> record = future.get();
//            bookRsList = objectMapper.readValue(record.value(), new TypeReference<List<BookRs>>() {});
//        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return bookRsList;
//    }

    @GetMapping(value = "/")
    public Flux<BookRs> getAllGoogleBooks(HttpServletRequest request, @PathVariable(name = "service") String service) throws JsonProcessingException {
        List<BookRs> bookRsList = new ArrayList<>();
        String u_id = UUID.randomUUID().toString();
        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/","getAllGoogleBooks");
        producerRecord.headers().add(new RecordHeader("u.id",u_id.getBytes(StandardCharsets.UTF_8)));
        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));

        kafkaTemplate.send(producerRecord);
        while (true) {
            if(reactiveConsumer.getResultMap().containsKey(u_id)) {
                bookRsList = objectMapper.readValue(reactiveConsumer.getResultMap().get(u_id), new TypeReference<List<BookRs>>() {
                });
                return Flux.fromIterable(bookRsList);
            }
        }
    }

//    @GetMapping(value = "/read")
//    public List<BookRs> getAllReadGoogleBooks(HttpServletRequest request, @PathVariable(name = "service") String service) {
//        List<BookRs> bookRsList = new ArrayList<>();
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/read","getAllReadGoogleBooks");
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            bookRsList = objectMapper.readValue(record.value(), new TypeReference<>() {});
//        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return bookRsList;
//    }
//
//    @GetMapping(value = "/noread")
//    public List<BookRs> getAllNoReadGoogleBooks(HttpServletRequest request, @PathVariable(name = "service") String service) {
//        List<BookRs> bookRsList = new ArrayList<>();
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/noread","getAllNoReadGoogleBooks");
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            bookRsList = objectMapper.readValue(record.value(), new TypeReference<List<BookRs>>() {});
//        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return bookRsList;
//    }
//
//    @GetMapping(value = "/{id}")
//    public BookRs getGoogleBookById(@PathVariable(name = "id") @Min(0) Long id, HttpServletRequest request, @PathVariable(name = "service") String service) {
//        BookRs book = null;
//
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/{id}",id.toString());
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            book = objectMapper.readValue(record.value(),BookRs.class);
//        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return book;
//    }
//
//    @GetMapping(value = "/{id}/isread")
//    public boolean addToReadGoogleBooks(@PathVariable(name = "id") @Min(0) Long id, HttpServletRequest request, @PathVariable(name = "service") String service) {
//        boolean result = false;
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/{id}/isread",id.toString());
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            result = objectMapper.readValue(record.value(), Boolean.class);
//        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    @GetMapping(value = "/{id}/isnoread")
//    public boolean deleteFromReadGoogleBooks(@PathVariable(name = "id") @Min(0) Long id, HttpServletRequest request, @PathVariable(name = "service") String service) {
//        boolean result = false;
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/{id}/isnoread",id.toString());
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            result = objectMapper.readValue(record.value(), Boolean.class);
//        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    @GetMapping(value = "/{id}/authors")
//    public List<AuthorRs> getAllAuthorsByGoogleBookId(@PathVariable(name = "id") @Min(0) Long id, HttpServletRequest request, @PathVariable(name = "service") String service){
//        List<AuthorRs> authorRsList = new ArrayList<>();
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/{id}/authors",id.toString());
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            authorRsList = objectMapper.readValue(record.value(), new TypeReference<List<AuthorRs>>() {});
//        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return authorRsList;
//    }
//
//    @DeleteMapping(value = "/{id}")
//    public String deleteGoogleBook(@PathVariable(name = "id") @Min(0) Long id, HttpServletRequest request, @PathVariable(name = "service") String service) {
//
//        String result = null;
//        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("bookRq",service + "/user/book/{id}/delete",id.toString());
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "bookRs".getBytes(StandardCharsets.UTF_8)));
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID,service.getBytes(StandardCharsets.UTF_8)));
//
//        RequestReplyFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
//        try {
//            ConsumerRecord<String,String> record = future.get();
//            result = record.value();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
