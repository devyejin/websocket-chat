package com.example.websocketchat.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ChatRoom {

    private String roomId; //UUID로 생성
    private String roomName;
    private long userCount; //채팅방 참여 인원수

    // key:유저UUID, value:userName
    private HashMap<String,String> userList = new HashMap<String,String>();

    public ChatRoom create(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;

        return chatRoom;
    }

}
