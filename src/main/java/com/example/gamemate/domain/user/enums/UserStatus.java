package com.example.gamemate.domain.user.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    ACTIVE("active"),
    WITHDRAW("withdraw");

    private String name;

    UserStatus(String name) {
        this.name = name;
    }
}
