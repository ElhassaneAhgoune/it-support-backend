package com.ticketsystem.repository;

import com.ticketsystem.model.Comment;
import com.ticketsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedDateDesc(Ticket ticket);
}
