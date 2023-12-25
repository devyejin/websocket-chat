package com.example.websocketchat.repository;


import com.example.websocketchat.model.ChatRoom;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoom> chatRoomMap; // roomId를 key로 사용

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>(); //LinkedHashMap은 HashMap과 달리 순서대로 저장
    }

    public List<ChatRoom> findAllRooms() {
        List chatRooms = new ArrayList(chatRoomMap.values());
        Collections.reverse(chatRooms); //채팅방 최신순으로 보여주기
        return chatRooms;
    }

    public ChatRoom findByRoomId(String roomdId) {
        return chatRoomMap.get(roomdId);
    }

    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom().create(roomName);

        //채팅방 목록에 저장
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public void plusUserCnt(String roomdId) {
        ChatRoom chatRoom = chatRoomMap.get(roomdId);
        chatRoom.setUserCount(chatRoom.getUserCount() + 1);
    }

    public void minusUserCnt(String roomId) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.setUserCount(chatRoom.getUserCount() - 1);
    }

    /*
     * TODO : 사용자 중복 체크 로직 ( IN MEMORY -> DB 변경 때)
     */
    public String addUser(String roomId, String userName) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);

        //userList는 key:유저UUID, value:userName
        String userUUID = UUID.randomUUID().toString();
        chatRoom.getUserList().put(userUUID, userName);
        return userUUID;
    }

    //채팅방 유저명 중복 확인
    public String isDuplicateUserName(String roomId, String userName) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        String tmp = userName;

        //만약 중복된 이름이라면, userName+랜덤숫자
        while(chatRoom.getUserList().containsValue(tmp)) {
            int ranNum = (int)(Math.random() * 100) + 1;
            tmp += ranNum;
        }

        return userName;
    }


    public void delUser(String roomId, String userUUID) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.getUserList().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        return chatRoom.getUserList().get(userUUID);
    }

    //채팅방 전체 user 조회 ( Key, value 중 UserName 인 value만 반환 )
    public ArrayList<String> getUserList(String roomId) {

        ChatRoom chatRoom = chatRoomMap.get(roomId);

        ArrayList<String> list = new ArrayList<>();
        chatRoom.getUserList().forEach((key, value) -> list.add(value));

        return list;
    }
}
