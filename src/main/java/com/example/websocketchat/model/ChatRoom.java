package com.example.websocketchat.model;

import com.example.websocketchat.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

/*
    - 채팅방에 입장한 클라이언트 정보 즉, 클라이언트 세션 정보 보유
 */
@Getter
@Setter
@ToString
public class ChatRoom {

    private String roomId;
    private String name; //채팅방 이름
    private Set<WebSocketSession> sessions = new HashSet<>(); // 클라이언트 세션

    @Builder //클래스가 아닌 생성자에 @Builder 사용시 해당 필드에 대해서만 builder 메서드 생성
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    //messageType에 따른 일 처리
    public void handleAction(WebSocketSession session, ChatDTO message, ChatService chatService) {
        MessageType messageType = message.getType();

        switch (message.getType()) {
            case JOIN -> {
                sessions.add(session);
                message.setMessage(message.getSender()+ " 님이 입장하셨습니다.");
                sendMessage(message, chatService);
            }
            case CHAT -> {
//                message.setMessage(message.getMessage()); <-- 불필요같은데
                sendMessage(message, chatService);
            }
            case LEAVE -> {
                sessions.add(session);
                message.setMessage(message.getSender()+ " 님이 퇴장하셨습니다.");
                sendMessage(message,chatService);
            }
        }
    }

    //해당 ChatRoom 참여자 (session) 들에게 메시지 전송
    public <T>void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream().forEach(session -> {
            chatService.sendMessage(session, message);
        });
    }
}

