package com.oopsfeedmecode.json_vs_protobuf.controller;

import com.oopsfeedmecode.json_vs_protobuf.model.json.MessageJson;
import com.oopsfeedmecode.json_vs_protobuf.model.protobuf.MessageProto;
import com.oopsfeedmecode.json_vs_protobuf.model.protobuf.MessageProto.ResponseMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MessageController {

    @PostMapping(value = "/protobuf/message",
            consumes = MediaType.APPLICATION_PROTOBUF_VALUE,
            produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    public ResponseMessage getHelloMessageProtobuf(@RequestBody MessageProto.RequestMessage request) {
        MessageProto.User user = request.getUser();
        String responseText =
                "Hello, " + user.getName() + " from "
                        + user.getAddress().getCity() + "!";

        return ResponseMessage.newBuilder()
                .setMessage(responseText)
                .setCode(200)
                .build();
    }

    @PostMapping(value = "/json/message",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageJson.ResponseMessage getHelloMessageJson(@RequestBody MessageJson.RequestMessage request) {
        MessageJson.User user = request.getUser();
        String responseText =
                "Hello, " + user.getName() + " from "
                        + user.getAddress().getCity() + "!";

        MessageJson.ResponseMessage response = new MessageJson.ResponseMessage();
        response.setMessage(responseText);
        response.setCode(200);
        return response;
    }
}