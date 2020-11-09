package com.marcu.vrp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class GeographicPointDTO extends BaseIdDTO<Long> {
    private Double latitude;
    private Double longitude;
    private String address;
}
