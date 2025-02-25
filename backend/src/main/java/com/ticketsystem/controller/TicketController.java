package com.ticketsystem.controller;

import com.ticketsystem.dto.*;
import com.ticketsystem.model.User;
import com.ticketsystem.service.AuditLogService;
import com.ticketsystem.service.CommentService;
import com.ticketsystem.service.TicketService;
import com.ticketsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final CommentService commentService;
    private final AuditLogService auditLogService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        User currentUser = userService.getCurrentUser();
        TicketDto createdTicket = ticketService.createTicket(request, currentUser);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        TicketDto ticket = ticketService.getTicketById(id, currentUser);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        User currentUser = userService.getCurrentUser();
        List<TicketDto> tickets = ticketService.getAllTickets(currentUser);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDto>> getTicketsByStatus(@PathVariable String status) {
        User currentUser = userService.getCurrentUser();
        List<TicketDto> tickets = ticketService.getTicketsByStatus(status, currentUser);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public ResponseEntity<TicketDto> updateTicketStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        User currentUser = userService.getCurrentUser();
        TicketDto updatedTicket = ticketService.updateTicketStatus(id, request, currentUser);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/{id}/assign/{assigneeId}")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public ResponseEntity<TicketDto> assignTicket(@PathVariable Long id, @PathVariable Long assigneeId) {
        User currentUser = userService.getCurrentUser();
        TicketDto updatedTicket = ticketService.assignTicket(id, assigneeId, currentUser);
        return ResponseEntity.ok(updatedTicket);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        User currentUser = userService.getCurrentUser();
        CommentDto comment = commentService.addComment(id, request, currentUser);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByTicketId(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        List<CommentDto> comments = commentService.getCommentsByTicketId(id, currentUser);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}/audit-logs")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByTicketId(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        List<AuditLogDto> auditLogs = auditLogService.getAuditLogsByTicketId(id, currentUser);
        return ResponseEntity.ok(auditLogs);
    }
}
