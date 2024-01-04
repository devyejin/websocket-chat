package com.example.websocketchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
/*
    - WebSocket만 사용한 경우
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    /*
        endpoint 지정
        즉, /ws/chat 요청 들어오면 websocket 통신 시작 (http -> websocket 프로토콜로 upgrade)
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
       registry.addHandler(webSocketHandler,"/ws/chat").setAllowedOrigins("*");

    }
}
