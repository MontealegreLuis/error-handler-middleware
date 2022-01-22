package com.montealegreluis.errorhandlermiddleware;

import static com.montealegreluis.activityfeed.Activity.error;
import static com.montealegreluis.activityfeed.Activity.warning;
import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

import com.montealegreluis.activityfeed.Activity;
import com.montealegreluis.activityfeed.ContextSerializer;
import com.montealegreluis.servicebuses.Action;
import com.montealegreluis.servicebuses.Input;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ErrorHandlerActivity {
  public static Activity domainException(
      Input input, DomainException exception, ContextSerializer serializer) {
    Action action = input.action();
    String message = "Cannot " + action.toWords() + ". " + exception.getMessage();
    String identifier = action.toSlug() + "-" + exception.code();

    return warning(
        identifier,
        message,
        (context) -> {
          context.put("exception", contextFrom(exception));
          context.put("input", serializer.toContextMap(input));
        });
  }

  public static Activity infrastructureException(
      Input input, Throwable exception, ContextSerializer serializer) {
    Action action = input.action();
    String message = "Cannot " + action.toWords() + ". " + exception.getMessage();
    String identifier = action.toSlug() + "-infrastructure-exception";

    return error(
        identifier,
        message,
        (context) -> {
          context.put("exception", contextFrom(exception));
          context.put("input", serializer.toContextMap(input));
        });
  }
}
