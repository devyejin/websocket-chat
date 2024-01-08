package com.example.websocketchat.pubsub;

import com.example.websocketchat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 * Redis 발생 서비스
 * - 채팅방에 사용자가 입장해서 메시지 작성(pub)시 메시지를 Redis Topic(채팅방)에 발행 하는 기능
 * - 이 서비스를 통해 대기하던 redis sub service(구독 서비스)가 메시지를 처리
 */
@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String,Object> redisTemplate;

    //메시지를 redis topic에 발행 -> 대기하던 redis 구독 서비스(RedisSubscriber)가 메시지를 처리
    public void publish(ChannelTopic topic, ChatMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);

    }
}
