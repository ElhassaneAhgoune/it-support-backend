package com.itsupportbackend.controller;

import com.itsupportbackend.entity.AuditLog;
import com.itsupportbackend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/ticket/{ticketId}")
    public List<AuditLog> getAuditLogsByTicket(@PathVariable Long ticketId) {
        return auditLogService.getAuditLogsForTicket(ticketId);
    }
}