package com.example.websocketchat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ChatDTO {

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private String time; // 메시지 발송 시간
}
