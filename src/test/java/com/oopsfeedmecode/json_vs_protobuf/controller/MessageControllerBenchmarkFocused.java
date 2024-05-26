package com.oopsfeedmecode.json_vs_protobuf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsfeedmecode.json_vs_protobuf.model.json.MessageJson;
import com.oopsfeedmecode.json_vs_protobuf.model.protobuf.MessageProto;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MessageControllerBenchmarkFocused {

    private ObjectMapper objectMapper;

    private MessageProto.RequestMessage protobufRequestMessage;
    private MessageJson.RequestMessage jsonRequestMessage;
    private byte[] protobufRequestMessageByte;
    private String jsonRequestMessageString;



    @Setup
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();

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
        jsonRequestMessage = new MessageJson.RequestMessage();
        MessageJson.User user = new MessageJson.User();
        MessageJson.Address address = new MessageJson.Address();

        address.setCity("New York");
        user.setName("John Doe");
        user.setAddress(address);
        jsonRequestMessage.setUser(user);

       protobufRequestMessageByte = protobufRequestMessage.toByteArray();

       jsonRequestMessageString = objectMapper.writeValueAsString(jsonRequestMessage);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkProtobufSerialization() throws Exception {
        byte[] data = protobufRequestMessage.toByteArray();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkProtobufDeserialization() throws Exception {

        MessageProto.RequestMessage deserializedMessage = MessageProto.RequestMessage.parseFrom(protobufRequestMessageByte);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkJsonSerialization() throws Exception {
        String data = objectMapper.writeValueAsString(jsonRequestMessage);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkJsonDeserialization() throws Exception {
        MessageJson.RequestMessage deserializedMessage = objectMapper.readValue(jsonRequestMessageString, MessageJson.RequestMessage.class);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
