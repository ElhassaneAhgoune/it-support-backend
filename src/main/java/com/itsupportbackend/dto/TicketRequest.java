package com.itsupportbackend.dto;


import com.itsupportbackend.entity.Category;
import com.itsupportbackend.entity.Priority;
import lombok.Data;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private Priority priority;
    private Category category;
}

