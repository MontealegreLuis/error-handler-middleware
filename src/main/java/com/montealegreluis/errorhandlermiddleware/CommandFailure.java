package com.montealegreluis.errorhandlermiddleware;

import com.montealegreluis.servicebuses.Action;

public final class CommandFailure extends InfrastructureException {
  public CommandFailure(Action action, Throwable cause) {
    super("Cannot complete " + action.toWords() + ". " + cause.getMessage(), cause);
  }
}
