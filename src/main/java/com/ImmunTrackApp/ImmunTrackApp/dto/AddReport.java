package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddReport {
    private String child_name;
    private String vaccine_administered;
    private String date_administered;
    private String next_dose_date;
    private String remarks;
}
