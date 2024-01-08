package com.example.websocketchat.repository;

import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.model.ChatRoom;
import com.example.websocketchat.pubsub.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    //topic(채팅방)에서 pub(발행)되는 메시지 처리할 listener
    private final RedisMessageListenerContainer redisMessageListener;

    //구독(sub) 처리 서비스
    private final RedisSubscriber redisSubscriber;

    //redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
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
        return opsHashChatRoom.values(CHAT_ROOMS);
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

    public void joinChatRoom(String roomId) {
    }
}
