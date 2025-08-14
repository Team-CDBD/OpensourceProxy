package com.example.opensourceproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProxyRequest {

    private String topic;
    private String key;
    private String message;
}