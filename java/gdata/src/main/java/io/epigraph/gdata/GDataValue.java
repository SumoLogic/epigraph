package io.epigraph.gdata;

import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GDataValue {
  @NotNull
  private final TextLocation location;

  protected GDataValue(@NotNull TextLocation location) {this.location = location;}

  @NotNull
  public TextLocation location() {
    return location;
  }
}
