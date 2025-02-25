package com.ticketsystem.repository;


import com.ticketsystem.model.AuditLog;
import com.ticketsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTicketOrderByActionDateDesc(Ticket ticket);
}