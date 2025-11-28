package com.manosgrigorakis.logisticsplatform.exception;

import lombok.Getter;

@Getter
public class DeleteConflictException extends RuntimeException {
    private String resource;
    private String referencedBy;

    public DeleteConflictException(String message) {
        super(message);
    }

    public DeleteConflictException(String resource, String referencedBy) {
        super(resource + " cannot be deleted as it is currently assigned to " + referencedBy);
        this.resource = resource;
        this.referencedBy = referencedBy;
    }
}
