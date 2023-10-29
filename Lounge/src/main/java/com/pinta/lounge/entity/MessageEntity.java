package com.pinta.lounge.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "message", schema = "message", catalog = "lounge")
public class MessageEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "content")
    private String content;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "pinned")
    private boolean pinned;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "time")
    private LocalDateTime time = LocalDateTime.now();

}
