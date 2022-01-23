# Error Handler Middleware

[![CI workflow](https://github.com/montealegreluis/error-handler-middleware/actions/workflows/ci.yml/badge.svg)](https://github.com/montealegreluis/error-handler-middleware/actions/workflows/ci.yml)
[![Release workflow](https://github.com/montealegreluis/error-handler-middleware/actions/workflows/release.yml/badge.svg)](https://github.com/montealegreluis/activity-feed/actions/workflows/release.yml)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventionalcommits-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

Error Handler Middleware for [Command and Query buses](https://github.com/MontealegreLuis/service-buses).

It logs and re-throws exceptions, so they can be classified in two main groups `DomainException`s and `InfrastructureException`s.

## Installation

1. [Authenticating to GitHub Packages](https://github.com/MontealegreLuis/error-handler-middleware/blob/main/docs/installation/authentication.md)
2. [Maven](https://github.com/MontealegreLuis/error-handler-middleware/blob/main/docs/installation/maven.md)
3. [Gradle](https://github.com/MontealegreLuis/error-handler-middleware/blob/main/docs/installation/gradle.md)

## Usage

The snippet below shows the simplest way to configure your error handler.

```java
var logger = LoggerFactory.getLogger(Application.class);
var feed = new ActivityFeed(logger);

var mapper = new ObjectMapper();
var serializer = new ContextSerializer(mapper);

var errorHandler = new CommandErrorHandlerMiddleware(
  feed, 
  serializer
);
```

You can customize the `ContextSerializer` as specified in its [documentation](https://github.com/MontealegreLuis/activity-feed#masking-sensitive-information).

Once you create the error handler, the next step is to add it to the command bus as shown below.

```java
var middleware = List.of(
  errorHandlerMiddleware, 
  commandHandlerMiddleware
);
var commandBus = new MiddlewareCommandBus(middleware);
```

### Log filtering

#### Domain exceptions

Whenever a child of the `DomainException` class is thrown, the error handler will generate an **identifier** using the **command class name** and the **exception class name**.

Suppose your command `ProcessPayment` throws a domain exception `InsufficientFunds`, the logging event would have the identifier `process-payment-insufficient-funds` which you could use to filter your logs.

The error handler will add the `exception` [information](https://github.com/MontealegreLuis/activity-feed#logging-an-exception) and the `input` used when the exception was thrown to the logging event automatically.

The snippet below shows how can you look for instances of this specific error in AWS CloudWatch.

```sql
fields @timestamp, message, `context.exception`, `context.input`
| filter `context.identifier` = "process-payment-insufficient-funds"
| sort @timestamp desc
| limit 50
```

#### Infrastructure exceptions

Any exception that doesn't extend `DomainException` is considered an infrastructure exception.
The error handler will generate an **identifier** using the **command class name** and will add the suffix `infrastructure-exception`.

Suppose your command `ProcessPayment` throws an `SQLException`, the logging event would have the identifier `process-payment-infrastructure-exception` which you could use to filter your logs.

The error handler will add the `exception` [information](https://github.com/MontealegreLuis/activity-feed#logging-an-exception) and the `input` used when the exception was thrown to the logging event automatically.

The snippet below shows how can you look for instances of this specific error in AWS CloudWatch.

```sql
fields @timestamp, message, `context.exception`, `context.input`
| filter `context.identifier` = "process-payment-infrastructure-exception"
| filter `context.exception.class` = "java.sql.SQLException"
| sort @timestamp desc
| limit 50
```

### How exceptions are handled

- Child classes of `DomainException` are re-thrown.
- Everything else is wrapped in a `CommandFailure` exception.

If you don't want to log exceptions twice you could do it as shown in the snippet below.

```java
if (exception instanceof ActionException) return;

// rest of your application logging logic...
```

## Contribute

Please refer to [CONTRIBUTING](https://github.com/MontealegreLuis/error-handler-middleware/blob/main/CONTRIBUTING.md) for information on how to contribute to Error Handler Middleware.

## License

Released under the [BSD-3-Clause](https://github.com/MontealegreLuis/error-handler-middleware/blob/main/LICENSE).
