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

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.gdata.GDataPrettyPrinter;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.DatumType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataValidationContext {
  private final Deque<StackItem> stack = new ArrayDeque<>();
  private final List<GDataValidationError> errors = new ArrayList<>();

  public void withStackItem(@NotNull StackItem item, @NotNull Runnable runnable) {
    stack.push(item);
    try {
      runnable.run();
    } finally {
      stack.pop();
    }
  }

  public void addError(@NotNull String message, @NotNull TextLocation location) {
    errors.add(new GDataValidationError(
        stack,
        location,
        message
    ));
  }

  public @NotNull List<@NotNull GDataValidationError> errors() { return errors; }

  interface StackItem {
    @Override
    @NotNull String toString();
  }

  public static class TagStackItem implements StackItem {
    public final @NotNull String tag;

    TagStackItem(final @NotNull String tag) {this.tag = tag;}

    @Override
    public @NotNull String toString() { return tag.equals(DatumType.MONO_TAG_NAME) ? "" : ":" + tag; }
  }

  public static class FieldStackItem implements StackItem {
    public final @NotNull String field;

    public FieldStackItem(final @NotNull String field) {this.field = field;}

    @Override
    public @NotNull String toString() { return "/" + field; }
  }

  public static class MapKeyStackItem implements StackItem {
    public final @NotNull GDatum key;

    public MapKeyStackItem(final @NotNull GDatum key) {this.key = key;}

    @Override
    public @NotNull String toString() {
      String keyPrintout;

      StringBuilder sb = new StringBuilder();
      GDataPrettyPrinter<NoExceptions> printer = new GDataPrettyPrinter<>(Layouter.getStringLayouter(sb));
      printer.print(key);
      keyPrintout = sb.toString();

      return "[" + keyPrintout + "]";
    }
  }

  public static class ListIndexStackItem implements StackItem {
    public final int index;

    public ListIndexStackItem(final int index) {this.index = index;}

    @Override
    public @NotNull String toString() { return "[" + index + "]"; }
  }
}
