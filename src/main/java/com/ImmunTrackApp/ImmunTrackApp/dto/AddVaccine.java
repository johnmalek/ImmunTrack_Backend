package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestPart;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVaccine {
    private String vaccineName;
    private String manufacturer;
    private String batchNo;
    private String expiryDate;
    private String quantity;
}
