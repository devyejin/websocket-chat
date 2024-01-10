package com.example.websocketchat.repository;

import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.model.ChatRoom;
import com.example.websocketchat.pubsub.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    //topic(채팅방)에서 pub(발행)되는 메시지 처리할 listener
    private final RedisMessageListenerContainer redisMessageListener;

    //구독(sub) 처리 서비스
    private final RedisSubscriber redisSubscriber;

    //redis
    private static final String CHAT_ROOMS = "CHAT_ROOM"; //redis에서 key값으로 사용 (종류 분류?)
    private final RedisTemplate<String,Object> redisTemplate;
    private HashOperations<String,String, ChatRoom> opsHashChatRoom;

    //redis topic(채팅방) 정보, 서버별로 채팅방에 매칭되는 topic 정보를 Map에 넣어 roomId로 찾을 수 있음
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        this.opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
//        return opsHashChatRoom.values(CHAT_ROOMS);

        List<ChatRoom> chatRoomList = opsHashChatRoom.values(CHAT_ROOMS);
        chatRoomList.stream().forEach(chatRoom -> {
            log.info("chatRoom={}", chatRoom);
        });

        return chatRoomList;
    }

    public ChatRoom findRoomById(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId); // (key, hashkey)
    }


    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장
     */
    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = ChatRoom.create(roomName);
        // key, hashkey, value
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);

        return chatRoom;
    }

    /**
     * 채팅방 입장 :  redis에 topic 생성 & pub/sub 통신을 위한 리스너 설정
     */
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);

        if(topic == null) {
            topic = new ChannelTopic(roomId);
        }

        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }


//
//    public Object getTopic(String roomId, ChatMessage chatMessage) {
//
//
//    }


    public ConcurrentHashMap<String, String> getUserList(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);

        if(chatRoom == null) {
            log.error("roomId={} 에 해당하는 ChatRoom이 존재하지 않음", roomId);
            throw new NoSuchElementException(roomId); //임시 예외처리, 명확히 수정
        }

        log.info("chatRoom={}", chatRoom);

        ConcurrentHashMap<String, String> userList = chatRoom.getUserList();
        return userList;
    }

    /**
     * TODO : 예외처리 명확히 하기
     */

    public String getUserName(String roomId, String userUUID) {
        ConcurrentHashMap<String, String> userList = getUserList(roomId);

        if(userList.isEmpty()) {
            log.error("userList가 존재하지 않음 , roomId={}", roomId);
            throw new NoSuchElementException(roomId);
        }

        return userList.get(userUUID);
    }


    public String addUser(String roomId, String userName) {

        log.info("======== addUser 호출============");
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);

        if(chatRoom == null) {
            log.error("roomId={} 에 해당하는 ChatRoom이 존재하지 않음", roomId);
            throw new NoSuchElementException(roomId); //임시 예외처리, 명확히 수정
        }

        log.info("chatRoom={}",chatRoom);

        String userUUID = UUID.randomUUID().toString();
        chatRoom.getUserList().put(userUUID, userName);

        opsHashChatRoom.put(CHAT_ROOMS,roomId,chatRoom);

        return userUUID;
    }
}
