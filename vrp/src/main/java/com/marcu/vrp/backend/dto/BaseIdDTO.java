package com.marcu.vrp.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BaseIdDTO<ID> implements Serializable {
    public ID id;
}
