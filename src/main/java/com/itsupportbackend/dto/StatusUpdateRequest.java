package com.itsupportbackend.dto;

import com.itsupportbackend.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// StatusUpdateRequest.java
@Data
public class StatusUpdateRequest {
    @NotNull
    private Status status;
}

