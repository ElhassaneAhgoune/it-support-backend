package com.ticketsystem.service;

import com.ticketsystem.dto.AddCommentRequest;
import com.ticketsystem.dto.CommentDto;
import com.ticketsystem.model.User;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long ticketId, AddCommentRequest request, User currentUser);
    List<CommentDto> getCommentsByTicketId(Long ticketId, User currentUser);
}