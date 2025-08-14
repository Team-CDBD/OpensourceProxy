package com.example.opensourceproxy.controller;

import com.example.opensourceproxy.dto.ProxyRequest;
import com.example.opensourceproxy.service.ProxyRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@WebMvcTest(controllers = ProxyRequestController.class)
@Import(LogControllerTest.Config.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProxyRequestService proxyService; // Mockito mock

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void http호출() throws Exception {
        ProxyRequest payload = new ProxyRequest();
        payload.setTopic("app.errors");
        payload.setKey("opt");
        payload.setMessage("테스트");

        mockMvc.perform(post("/logs")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("queued"));

        verify(proxyService, times(1)).enqueue(payload);
    }

    @TestConfiguration
    static class Config {
        @Bean
        public ProxyRequestService proxyService() {
            return Mockito.mock(ProxyRequestService.class);
        }
    }
}