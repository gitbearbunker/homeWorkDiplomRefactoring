package ru.netology.homeworkdiplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {

    @JsonProperty("auth-token")
    private String token;
}