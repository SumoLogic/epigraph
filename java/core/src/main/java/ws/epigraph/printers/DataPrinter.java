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

/* Created by yegor on 7/24/16. */

package ws.epigraph.printers;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.WriterBackend;
import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.RecordType;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;

public class DataPrinter<Exc extends Exception> {

  public final Layouter<Exc> lo;

  public final boolean withTypes;

  private final Map<Datum, Integer> visiting = new IdentityHashMap<>();

  private int lastId = 0;

  private final Integer UNASSIGNED_ID = lastId;

  public DataPrinter(@NotNull Layouter<Exc> lo, boolean withTypes) {
    this.lo = lo;
    this.withTypes = withTypes;
  }

  public DataPrinter(@NotNull Layouter<Exc> lo) { this(lo, false); }

  public static @NotNull DataPrinter<IOException> out(int width, boolean withTypes) {
    return new DataPrinter<>(
        new Layouter<>(new WriterBackend(new OutputStreamWriter(System.out), width), 2),
        withTypes
    );
  }

  public static @NotNull DataPrinter<IOException> toString(int width, boolean withTypes, StringWriter writer) {
    return new DataPrinter<>(new Layouter<>(new WriterBackend(writer, width), 2), withTypes);
  }

  public void print(@Nullable Data data) throws Exc {
    if (data == null) { lo.print("null"); } else {
      switch (data.type().kind()) {
        case UNION:
          if (withTypes) lo.print("(").print(data.type().name().toString()).print(") ");
          Data.Raw raw = data._raw();
//        boolean single = raw.tagValues().size() == 1;
//        lo.print(":");
          /*if (!single)*/
          lo.print("<").beginCInd();
          boolean comma = false;
          for (Type.Tag tag : data.type().tags()) {
            @Nullable Val value = raw.getValue(tag);
            if (value != null) {
              if (comma) lo.print(",").end();
              else comma = true;
            /*if (!single)*/
              lo.brk(1);
              lo.beginIInd(0).print("").print(tag.name).print(": ");
              print(value);
            }
          }
          if (comma) lo.end();
          /*if (!single)*/
          lo.brk(1, -lo.getDefaultIndentation()).print(">").end();
          break;
        default:
          print(data._raw()
              .getValue(((DatumType) data.type()).self)); // expecting datum data to always have self-tag...
      }
    }
  }

  public void print(@NotNull Val value) throws Exc {
    ErrorValue error = value.getError();
    if (error == null) {
      Datum datum = value.getDatum();
      if (withTypes && datum != null) lo.print("(").print(value.getDatum().type().name().toString()).print(") ");
      print(datum);
    } else {
      print(error);
    }
  }

  public void print(@NotNull ErrorValue error) throws Exc {
    lo.print("ERROR(");
    lo.print(error.statusCode.toString()).print(", '");
    lo.print(error.message.trim()).print("'");

    if (error.cause != null) {
      String message = error.cause.getMessage();
      if (message != null)
        lo.print(", cause: '").print(message.trim()).print("'");
    }

    lo.print(")");
  }

  public void print(@Nullable Datum datum) throws Exc {
    if (datum == null) {
      lo.print("null");
    } else {
      if (datum instanceof PrimitiveDatum) print((PrimitiveDatum) datum);
      else if (datum instanceof RecordDatum) print((RecordDatum) datum);
      else if (datum instanceof ListDatum) print((ListDatum) datum);
      else if (datum instanceof MapDatum) print((MapDatum) datum);
//    else if (datum instanceof EnumDatum) print((EnumDatum) datum);
      else if (datum instanceof Datum) lo.print(datum.type().name().toString());
      else throw new UnsupportedOperationException(datum.type().name().toString());
    }
  }

  public void print(@NotNull PrimitiveDatum datum) throws Exc {
    if (datum instanceof StringDatum) lo.print("\"").print(datum.getVal().toString()).print("\"");
    else lo.print(datum.getVal().toString());
  }

  public void print(@NotNull RecordDatum datum) throws Exc {
    if (enter(datum)) try {
      lo.print("{").beginCInd();
      RecordDatum.Raw raw = datum._raw();
      boolean first = true;
      for (RecordType.Field field : datum.type().fields()) {
        @Nullable Data data = raw.getData(field);
        if (data != null) {
          if (!first) lo.print(",").end();
          else first = false;
          lo.brk(1).beginI(0).print("").print(field.name).print(": ");
          print(data);
        }
      }
      if (!first) lo.end();
      lo.brk(1, -lo.getDefaultIndentation()).print("}").end();
    } finally { leave(datum); }
  }

  public void print(@NotNull ListDatum datum) throws Exc {
    if (enter(datum)) try {
      lo.print("[").beginCInd();
      ListDatum.Raw raw = datum._raw();
      boolean first = true;
      for (Data data : raw.elements()) {
        if (!first) lo.print(",").end();
        else first = false;
        lo.brk(1).beginI(0);
        print(data);
      }
      if (!first) lo.end();
      lo.brk(1, -lo.getDefaultIndentation()).print("]").end();
    } finally { leave(datum); }
  }

  public void print(@NotNull MapDatum datum) throws Exc {
    if (enter(datum)) try {
      lo.print("(").beginCInd();
      MapDatum.Raw raw = datum._raw();
      boolean first = true;
      for (Map.Entry<Datum.@NotNull Imm, @NotNull ? extends Data> entry : raw.elements().entrySet()) {
        if (!first) lo.print(",").end();
        else first = false;
        lo.brk(1).beginI(0);
        print(entry.getKey());
        lo.print(": ");
        print(entry.getValue());
      }
      if (!first) lo.end();
      lo.brk(1, -lo.getDefaultIndentation()).print(")").end();
    } finally { leave(datum); }
  }

  private boolean enter(@NotNull Datum datum) throws Exc {
    Integer selfId = visiting.get(datum);
    if (selfId == null) {
      visiting.put(datum, UNASSIGNED_ID);
      return true;
    } else {
      if (selfId == UNASSIGNED_ID) {
        selfId = ++lastId;
        visiting.put(datum, selfId);
      }
      lo.print("...@").print(selfId.toString());
      return false;
    }
  }

  private void leave(@NotNull Datum datum) throws Exc {
    Integer selfId = visiting.remove(datum);
    if (selfId != null && selfId != UNASSIGNED_ID) lo.print("@").print(selfId.toString());
  }

}
