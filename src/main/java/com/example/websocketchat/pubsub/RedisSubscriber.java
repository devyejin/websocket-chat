package com.example.websocketchat.pubsub;

import com.example.websocketchat.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
/*
    대기하다 message가 오는걸 받으니까 MessageListener interface 구현
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 대기하다가 RedisPublisher가 메시지를 발행하면, RedisSubscriber onMessage가 메시지를 받아 처리
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {


        try {
            //redis에서 발행된 데이터를 받아서 deserialize
            String publishMessage = (String)redisTemplate.getStringSerializer().deserialize(message.getBody());

            //ChatMessage 객체로 매핑
            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            //구독자들에게 메시지 전달
            messagingTemplate.convertAndSend("/sub/chat/room/"+roomMessage.getRoomId(),roomMessage);
        } catch (JsonProcessingException e) {
            log.error("error={}", e.getMessage());
        }
    }
}
