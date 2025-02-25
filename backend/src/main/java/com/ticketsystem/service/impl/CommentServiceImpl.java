package com.ticketsystem.service.impl;


import com.ticketsystem.dto.AddCommentRequest;
import com.ticketsystem.dto.CommentDto;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.exception.UnauthorizedException;
import com.ticketsystem.model.Comment;
import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.User;
import com.ticketsystem.repository.CommentRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.AuditLogService;
import com.ticketsystem.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CommentDto addComment(Long ticketId, AddCommentRequest request, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        // If user is not IT support, they can only comment on their own tickets
        if (!currentUser.isITSupport() && !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to comment on this ticket");
        }

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setUser(currentUser);
        comment.setCommentText(request.getCommentText());

        Comment savedComment = commentRepository.save(comment);

        // Log the comment addition
        auditLogService.logCommentAdded(ticket, currentUser, savedComment);

        return CommentDto.fromEntity(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTicketId(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        // If user is not IT support, they can only view comments on their own tickets
        if (!currentUser.isITSupport() && !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view comments for this ticket");
        }

        List<Comment> comments = commentRepository.findByTicketOrderByCreatedDateDesc(ticket);

        return comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
