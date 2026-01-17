package com.yara.ragchat.entity;

import java.util.Arrays;

/**
 * Message sender types used across the domain model and controllers.
 */
public enum Sender {
    USER,
    ASSISTANT,
    AI,
    SYSTEM;

    public static Sender from(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender: " + value));
    }

}
