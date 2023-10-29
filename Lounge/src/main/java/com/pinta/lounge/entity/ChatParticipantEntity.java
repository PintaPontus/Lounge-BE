package com.pinta.lounge.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "chat_participant", schema = "message", catalog = "lounge")
public class ChatParticipantEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "since")
    private LocalDateTime since;

    @Column(name = "kicked")
    private boolean kicked;

    @Column(name = "archived")
    private boolean archived;

}
