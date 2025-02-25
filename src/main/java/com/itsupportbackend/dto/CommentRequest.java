package com.itsupportbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// CommentRequest.java
@Data
public class CommentRequest {
    @NotBlank
    private String content;
}
