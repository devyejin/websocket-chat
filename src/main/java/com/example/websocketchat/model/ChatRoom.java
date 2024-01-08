package com.example.websocketchat.model;

import com.example.websocketchat.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
    - 채팅방에 입장한 클라이언트 정보 즉, 클라이언트 세션 정보 보유
 */
@Getter
@Setter
@ToString
public class ChatRoom implements Serializable { //Redis에 저장하는 객체들은 Serialize 가능해야 함 -> Serializable 구현

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private String roomName; //채팅방 이름
    private long userCount; //채팅방 인원 수

    private HashMap<String,String> userList = new HashMap<String,String>();

    //생성자로 하는게 더 깔끔할거같기도하면서도, 메서드명 명확한게 더 나은것도 같고..
    public static ChatRoom create(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;

        return chatRoom;
    }

    //STOMP 프로토콜을 이용시 pub/sub을 통해 구독자,송신자 같은 session관리, 메시지전송 기능 이용, 직접 구현할 필요가 없음
//    private Set<WebSocketSession> sessions = new HashSet<>(); // 클라이언트 세션
//
//    //messageType에 따른 일 처리
//    @Builder //클래스가 아닌 생성자에 @Builder 사용시 해당 필드에 대해서만 builder 메서드 생성
//    public ChatRoom(String roomId, String roomName) {
//        this.roomId = roomId;
//        this.roomName = roomName;
//    }
//
//
//
//    public void handleAction(WebSocketSession session, ChatMessage message, ChatService chatService) {
//        MessageType messageType = message.getType();
//
//        switch (message.getType()) {
//            case JOIN -> {
//                sessions.add(session);
//                message.setMessage(message.getSender()+ " 님이 입장하셨습니다.");
//                sendMessage(message, chatService);
//            }
//            case CHAT -> {
////                message.setMessage(message.getMessage()); <-- 불필요같은데
//                sendMessage(message, chatService);
//            }
//            case LEAVE -> {
//                sessions.add(session);
//                message.setMessage(message.getSender()+ " 님이 퇴장하셨습니다.");
//                sendMessage(message,chatService);
//            }
//        }
//    }
//
//    //해당 ChatRoom 참여자 (session) 들에게 메시지 전송
//    public <T>void sendMessage(T message, ChatService chatService) {
//        sessions.parallelStream().forEach(session -> {
//            chatService.sendMessage(session, message);
//        });
    }


