/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 7/25/16. */

package ws.epigraph.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO implement Valuable (shared with Datum)?
public class ErrorValue { // TODO rename to DatumError (to avoid clash with java.lang.Error)?

  public final @NotNull Integer statusCode;

  public final @NotNull String message;

  public final @Nullable Exception cause; // TODO Throwable?

  // TODO add generic property bag for custom properties?
  // TODO add (hierarchical?) error category set (e.g. [ "BadRequest", "InvalidParameterValue" ])?

  public ErrorValue(@NotNull Integer statusCode, @NotNull String message, @Nullable Exception cause) {
    this.message = message;
    this.statusCode = statusCode;
    this.cause = cause;
  }

  public ErrorValue(@NotNull Integer statusCode, @NotNull Exception cause) {
    this(statusCode, causeMessage(cause), cause);
  }

  public ErrorValue(@NotNull Integer statusCode, @NotNull String message) {
    this(statusCode, message, null);
  }

  private static @NotNull String causeMessage(@NotNull Exception cause) {
    String message = cause.getMessage();
    return message == null ? cause.toString() : message;
  }

  public @NotNull Integer statusCode() { return statusCode; }

  public @NotNull String message() { return message; }

  public @Nullable Exception cause() { return cause; }

}
