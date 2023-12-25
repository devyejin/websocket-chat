package com.example.websocketchat.controller;

import com.example.websocketchat.model.ChatRoom;
import com.example.websocketchat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Slf4j
@Controller
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/")
    public String getChatRoom(Model model) {

        model.addAttribute("list", chatRoomRepository.findAllRooms());
        log.info("chat room list = {}", chatRoomRepository.findAllRooms());

        return "roomlist";
    }

    //채팅방 생성 후 채팅방 목록으로 리다이렉트 , 그냥 바로 입장시키는게 더 좋지 않나?
    @PostMapping("/chat/create-room")
    public String createRoom(@RequestParam String roomName, RedirectAttributes redirectAttributes) {
        ChatRoom chatRoom = chatRoomRepository.createChatRoom(roomName);

        return "redirect:/";
    }

    //TODO : refactoring할 때 채팅방 못 찾는경우 예외처리
    //채팅방 입장 "/chat/room/{roomId} roomId param으로 요청 들어옴
    @GetMapping("/chat/room")
    public String roomDetail(Model model, @RequestParam String roomId) {
        log.info("roomId={}", roomId);

        ChatRoom findChatRoom = chatRoomRepository.findByRoomId(roomId);
        model.addAttribute("room", findChatRoom);

        return "chatroom";
    }
}
