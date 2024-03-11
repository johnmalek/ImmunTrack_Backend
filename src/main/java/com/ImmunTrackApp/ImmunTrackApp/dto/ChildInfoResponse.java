package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildInfoResponse {
    private boolean success;
    private String message;
    ArrayList<ChildDetails> children;
}
