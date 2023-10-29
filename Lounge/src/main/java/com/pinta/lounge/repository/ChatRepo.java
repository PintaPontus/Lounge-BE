package com.pinta.lounge.repository;

import com.pinta.lounge.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends JpaRepository<ChatEntity, Long> {
}
