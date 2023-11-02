package com.pinta.lounge.repository;

import com.pinta.lounge.entity.ChatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepo extends JpaRepository<ChatParticipantEntity, Long> {
    Optional<ChatParticipantEntity> findByChatIdAndUserId(long chat, long user);

    List<ChatParticipantEntity> findByChatId(long chat);
}
