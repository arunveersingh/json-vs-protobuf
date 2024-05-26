package com.oopsfeedmecode.json_vs_protobuf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsfeedmecode.json_vs_protobuf.model.json.MessageJson;
import com.oopsfeedmecode.json_vs_protobuf.model.protobuf.MessageProto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetMessage() throws Exception {
        MessageProto.User user = MessageProto.User.newBuilder()
                .setName("John Doe")
                .setAddress(MessageProto.Address.newBuilder()
                        .setCity("New York")
                        .build())
                .build();

        MessageProto.RequestMessage request = MessageProto.RequestMessage.newBuilder()
                .setUser(user)
                .build();

        // Perform the request and expect a Protobuf response
        byte[] responseBytes = mockMvc.perform(MockMvcRequestBuilders.post("/api/protobuf/message")
                        .contentType("application/x-protobuf")
                        .content(request.toByteString().toByteArray()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        // Parse the Protobuf response
        MessageProto.ResponseMessage response = MessageProto.ResponseMessage.parseFrom(responseBytes);

        // Validate the response
        Assertions.assertEquals("Hello, John Doe from New York!", response.getMessage());
        Assertions.assertEquals(200, response.getCode());
    }

    @Test
    public void testGetHelloMessageJson() throws Exception {
        // Create a RequestMessage object
        MessageJson.RequestMessage requestMessage = new MessageJson.RequestMessage();
        MessageJson.User user = new MessageJson.User();
        MessageJson.Address address = new MessageJson.Address();

        address.setCity("New York");
        user.setName("John Doe");
        user.setAddress(address);
        requestMessage.setUser(user);

        // Convert the RequestMessage object to JSON
        String requestJson = objectMapper.writeValueAsString(requestMessage);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/json/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello, John Doe from New York!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }
}
