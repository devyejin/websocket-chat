package com.example.websocketchat.controller;

import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.model.MessageType;
import com.example.websocketchat.pubsub.RedisPublisher;
import com.example.websocketchat.repository.ChatRoomRepository;
import com.example.websocketchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;

/*
    채팅 수신(sub), 송신(pub)
 */
@Slf4j
@RequiredArgsConstructor
//@RequestMapping("/chat")
@Controller
public class ChatController {

    private final ChatService chatService;

    //redis 추가
    private final RedisPublisher redisPublisher; // 클라이언트 메시지 -> 컨트롤러 -> Redis Pub
    private final ChatRoomRepository chatRoomRepository;

    //Spring framework의 메시징 기능 클래스, WebSocket 통신 지원, 메시지 교환 쉽게 처리 가능, @SendTo 어노테이션 역할 대신 함
    private final SimpMessageSendingOperations template; // SimpMessageSendingOperations는 인터페이스, 대표적 구현체 SimpMessagingTemplate 클래스

    /*
        @MessageMapping
        - MessagBroker설정에서 registry.setApplicationDestinationPrefixes("/pub");
          "/pub"으로 시작하는 메시지들은 mesage-handling methods 즉, @MessageMapping어노테이션이 붙은 메서드가 처리 (라우팅)
     */
    @MessageMapping("/chat/sendMessage") //즉, 이 메서드의 엔드포인트는 "/pub/chat/sendMessage"
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(Instant.now().toString()); //사실 이 부분도 service나 repository에서 하는게 맞음

        log.info("chatMessage={}", chatMessage);
//        chatMessage.setMessage(chatMessage.getMessage()); 불필요한 로직같음
        //서버에서 메시지를 받고, sub들에게 전송
//        template.convertAndSend("/sub/chat/room/"+ chatMessage.getRoomId(), chatMessage);

        //이제는 redis pub/sub에서 처리  (topic=구독중인 채팅방)
        redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);

    }

    //퇴장은 EventListener를 통해서 확인
    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("enterUser 메서드 호출!!");
        log.info("chatMessage={}", chatMessage);
        //채팅방 인원 + 1
        chatService.plusUserCnt(chatMessage.getRoomId());
//
//        //채팅방에 사용자 추가
//        String userUUID = chatService.addUser(chatMessage.getRoomId(), chatMessage.getSender());
//
//        //WebSocket Session에 userUUID 저장
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());
//
//        chatMessage.setMessage(chatMessage.getSender()+ " 님이 입장하셨습니다.");
//        //방 참여자들에게 전송
//        template.convertAndSend("/sub/chat/room/"+ chatMessage.getRoomId(), chatMessage);


        //redis pub/sub에서 처리
        if(MessageType.JOIN.equals(chatMessage.getType())) {
            chatRoomRepository.enterChatRoom(chatMessage.getRoomId());
            chatMessage.setMessage(chatMessage.getSender()+"님이 입장하셨습니다.");
        }

        //websocket 발행 메시지 -> redis로 발행
        redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);

    }

    @GetMapping("/chat/user-list")
    @ResponseBody
    public ArrayList<String> userList(String roomId) {
        log.info("roomId={}", roomId);
        return chatService.getUserList(roomId);
    }


    @GetMapping("/chat/duplicate-username")
    @ResponseBody
    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username") String username ) {

        log.info("roomId={}, username={}", roomId,username);

        //중복 있는 경우 숫자 붙여서 반환
        String userName = chatService.isDuplicateName(roomId, username);
        log.info("userName ={}", userName);

        return userName;
    }



}
