package com.manosgrigorakis.logisticsplatform.shipments;

import lombok.Getter;

@Getter
public class ShipmentStatusException extends RuntimeException {
  private final String errorCode;

  public ShipmentStatusException(String message) {
    super(message);
    this.errorCode = "";
  }

  public ShipmentStatusException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode != null ? errorCode : "";
  }
}
