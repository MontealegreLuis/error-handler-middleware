package com.montealegreluis.errorhandlermiddleware;

public abstract class ActionException extends RuntimeException {
  public ActionException(String message) {
    super(message);
  }

  public ActionException(String message, Throwable cause) {
    super(message, cause);
  }
}
