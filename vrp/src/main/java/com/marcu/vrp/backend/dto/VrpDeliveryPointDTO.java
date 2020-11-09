package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.VrpInstance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class VrpDeliveryPointDTO implements Serializable {
    private GeographicPointDTO geographicPoint;
    private VrpInstanceDTO vrpInstance;
}
