package com.example.websocketchat.controller;

import com.example.websocketchat.model.ChatRoom;
import com.example.websocketchat.repository.ChatRoomRepository;
import com.example.websocketchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
    채팅방 생성, 조회, 입장
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
@Controller
public class ChatRoomController {

//    private final ChatService chatService; //redis 사용 이전 버전
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/rooms")
    public String getChatRooms(Model model) {
        log.info("채팅방 목록 컨트롤러 호출!");

        model.addAttribute("list", chatRoomRepository.findAllRoom());
        return "room-list";
    }

    @PostMapping("/create")
    public String createChatRoom(@RequestParam String roomName, RedirectAttributes redirectAttributes) {
        ChatRoom chatRoom = chatRoomRepository.createChatRoom(roomName);
        redirectAttributes.addFlashAttribute("chatRoom", chatRoom);

        log.info("채팅방 생성 chatRoom={}", chatRoom);

        return "redirect:/chat/rooms";
    }

    @GetMapping("/room")
    public String chatRoomDetail(Model model, @RequestParam("roomId") String roomId) {
        log.info("roomId={}", roomId);

        model.addAttribute("room", chatRoomRepository.findRoomById(roomId));
        return "chat-room";
    }

}
