/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.validation.gdata;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataValidationError {
  private final @NotNull List<GDataValidationContext.@NotNull StackItem> location;
  private final @NotNull TextLocation textLocation;
  private final @NotNull String message;

  GDataValidationError(
      final @NotNull Collection<GDataValidationContext.StackItem> location,
      final @NotNull TextLocation textLocation,
      final @NotNull String message) {

    final ArrayList<GDataValidationContext.StackItem> locationTmp = new ArrayList<>(location);
    Collections.reverse(locationTmp);
    this.location = Collections.unmodifiableList(locationTmp);
    this.textLocation = textLocation;
    this.message = message;
  }

  public @NotNull String message() { return message; }

  public @NotNull List<GDataValidationContext.StackItem> location() { return location; }

  public @NotNull TextLocation textLocation() { return textLocation; }

  public @NotNull String toStringNoTextLocation() {
    return location().isEmpty() ?
           message() :
           location().stream().map(GDataValidationContext.StackItem::toString).collect(Collectors.joining()) +
           " : " + message();
  }

  @Override
  public @NotNull String toString() { return toStringNoTextLocation() + " at " + textLocation(); }
}
