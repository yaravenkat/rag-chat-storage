package com.yara.ragchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class CreateSessionRequest {
    @NotNull
    @NotBlank
    private String userId;
    @NotNull @NotBlank
    private String sessionName;
}
