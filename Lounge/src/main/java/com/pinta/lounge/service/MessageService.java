package com.pinta.lounge.service;

import com.pinta.lounge.dto.ChatInfo;
import com.pinta.lounge.dto.MessageIn;
import com.pinta.lounge.dto.MessageOut;
import com.pinta.lounge.entity.ChatParticipantEntity;
import com.pinta.lounge.entity.MessageEntity;
import com.pinta.lounge.repository.ChatParticipantRepo;
import com.pinta.lounge.repository.ChatRepo;
import com.pinta.lounge.repository.MessageRepo;
import com.pinta.lounge.security.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private ChatParticipantRepo chatPartRepo;

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private ModelMapper modelMapper;

    public ChatInfo getChat(Long chatId) {
        Optional<ChatParticipantEntity> chatPart = chatPartRepo.findByChatIdAndUserId(chatId, SecurityUtils.getId());
        if (chatPart.map(ChatParticipantEntity::isKicked).orElse(true)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return chatRepo.findById(chatId)
            .map(c -> modelMapper.map(c, ChatInfo.class))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<MessageOut> getMessages(Long chatId, Long pageNumber, Long pageSize) {
        Optional<ChatParticipantEntity> chatPart = chatPartRepo.findByChatIdAndUserId(chatId, SecurityUtils.getId());
        if (chatPart.map(ChatParticipantEntity::isKicked).orElse(true)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Pageable page;
        if (pageNumber >= 0 && pageSize > 0) {
            page = Pageable.ofSize(pageSize.intValue())
                .withPage(pageNumber.intValue());
        } else {
            page = Pageable.unpaged();
        }

        return messageRepo.findByChatIdPaged(page, chatId).stream()
            .map(m -> modelMapper.map(m, MessageOut.class))
            .toList();
    }

    public void sendMessage(MessageIn message) {
        messageRepo.save(
            new MessageEntity()
                .setUserId(SecurityUtils.getId())
                .setChatId(message.getChatId())
                .setContent(message.getContent())
        );
    }
}
