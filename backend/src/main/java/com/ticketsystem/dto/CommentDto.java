package com.ticketsystem.dto;

import com.ticketsystem.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String username;
    private String commentText;
    private LocalDateTime createdDate;

    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setTicketId(comment.getTicket().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setCommentText(comment.getCommentText());
        dto.setCreatedDate(comment.getCreatedDate());
        return dto;
    }
}
