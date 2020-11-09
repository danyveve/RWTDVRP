package com.marcu.vrp.backend.validator;

public interface Validator<T> {
    /**
     * Basic validator for an entity
     *
     * @param entity the entity instance to be validated
     * @throws ValidatorException if a certain condition is not following with a suggestive message along
     */
    void validate(T entity) throws ValidatorException;
}
