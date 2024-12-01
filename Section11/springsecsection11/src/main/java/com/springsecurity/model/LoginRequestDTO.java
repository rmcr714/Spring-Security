package com.springsecurity.model;


//Record classes are new feature in java where we can create a class like this and java would create pojo out of it
public record LoginRequestDTO(String username, String password) {
}