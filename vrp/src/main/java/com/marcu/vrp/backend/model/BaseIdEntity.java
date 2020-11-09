package com.marcu.vrp.backend.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Data
public abstract class BaseIdEntity<ID> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public ID id;
}
