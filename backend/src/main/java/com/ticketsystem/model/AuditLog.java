package com.ticketsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "old_value", length = 4000)
    private String oldValue;

    @Column(name = "new_value", length = 4000)
    private String newValue;

    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate;

    @PrePersist
    protected void onCreate() {
        actionDate = LocalDateTime.now();
    }

    public static AuditLog createStatusChangeLog(Ticket ticket, User user, String oldStatus, String newStatus) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("STATUS_CHANGE");
        log.setOldValue(oldStatus);
        log.setNewValue(newStatus);
        return log;
    }

    public static AuditLog createCommentAddedLog(Ticket ticket, User user, String commentId) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("COMMENT_ADDED");
        log.setNewValue("Comment ID: " + commentId);
        return log;
    }

    public static AuditLog createTicketCreatedLog(Ticket ticket, User user) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setUser(user);
        log.setActionType("TICKET_CREATED");
        log.setNewValue("Ticket ID: " + ticket.getId());
        return log;
    }
}
