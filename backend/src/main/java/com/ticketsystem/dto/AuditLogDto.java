package com.ticketsystem.dto;

import com.ticketsystem.model.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String username;
    private String actionType;
    private String oldValue;
    private String newValue;
    private LocalDateTime actionDate;

    public static AuditLogDto fromEntity(AuditLog auditLog) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(auditLog.getId());
        dto.setTicketId(auditLog.getTicket().getId());
        dto.setUserId(auditLog.getUser().getId());
        dto.setUsername(auditLog.getUser().getUsername());
        dto.setActionType(auditLog.getActionType());
        dto.setOldValue(auditLog.getOldValue());
        dto.setNewValue(auditLog.getNewValue());
        dto.setActionDate(auditLog.getActionDate());
        return dto;
    }
}
