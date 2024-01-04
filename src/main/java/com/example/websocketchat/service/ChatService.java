package com.example.websocketchat.service;

import com.example.websocketchat.model.ChatRoom;
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

//    private final ObjectMapper mapper; stomp에서 해줌
    private Map<String, ChatRoom> chatRoomMap; // 임시 db


    @PostConstruct // ChatService 객체가 생성된 후 초기화 됨
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    //db연동시 repository클래스로 옮길 로직들
    public List<ChatRoom> findAllRoom() {
        List<ChatRoom> chatRooms = new ArrayList<>(this.chatRoomMap.values());
        Collections.reverse(chatRooms);

        return chatRooms; //생성 최신순으로 반환
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomMap.get(roomId);
    }

    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom().create(roomName);

        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);

        return chatRoom;
    }

    public void plusUserCnt(String roomId) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.setUserCount(chatRoom.getUserCount()+1);
    }

    public void minusUserCnt(String roomId) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.setUserCount(chatRoom.getUserCount()-1);
    }

    public String addUser(String roomId, String userName) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);

        //user도 userId(UUID)로 관리
        String userUUID = UUID.randomUUID().toString();
        chatRoom.getUserList().put(userUUID,userName);

        return userUUID;
    }

    //유저 이름 충복 체크, 중복시 랜덤 숫자 부여
    public String isDuplicateName(String roomId, String userName) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);

        while(chatRoom.getUserList().containsValue(userName)) {
            int ranNum = (int) (Math.random() * 100) + 1;

            userName += ranNum;
        }

        return userName;
    }


    public void delUser(String roomId, String userUUID) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.getUserList().remove(userUUID);
    }

    public String getUserName(String roomId, String userUUID) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        return chatRoom.getUserList().get(userUUID);
    }

    public ArrayList<String> getUserList(String roomId) {
        ArrayList<String> list = new ArrayList<>();

        ChatRoom chatRoom = chatRoomMap.get(roomId);

        chatRoom.getUserList().forEach((key,value) -> list.add(value)); // uuid제외하고 userName만 반환
        return list;
    }



//    public <T> void sendMessage(WebSocketSession session, T message) {
//        //WebSocket 제공 기능
//        try {
//            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
//        } catch (IOException e) {
//            log.error("error={}", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }



}
