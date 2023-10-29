package com.pinta.lounge.repository;

import com.pinta.lounge.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends JpaRepository<MessageEntity, Long> {

    @Query("""
            select m
            from MessageEntity m
            where m.chatId = :chatId
            order by m.time desc
        """)
    Page<MessageEntity> findByChatIdPaged(Pageable page, @Param("chatId") Long chatId);

}
