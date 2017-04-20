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

package ws.epigraph.validation.data;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Datum;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.TagApi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DataValidationContext {
  private final Deque<StackItem> stack = new ArrayDeque<>();
  private final List<DataValidationError> errors = new ArrayList<>();

  public void withStackItem(@NotNull StackItem item, @NotNull Runnable runnable) {
    stack.push(item);
    try {
      runnable.run();
    } finally {
      stack.pop();
    }
  }

  public void addError(@NotNull String message) { errors.add(new DataValidationError(stack, message)); }

  public @NotNull List<@NotNull DataValidationError> errors() { return errors; }

  interface StackItem {
    @Override
    @NotNull String toString();
  }

  public static class TagStackItem implements StackItem {
    public final @NotNull TagApi tag;

    TagStackItem(final @NotNull TagApi tag) {this.tag = tag;}

    @Override
    public @NotNull String toString() { return ":" + tag.name(); }
  }

  public static class FieldStackItem implements StackItem {
    public final @NotNull FieldApi field;

    public FieldStackItem(final @NotNull FieldApi field) {this.field = field;}

    @Override
    public @NotNull String toString() { return "/" + field.name(); }
  }

  public static class MapKeyStackItem implements StackItem {
    public final @NotNull Datum.@NotNull Imm key;

    public MapKeyStackItem(final @NotNull Datum.@NotNull Imm key) {this.key = key;}

    @Override
    public @NotNull String toString() {
      String keyPrintout;

      try {
        StringWriter sw = new StringWriter();
        DataPrinter<IOException> printer = DataPrinter.toString(120, false, sw);
        printer.print(key);
        keyPrintout = sw.toString();
      } catch (IOException e) {
        keyPrintout = e.toString();
      }

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
