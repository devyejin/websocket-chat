package com.example.websocketchat.util;

import com.example.websocketchat.controller.ChatController;
import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.model.MessageType;
import com.example.websocketchat.repository.ChatRoomRepository;
import com.example.websocketchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Log4j2
@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations template;
//    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;

    //입장 메시지는 /pub/chat/enterUser 에서 처리
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        log.info("Connect event = {}", event);
        log.info("Connect a new WebSocket session! -- 새로운 사용자 연결");
    }

    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        log.info("Disconnect event = {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("headerAccessor={}",headerAccessor);

        //stomp session에서 userUUID, roomId 확인
        String userUUID = (String)headerAccessor.getSessionAttributes().get("userUUID");
        log.info("userUUID={}",userUUID);
        String roomId = (String)headerAccessor.getSessionAttributes().get("roomId");

//        String userName = chatService.getUserName(roomId, userUUID);
        String userName = chatRoomRepository.getUserName(roomId, userUUID);

//        chatService.minusUserCnt(roomId);
//        chatService.delUser(roomId,userUUID);  //로직추가해야함

        log.info("check disconnection event username = {}", userName);

        if(userName != null) {
            log.info("Disconnect User : " + userName);

            ChatMessage chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(userName)
                    .message(userName + " 님이 퇴장하셨습니다.")
                    .build();

            //구독자들에게 전송
            template.convertAndSend("/sub/chat/room/"+roomId, chatMessage);
        }
    }

}
