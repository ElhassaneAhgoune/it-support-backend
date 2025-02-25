package com.ticketsystem.service.impl;

import com.ticketsystem.dto.CreateTicketRequest;
import com.ticketsystem.dto.TicketDto;
import com.ticketsystem.dto.UpdateTicketStatusRequest;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.exception.UnauthorizedException;
import com.ticketsystem.model.*;
import com.ticketsystem.repository.*;
import com.ticketsystem.service.AuditLogService;
import com.ticketsystem.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public TicketDto createTicket(CreateTicketRequest request, User currentUser) {
        // Find category by name
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategory()));

        // Find priority by name
        Priority priority = priorityRepository.findByName(request.getPriority())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found: " + request.getPriority()));

        // Get default NEW status
        Status status = statusRepository.findByName(Status.StatusName.NEW.name())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found: NEW"));

        // Create and save the ticket
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedBy(currentUser);
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setStatus(status);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Create an audit log entry for ticket creation
        auditLogService.logTicketCreated(savedTicket, currentUser);

        return TicketDto.fromEntity(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDto getTicketById(Long id, User currentUser) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        // If user is not IT support, they can only view their own tickets
        if (!currentUser.isITSupport() && !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this ticket");
        }

        return TicketDto.fromEntity(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets(User currentUser) {
        List<Ticket> tickets;

        // If user is IT support, they can see all tickets
        // Otherwise, they can only see their own tickets
        if (currentUser.isITSupport()) {
            tickets = ticketRepository.findAll();
        } else {
            tickets = ticketRepository.findByCreatedBy(currentUser);
        }

        return tickets.stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByStatus(String status, User currentUser) {
        Status statusEntity = statusRepository.findByName(status)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + status));

        List<Ticket> tickets;

        // If user is IT support, they can see all tickets with the given status
        // Otherwise, they can only see their own tickets with the given status
        if (currentUser.isITSupport()) {
            tickets = ticketRepository.findByStatus(statusEntity);
        } else {
            tickets = ticketRepository.findByStatusAndCreatedBy(statusEntity, currentUser);
        }

        return tickets.stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TicketDto updateTicketStatus(Long id, UpdateTicketStatusRequest request, User currentUser) {
        // Only IT support can update ticket status
        if (!currentUser.isITSupport()) {
            throw new UnauthorizedException("Only IT support can update ticket status");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        Status newStatus = statusRepository.findByName(request.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + request.getStatus()));

        String oldStatusName = ticket.getStatus().getName();

        // Update ticket status
        ticket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Log the status change
        auditLogService.logStatusChange(updatedTicket, currentUser, oldStatusName, newStatus.getName());

        return TicketDto.fromEntity(updatedTicket);
    }

    @Override
    @Transactional
    public TicketDto assignTicket(Long id, Long assigneeId, User currentUser) {
        // Only IT support can assign tickets
        if (!currentUser.isITSupport()) {
            throw new UnauthorizedException("Only IT support can assign tickets");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + assigneeId));

        // Ensure the assignee is IT support
        if (!assignee.isITSupport()) {
            throw new IllegalArgumentException("Can only assign tickets to IT support staff");
        }

        ticket.setAssignedTo(assignee);
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Log the assignment
        auditLogService.logTicketAssigned(updatedTicket, currentUser, assignee);

        return TicketDto.fromEntity(updatedTicket);
    }
}
