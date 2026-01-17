package com.yara.ragchat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AddMessageRequest {
    private String sender; // USER, ASSISTANT, SYSTEM
    private String content;
    private String context;
}
