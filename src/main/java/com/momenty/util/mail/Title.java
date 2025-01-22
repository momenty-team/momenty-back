package com.momenty.util.mail;

import lombok.Getter;

@Getter
public enum Title {
    REGISTER_TITLE("Momenty 회원가입 인증번호"),
    ;

    private final String content;

    Title(String content) {
        this.content = content;
    }
}
