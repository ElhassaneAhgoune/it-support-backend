package com.ticketsystem.service;


import com.ticketsystem.dto.AuditLogDto;
import com.ticketsystem.model.Comment;
import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.User;

import java.util.List;

public interface AuditLogService {
    void logTicketCreated(Ticket ticket, User user);
    void logStatusChange(Ticket ticket, User user, String oldStatus, String newStatus);
    void logCommentAdded(Ticket ticket, User user, Comment comment);
    void logTicketAssigned(Ticket ticket, User user, User assignee);
    List<AuditLogDto> getAuditLogsByTicketId(Long ticketId, User currentUser);
}
