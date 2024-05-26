package com.oopsfeedmecode.json_vs_protobuf.model.json;
public class MessageJson {

    public static class RequestMessage {
        private User user;

        // Getters and Setters
        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static class ResponseMessage {
        private String message;
        private int code;

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public static class User {
        private String name;
        private Address address;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Address {
        private String city;

        // Getters and Setters
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}
