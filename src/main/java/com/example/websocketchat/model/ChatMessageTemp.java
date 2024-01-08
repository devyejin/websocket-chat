package com.example.websocketchat.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//Server와 Client 사이에서 message payload를 전달해주는 역할
@Setter
@Getter
@ToString
public class ChatMessageTemp {

    private MessageType type;
    private String content;
    private String sender;
}
