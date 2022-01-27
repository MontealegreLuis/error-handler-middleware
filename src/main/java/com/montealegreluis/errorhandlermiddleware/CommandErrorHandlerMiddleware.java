package com.montealegreluis.errorhandlermiddleware;

import static com.montealegreluis.errorhandlermiddleware.ErrorHandlerActivity.domainException;
import static com.montealegreluis.errorhandlermiddleware.ErrorHandlerActivity.infrastructureException;

import com.montealegreluis.activityfeed.ActivityFeed;
import com.montealegreluis.activityfeed.ContextSerializer;
import com.montealegreluis.servicebuses.Action;
import com.montealegreluis.servicebuses.commandbus.Command;
import com.montealegreluis.servicebuses.commandbus.CommandHandler;
import com.montealegreluis.servicebuses.commandbus.middleware.CommandMiddleware;
import io.vavr.control.Try;

public final class CommandErrorHandlerMiddleware implements CommandMiddleware {
  private final ActivityFeed feed;
  private final ContextSerializer serializer;

  public CommandErrorHandlerMiddleware(ActivityFeed feed, ContextSerializer serializer) {
    this.feed = feed;
    this.serializer = serializer;
  }

  @Override
  public void execute(Command command, CommandHandler<Command> next) {
    Try.run(() -> next.execute(command))
        .onFailure((e) -> logException(command, e))
        .getOrElseThrow((e) -> rethrowException(command.action(), e));
  }

  private void logException(Command command, Throwable exception) {
    if (exception instanceof DomainException) {
      feed.record(domainException(command, (DomainException) exception, serializer));
      return;
    }
    feed.record(infrastructureException(command, exception, serializer));
  }

  private RuntimeException rethrowException(Action action, Throwable cause) {
    return cause instanceof DomainException
        ? (DomainException) cause
        : new CommandFailure(action, cause);
  }
}
