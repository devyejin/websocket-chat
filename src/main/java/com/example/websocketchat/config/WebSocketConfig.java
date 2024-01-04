package com.example.websocketchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker // WebSocket server 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { //WebSocket Connection 설정

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { //client가 Websocket Server에 연결하는데 사용하는 endpoint
        registry.addEndpoint("/ws-stomp")
                .withSockJS(); //SockJS는 webSocket을 미지원하는 브라우저에서 fallback option(대체 기능) 활성화에 사용 -> SocketJs사용
    }

    // pub = app,  topic=sub
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { //한 클라이언트에서 다른 클라이언트로 메시지를 라우팅(A->B로 메시지 전달되는 작업)하는데 사용할 메시지 브로커 구성
        //메시지 발행 요청 url
        registry.setApplicationDestinationPrefixes("/pub"); // destination이 "/pub"으로 시작하는 메시지들이 mesage-handling methods( @MessageMapping 어노테이션이 붙은 메서드)로 라우팅되도록 정의
        //메시지 구독 요청 url
        registry.enableSimpleBroker("/sub"); //destination이 "sub"으로 시작하는 메시지들이 message-broker로 라우팅되도록 정의, message-broker는 messages들을 브로드캐스팅함, 특정 토픽을 구독하고 있는 연결된 클라이언트 모두에게!

        //지금은 simple in-memory message broker 사용함. (즉, 앱 종료시 날라가겠지)
        // 추후 RabbitMQ, ActiveMQ 같은거로 이용!

    }
}
