package com.example.kafka.service.controller;

import com.example.kafka.service.service.KafkaService;
import com.example.types.kafka.KafkaMessageRs;
import com.example.types.rest.BaseResponseRs;
import com.example.types.rest.Body;
import com.example.types.rest.MessageRq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message")
public class MessageController {
    @Autowired
    private KafkaService kafkaService;

    @PostMapping(path = "/send",consumes = "application/json", produces = "application/json")
    public BaseResponseRs sendMessage(@RequestBody MessageRq request) throws Exception{
        KafkaMessageRs kafkaResponse = kafkaService.send(request.getMessageText());
        BaseResponseRs response = new BaseResponseRs();
        response.setSuccess(true);
        Body body = new Body();
        body.setAdditionalProperty ("messageText", kafkaResponse.getMessageText ());
        response.setBody(body);
        return response;
    }
}
