package com.ticketsystem.service.impl;

import com.ticketsystem.dto.AuditLogDto;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.exception.UnauthorizedException;
import com.ticketsystem.model.AuditLog;
import com.ticketsystem.model.Comment;
import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.User;
import com.ticketsystem.repository.AuditLogRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void logTicketCreated(Ticket ticket, User user) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("TICKET_CREATED");
        log.setNewValue("Ticket created with title: " + ticket.getTitle());

        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public void logStatusChange(Ticket ticket, User user, String oldStatus, String newStatus) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("STATUS_CHANGED");
        log.setOldValue(oldStatus);
        log.setNewValue(newStatus);

        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public void logCommentAdded(Ticket ticket, User user, Comment comment) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("COMMENT_ADDED");
        log.setNewValue("Comment added: " + comment.getCommentText());

        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public void logTicketAssigned(Ticket ticket, User user, User assignee) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("TICKET_ASSIGNED");
        log.setNewValue("Ticket assigned to: " + assignee.getUsername());

        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogsByTicketId(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        // If user is not IT support, they can only view audit logs for their own tickets
        if (!currentUser.isITSupport() && !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view audit logs for this ticket");
        }

        List<AuditLog> auditLogs = auditLogRepository.findByTicketOrderByActionDateDesc(ticket);

        return auditLogs.stream()
                .map(AuditLogDto::fromEntity)
                .collect(Collectors.toList());
    }
}