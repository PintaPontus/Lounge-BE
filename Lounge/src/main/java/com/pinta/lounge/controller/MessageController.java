package com.pinta.lounge.controller;

import com.pinta.lounge.dto.ChatInfo;
import com.pinta.lounge.dto.MessageIn;
import com.pinta.lounge.dto.MessageOut;
import com.pinta.lounge.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/chat")
    public ChatInfo getChat(@RequestParam Long chatId) {
        return messageService.getChat(chatId);
    }

    @GetMapping("/messages")
    public List<MessageOut> getMessages(@RequestParam Long chatId,
                                        @RequestParam(defaultValue = "0") Long pageNumber,
                                        @RequestParam(defaultValue = "10") Long pageSize) {
        return messageService.getMessages(chatId, pageNumber, pageSize);
    }

    @PostMapping("/send")
    public void send(@RequestBody MessageIn message) {
        messageService.sendMessage(message);
    }

}
