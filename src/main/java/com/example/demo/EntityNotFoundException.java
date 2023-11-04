package com.example.demo;

public final class EntityNotFoundException extends RuntimeException {

    private final int entityId;

    public EntityNotFoundException(int entityId){
        super("Entity not found with Id "+ entityId);
        this.entityId = entityId;
    }
}
