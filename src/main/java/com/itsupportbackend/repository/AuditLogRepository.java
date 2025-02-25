package com.itsupportbackend.repository;

import com.itsupportbackend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT a.ticket.id FROM AuditLog a WHERE a.user.id = ?1")
    List<Long> findAuditLogByTicket_Id( UUID userId);
    @Query("SELECT a.ticket.id FROM AuditLog a WHERE a.ticket.id = ?1")
    List<AuditLog> findAuditLogByTicketId(Long ticketId);

    List<AuditLog> countAuditLogByUser_Id(UUID userId);
}