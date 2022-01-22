package com.montealegreluis.errorhandlermiddleware;

import static com.montealegreluis.activityfeed.ContextAssertions.assertContextHasKey;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.montealegreluis.activityfeed.ContextSerializer;
import com.montealegreluis.servicebuses.fakes.commandbus.FakeCommand;
import java.util.Map;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ErrorHandlerActivityTest {
  @Test
  void it_creates_a_domain_exception_activity() {
    var activity =
        ErrorHandlerActivity.domainException(
            new FakeCommand(), new DomainException("Action cannot be completed") {}, serializer);

    assertEquals("Cannot fake command. Action cannot be completed", activity.message());
    assertEquals(Level.WARNING, activity.level());
    @SuppressWarnings("unchecked")
    Map<String, Object> context = (Map<String, Object>) (activity.context().get("context"));
    assertContextHasKey("input", context);
    assertContextHasKey("exception", context);
  }

  @Test
  void it_creates_an_infrastructure_exception_activity() {
    var activity =
        ErrorHandlerActivity.infrastructureException(
            new FakeCommand(), new RuntimeException("Action cannot be completed") {}, serializer);

    assertEquals("Cannot fake command. Action cannot be completed", activity.message());
    assertEquals(Level.SEVERE, activity.level());
    @SuppressWarnings("unchecked")
    Map<String, Object> context = (Map<String, Object>) (activity.context().get("context"));
    assertContextHasKey("input", context);
    assertContextHasKey("exception", context);
  }

  @BeforeEach
  void let() {
    serializer = new ContextSerializer(new ObjectMapper());
  }

  private ContextSerializer serializer;
}
