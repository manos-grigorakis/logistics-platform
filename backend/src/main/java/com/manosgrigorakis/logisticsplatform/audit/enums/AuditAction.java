package com.manosgrigorakis.logisticsplatform.audit.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE,
    UPDATE,
    DELETE,
    STATUS_CHANGE,
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    PASSWORD_RESET,
    PASSWORD_CHANGED,
}
