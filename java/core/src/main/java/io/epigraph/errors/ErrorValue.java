/* Created by yegor on 7/25/16. */

package io.epigraph.errors;

import org.jetbrains.annotations.Nullable;

// TODO implement Valuable (shared with Datum)?
public class ErrorValue { // TODO rename to DatumError (to avoid clash with java.lang.Error)?

  private final Integer statusCode;

  @Nullable
  public final Exception cause; // TODO Throwable?

  public ErrorValue(@Nullable Integer statusCode, @Nullable Exception cause) {
    this.statusCode = statusCode;
    this.cause = cause;
  }

  @Nullable
  public Integer statusCode() { return statusCode; }

}
