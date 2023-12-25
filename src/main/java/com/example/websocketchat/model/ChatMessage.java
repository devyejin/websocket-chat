package com.example.websocketchat.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//Server와 Client 사이에서 message payload를 전달해주는 역할
@Setter
@Getter
@ToString
public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;
    private String roomId;
    private String time; //채팅 발송 시간
}
