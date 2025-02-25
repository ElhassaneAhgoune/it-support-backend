package com.ticketsystem.dto;

import com.ticketsystem.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private String title;
    private String description;
    private Long createdById;
    private String createdByUsername;
    private Long assignedToId;
    private String assignedToUsername;
    private String category;
    private String priority;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;

    public static TicketDto fromEntity(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setCreatedById(ticket.getCreatedBy().getId());
        dto.setCreatedByUsername(ticket.getCreatedBy().getUsername());

        if (ticket.getAssignedTo() != null) {
            dto.setAssignedToId(ticket.getAssignedTo().getId());
            dto.setAssignedToUsername(ticket.getAssignedTo().getUsername());
        }

        dto.setCategory(ticket.getCategory().getName());
        dto.setPriority(ticket.getPriority().getName());
        dto.setStatus(ticket.getStatus().getName());
        dto.setCreatedDate(ticket.getCreatedDate());
        dto.setLastUpdatedDate(ticket.getLastUpdatedDate());

        return dto;

    }
}
