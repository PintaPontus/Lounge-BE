package com.pinta.lounge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChatInfo {
    private String id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private List<UserInfo> participants;
}
