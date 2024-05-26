package com.oopsfeedmecode.json_vs_protobuf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsfeedmecode.json_vs_protobuf.controller.MessageController;
import com.oopsfeedmecode.json_vs_protobuf.model.json.MessageJson;
import com.oopsfeedmecode.json_vs_protobuf.model.protobuf.MessageProto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MessageController.class)
public class BenchMarkLoop {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int ITERATIONS = 1_000_00;

    @Test
    public void benchmarkProtobuf() throws Exception {
        // Create a RequestMessage object for Protobuf
        MessageProto.RequestMessage requestMessage = MessageProto.RequestMessage.newBuilder()
                .setUser(MessageProto.User.newBuilder()
                        .setName("John Doe")
                        .setAddress(MessageProto.Address.newBuilder()
                                .setCity("New York")
                                .build())
                        .build())
                .build();

        long totalTime = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/protobuf/message")
                            .contentType("application/x-protobuf")
                            .content(requestMessage.toByteString().toByteArray()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType("application/x-protobuf"))
                    .andExpect(result -> {
                        // Parse the Protobuf response
                        byte[] responseBytes = result.getResponse().getContentAsByteArray();
                        MessageProto.ResponseMessage response = MessageProto.ResponseMessage.parseFrom(responseBytes);

                        // Validate the response
                        org.hamcrest.MatcherAssert.assertThat(response.getMessage(), org.hamcrest.Matchers.equalTo("Hello, John Doe from New York!"));
                        org.hamcrest.MatcherAssert.assertThat(response.getCode(), org.hamcrest.Matchers.equalTo(200));
                    });

            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        long averageTime = totalTime / ITERATIONS;
        System.out.println("Average Protobuf request time: " + averageTime + " ns");
    }

    @Test
    public void benchmarkJson() throws Exception {
        // Create a RequestMessage object for JSON
        MessageJson.RequestMessage requestMessage = new MessageJson.RequestMessage();
        MessageJson.User user = new MessageJson.User();
        MessageJson.Address address = new MessageJson.Address();

        address.setCity("New York");
        user.setName("John Doe");
        user.setAddress(address);
        requestMessage.setUser(user);

        String requestJson = objectMapper.writeValueAsString(requestMessage);

        long totalTime = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/json/message")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello, John Doe from New York!"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));

            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        long averageTime = totalTime / ITERATIONS;
        System.out.println("Average JSON request time: " + averageTime + " ns");
    }
}