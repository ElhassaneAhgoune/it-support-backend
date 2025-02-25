package com.itsupportbackend.dto;

import com.itsupportbackend.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String category;
    private LocalDateTime creationDate;
    private Status status;
    private String createdBy;
}
