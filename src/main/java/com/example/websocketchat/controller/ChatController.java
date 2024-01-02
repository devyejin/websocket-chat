package com.example.websocketchat.controller;

import com.example.websocketchat.model.ChatDTO;
import com.example.websocketchat.model.ChatRoom;
import com.example.websocketchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatRoom createRoom(@RequestParam String name) {
        log.info("room name={}", name);
        return chatService.createRoom(name);
    }

    @GetMapping
    public List<ChatRoom> findAllRooms() {
        return chatService.findAllRoom();
    }


}
