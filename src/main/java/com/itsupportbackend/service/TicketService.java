package com.itsupportbackend.service;


import com.itsupportbackend.dto.*;
import com.itsupportbackend.entity.*;
import com.itsupportbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public TicketResponse createTicket(TicketRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(request.getCategory())
                .creationDate(LocalDateTime.now())
                .status(Status.NEW)
                .createdBy(user)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .priority(ticket.getPriority().name())
                .category(ticket.getCategory().name())
                .creationDate(ticket.getCreationDate())
                .status(ticket.getStatus())
                .createdBy(ticket.getCreatedBy().getUsername())
                .build();
    }

    public void updateTicketStatus(Long ticketId, String newStatus, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(Status.valueOf(newStatus));
        ticketRepository.save(ticket);

        // Log status change
        auditLogService.logStatusChange(ticket, user, oldStatus, newStatus);
    }

    public void addComment(Long ticketId, String comment, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Log comment addition
        auditLogService.logCommentAdded(ticket, user, comment);
    }

    public List<TicketResponse> getTicketsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

       List<Long> ticketsIds =   auditLogService.getTicketsByUser(user)
                .stream()
                 .distinct()
                .toList();

        return ticketRepository.findAllById(ticketsIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse updateStatus(Long ticketId, StatusUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Status oldStatus = ticket.getStatus();
        ticket.setStatus(request.getStatus());
        Ticket updatedTicket = ticketRepository.save(ticket);

        auditLogService.logStatusChange(ticket, user, oldStatus.name(), request.getStatus().name());

        return mapToResponse(updatedTicket);
    }

    public CommentResponse addComment(Long ticketId, CommentRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        AuditLog comment = AuditLog.builder()
                .commentContent(request.getContent())
                .actionDate(LocalDateTime.now())
                .actionType(ActionType.COMMENT_ADDED)
                .user(user)
                .ticket(ticket)
                .build();

        AuditLog savedComment = auditLogService.save(comment);
        auditLogService.logCommentAdded(ticket, user, request.getContent());

        return mapToCommentResponse(savedComment);
    }

    private CommentResponse mapToCommentResponse(AuditLog comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getCommentContent())
                .createdDate(comment.getActionDate())
                .author(comment.getUser().getUsername())
                .build();
    }
}