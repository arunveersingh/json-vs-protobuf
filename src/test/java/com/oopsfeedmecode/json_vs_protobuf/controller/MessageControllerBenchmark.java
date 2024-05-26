package com.oopsfeedmecode.json_vs_protobuf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsfeedmecode.json_vs_protobuf.configuration.ProtobufConfig;
import com.oopsfeedmecode.json_vs_protobuf.controller.MessageController;
import com.oopsfeedmecode.json_vs_protobuf.model.json.MessageJson;
import com.oopsfeedmecode.json_vs_protobuf.model.protobuf.MessageProto;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@State(Scope.Benchmark)
public class MessageControllerBenchmark {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MessageProto.RequestMessage protobufRequestMessage;
    private String jsonRequestMessage;

    @Setup
    public void setUp() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MessageController.class, ProtobufConfig.class);
        objectMapper = new ObjectMapper();

        ProtobufHttpMessageConverter protobufHttpMessageConverter = new ProtobufHttpMessageConverter();
        // crate the instance of http converter for json
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        mockMvc = MockMvcBuilders.standaloneSetup(context.getBean(MessageController.class))
                .setMessageConverters(protobufHttpMessageConverter, mappingJackson2HttpMessageConverter)
                .build();

        // Setup Protobuf request
        protobufRequestMessage = MessageProto.RequestMessage.newBuilder()
                .setUser(MessageProto.User.newBuilder()
                        .setName("John Doe")
                        .setAddress(MessageProto.Address.newBuilder()
                                .setCity("New York")
                                .build())
                        .build())
                .build();

        // Setup JSON request
        MessageJson.RequestMessage requestMessage = new MessageJson.RequestMessage();
        MessageJson.User user = new MessageJson.User();
        MessageJson.Address address = new MessageJson.Address();

        address.setCity("New York");
        user.setName("John Doe");
        user.setAddress(address);
        requestMessage.setUser(user);

        jsonRequestMessage = objectMapper.writeValueAsString(requestMessage);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkProtobuf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/protobuf/message")
                        .contentType("application/x-protobuf")
                        .content(protobufRequestMessage.toByteString().toByteArray()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // Parse the Protobuf response
                    byte[] responseBytes = result.getResponse().getContentAsByteArray();
                    MessageProto.ResponseMessage response = MessageProto.ResponseMessage.parseFrom(responseBytes);

                    // Validate the response
                    org.hamcrest.MatcherAssert.assertThat(response.getMessage(), org.hamcrest.Matchers.equalTo("Hello, John Doe from New York!"));
                    org.hamcrest.MatcherAssert.assertThat(response.getCode(), org.hamcrest.Matchers.equalTo(200));
                });
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkJson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/json/message")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequestMessage))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // Parse the JSON response
                    String responseJson = result.getResponse().getContentAsString();
                    MessageJson.ResponseMessage response = objectMapper.readValue(responseJson, MessageJson.ResponseMessage.class);

                    // Validate the response
                    org.hamcrest.MatcherAssert.assertThat(response.getMessage(), org.hamcrest.Matchers.equalTo("Hello, John Doe from New York!"));
                    org.hamcrest.MatcherAssert.assertThat(response.getCode(), org.hamcrest.Matchers.equalTo(200));
                });
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}