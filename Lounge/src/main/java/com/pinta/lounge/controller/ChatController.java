package com.pinta.lounge.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @MessageMapping("/chat")
    public String handle(String greeting) {
        return "[" + LocalDateTime.now() + "]: " + greeting;
    }

}
