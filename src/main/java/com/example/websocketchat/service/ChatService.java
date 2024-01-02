package com.example.websocketchat.service;

import com.example.websocketchat.model.ChatRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

/*
    사용자가 입력한 RoomName으로는 채팅방 이름 지정, id값은 UUID
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

    private final ObjectMapper mapper;
    private Map<String, ChatRoom> chatRooms; // 임시 db


    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //db연동시 repository클래스로 옮길 로직들
    public List<ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    public ChatRoom createRoom(String name) {
        String roomId = UUID.randomUUID().toString();

        ChatRoom room = ChatRoom.builder()
                .roomId(roomId)
                .name(name)
                .build();

        chatRooms.put(roomId, room); //db연동시 수정할 부분
        return room;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        //WebSocket 제공 기능
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error("error={}", e.getMessage());
            throw new RuntimeException(e);
        }
    }



}
