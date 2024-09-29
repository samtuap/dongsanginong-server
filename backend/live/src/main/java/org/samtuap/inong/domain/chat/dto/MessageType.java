package org.samtuap.inong.domain.chat.dto;

public enum MessageType {
    TEXT,     // 일반 메시지
    ENTER,    // 사용자의 입장 메시지
    EXIT,     // 사용자의 퇴장 메시지
    JOIN      // 채팅방 참가 메시지
}
