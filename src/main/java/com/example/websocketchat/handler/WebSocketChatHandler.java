package com.example.websocketchat.handler;

/*
    - STOMP 없이 WebSocket만 이용하는 경우
    - 웹 소켓 클라이언트로부터 채팅 메시지 전달받아 객체로 변환
    - 전달받은 객체에 담긴 chat room id로 채팅방 정보 조회
    - 채팅방에 입장한 모든 클라이언트(WebSocket Session)에게 타입에 따른 메시지 발송
 */
//@Slf4j
//@RequiredArgsConstructor
////@Component
//public class WebSocketChatHandler extends TextWebSocketHandler {
//
//    private final ObjectMapper mapper;
//    private final ChatService chatService;
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//
//        String payload = message.getPayload();
//        log.info("payload={}", payload);
//
//        //STOMP미적용시에는 직접 Mapper로 변환 작업
//        ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);
//        log.info("chatMessage={}",chatMessage); //session에 해당하나? 찍어보고 이름 session/chatMessage 결정하기
//
//        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());
//
//        room.handleAction(session,chatMessage,chatService); //Chatroom에서 messageType에 따라 처리
//    }
//}
