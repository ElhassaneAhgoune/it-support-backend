package com.itsupportbackend.controller;


import com.itsupportbackend.dto.*;
import com.itsupportbackend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    // Employee: Create ticket
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @RequestBody TicketRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ticketService.createTicket(request, authentication.getName())
        );
    }

    // Employee: Get own tickets
    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(Authentication authentication) {
        return ResponseEntity.ok(
                ticketService.getTicketsByUser(authentication.getName())
        );
    }

    // IT Support: Get all tickets
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // IT Support: Update ticket status
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long ticketId,
            @RequestBody StatusUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ticketService.updateStatus(ticketId, request, authentication.getName())
        );
    }

    // IT Support: Add comment
    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long ticketId,
            @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ticketService.addComment(ticketId, request, authentication.getName())
        );
    }
}