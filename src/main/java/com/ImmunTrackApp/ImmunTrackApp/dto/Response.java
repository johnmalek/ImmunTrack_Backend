package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {
    private boolean success;
    private String message;
}
