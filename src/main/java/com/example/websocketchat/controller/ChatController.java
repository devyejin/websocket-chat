package com.example.websocketchat.controller;


import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

// 한명의 Client로부터 mewssage를 받고 다른 사람들한테 broadcasting하는 역할
@Log4j2
@Controller
@RequiredArgsConstructor
public class ChatController {

    /*
        @MessageMapping : WebSocket Config랑 맞물림
        MessageBroker Config에
            registry.setApplicationDestinationPrefixes("/app"); client가 보내는 /app"으로 시작하는 메시지들이 mesage-handling methods(?)로 라우팅되도록 정의 했음
            바로 mesage-handling methods를 지칭하는게 @MessageMapping 메서드

        그래서, 예를 들어 메시지 destination이 /app/chat.sendMessage 이면 sendMessage() 메서드가 라우팅되고
                                           /app/chat.addUser 이면 addUser() 메서드가 라우팅됨

    */

    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public") // 메시지 출력을 못해서 원인찾다보니 어디로 보낼지 대상 뺴먹었네!
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        log.info("chatMessage={}",chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {

        //채팅방에 사용자 인원 수 추가
        chatRoomRepository.plusUserCnt(chatMessage.getRoomId());

        //채팅방에 유저 추가, 유저 UUID 반환
        String userUUID = chatRoomRepository.addUser(chatMessage.getRoomId(), chatMessage.getSender());

        // Websocket session에 username 추가 --> username은 중복될 수 있으니까 userUUID로 저장!
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());

        return chatMessage;
    }


    //채팅방 참여 유저 리스트
    @GetMapping("/chat/user-list")
    @ResponseBody
    public ArrayList<String> userList(String roomId) {
        return chatRoomRepository.getUserList(roomId);
    }

    //username 중복 확인
    @GetMapping("/chat/dulicate-username")
    @ResponseBody
    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username")String username) {

        String userName = chatRoomRepository.isDuplicateUserName(roomId, username);
        log.info("userName={}", userName);

        return userName;
    }
}
