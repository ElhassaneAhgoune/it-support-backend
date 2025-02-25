package com.ticketsystem.repository;

import com.ticketsystem.model.Status;
import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBy(User user);

    List<Ticket> findByStatus(Status status);

    List<Ticket> findByStatusAndCreatedBy(Status status, User user);
}
