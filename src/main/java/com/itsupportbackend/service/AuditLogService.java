package com.itsupportbackend.service;

import com.itsupportbackend.entity.ActionType;
import com.itsupportbackend.entity.AuditLog;
import com.itsupportbackend.entity.Ticket;
import com.itsupportbackend.entity.User;
import com.itsupportbackend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void logStatusChange(Ticket ticket, User user, String oldStatus, String newStatus) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTicket(ticket);
        auditLog.setUser(user);
        auditLog.setActionType(ActionType.STATUS_UPDATE);
        auditLog.setOldStatus(oldStatus);
        auditLog.setNewStatus(newStatus);
        auditLogRepository.save(auditLog);
    }

    public void logCommentAdded(Ticket ticket, User user, String commentContent) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTicket(ticket);
        auditLog.setUser(user);
        auditLog.setActionType(ActionType.COMMENT_ADDED);
        auditLog.setCommentContent(commentContent);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAuditLogsForTicket(Long ticketId) {
        return auditLogRepository.findAuditLogByTicketId(ticketId);
    }

    public AuditLog save(AuditLog comment) {
      return   auditLogRepository.save(comment);
    }

    public List<Long> getTicketsByUser(User user) {
        return auditLogRepository.findAuditLogByTicket_Id(user.getId());
    }
}