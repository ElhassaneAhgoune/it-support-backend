package com.ticketsystem.service;

import com.ticketsystem.dto.CreateTicketRequest;
import com.ticketsystem.dto.TicketDto;
import com.ticketsystem.dto.UpdateTicketStatusRequest;
import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.User;

import java.util.List;

public interface TicketService {
    TicketDto createTicket(CreateTicketRequest request, User currentUser);
    TicketDto getTicketById(Long id, User currentUser);
    List<TicketDto> getAllTickets(User currentUser);
    List<TicketDto> getTicketsByStatus(String status, User currentUser);
    TicketDto updateTicketStatus(Long id, UpdateTicketStatusRequest request, User currentUser);
    TicketDto assignTicket(Long id, Long assigneeId, User currentUser);
}

