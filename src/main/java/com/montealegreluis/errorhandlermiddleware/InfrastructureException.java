package com.montealegreluis.errorhandlermiddleware;

public abstract class InfrastructureException extends ActionException {
  protected InfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }
}
