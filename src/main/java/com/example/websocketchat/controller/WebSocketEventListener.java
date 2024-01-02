package com.example.websocketchat.controller;

import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.model.MessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/*
    socket 연결, 연결해제 이벤트 감지,
    WebSocketEventListener를 이용해서 log, user 참여, 나가기 등을 broadcast!
 */
@RequiredArgsConstructor
//@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);


    private final SimpMessageSendingOperations messagingTemplate;

    // ChatController의 addUser() 메서드에서 /app/chat.addUser 시,입장을 broadcast했음 (topic 구독자들에게)
    //   그래서 별도 작업은 필요 없고, log만 찍음
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.info("Received a new web socekt connection -- 새로운 사용자 연결!");
    }

    //Leaver 이벤트의 경우, Session에서 username뽑아내서, 나가는 username 브로드캐스트
    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String)headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.info("User Disconnected : " + username);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
