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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Field;
import ws.epigraph.types.Tag;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * DataPrinter compatible with the data language syntax from {@code url.bnf}
 * except for metadata, error values and recursive data support (which are not present in url syntax)
 */
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

  protected Layouter<Exc> brk(int i) throws Exc {
    return lo.brk(i);
  }

  protected Layouter<Exc> brk(int i, int k) throws Exc {
    return lo.brk(i, k);
  }

  public void print(@Nullable Data data) throws Exc {
    if (data == null) { lo.print("null"); } else {
      switch (data.type().kind()) {
        case ENTITY:
         if (withTypes && !data.type().immediateSupertypes().isEmpty())
            lo.print(data.type().name().toString());
          Data.Raw raw = data._raw();
//        boolean single = raw.tagValues().size() == 1;
//        lo.print(":");
          /*if (!single)*/
          lo.print("<").beginCInd();
          boolean comma = false;
          for (Tag tag : data.type().tags()) {
            @Nullable Val value = raw.getValue(tag);
            if (value != null) {
              if (comma) lo.print(",").end();
              else comma = true;
            /*if (!single)*/
              brk(1);
              lo.beginIInd(0).print("").print(tag.name).print(": ");
              print(tag.type, value);
            }
          }
          if (comma) lo.end();
          /*if (!single)*/
          brk(1, -lo.getDefaultIndentation()).print(">").end();
          break;
        default:
          DatumType datumType = (DatumType) data.type();
          print(datumType, data._raw().getValue(datumType.self())); // expecting datum data to always have self-tag...
      }
    }
  }

  public void print(@NotNull DatumType type, @NotNull Val value) throws Exc {
    ErrorValue error = value.getError();
    if (error == null) {
      Datum datum = value.getDatum();
      print(type, datum);
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

  public void print(@Nullable DatumType type, @Nullable Datum datum) throws Exc {
    // todo ideally we shoud have acces to value type here and only print out type
    // information if type != container type, e.g. op parameter type
    // In reality this means reimplementing req/data printers for client-side URI composer
    // so that they can take op projections into account (unlike current req printers that don't)

    if (datum == null) {
      if (withTypes && type != null && !type.immediateSupertypes().isEmpty())
        lo.print(type.name().toString()).print("@");
      lo.print("null");
    } else {
      if (withTypes && !datum.type().immediateSupertypes().isEmpty())
        lo.print(datum.type().name().toString());

      if (datum instanceof PrimitiveDatum) print((PrimitiveDatum) datum);
      else if (datum instanceof RecordDatum) print((RecordDatum) datum);
      else if (datum instanceof ListDatum) print((ListDatum) datum);
      else if (datum instanceof MapDatum) print((MapDatum) datum);
//    else if (datum instanceof EnumDatum) print((EnumDatum) datum);
      else if (datum instanceof Datum) lo.print(datum.type().name().toString());
      else throw new UnsupportedOperationException(datum.type().name().toString());

      Datum meta = datum._raw().meta();
      if (meta != null) {
        lo.print("@");
        print(meta.type(), meta);
      }
    }
  }

  public void print(@NotNull PrimitiveDatum datum) throws Exc {
    if (withTypes && !datum.type().immediateSupertypes().isEmpty())
      lo.print("@");
    if (datum instanceof StringDatum) {
      String s = datum.getVal().toString();
      String escaped = s.replace("'", "\\'");
      lo.print("'").print(escaped).print("'");
    } else
      lo.print(datum.getVal().toString());
  }

  public void print(@NotNull RecordDatum datum) throws Exc {
    if (enter(datum)) try {
      lo.print("{").beginCInd();
      RecordDatum.Raw raw = datum._raw();
      boolean first = true;
      for (Field field : datum.type().fields()) {
        @Nullable Data data = raw.getData(field);
        if (data != null) {
          if (!first) lo.print(",").end();
          else first = false;
          brk(1).beginI(0).print("").print(field.name).print(": ");
          print(data);
        }
      }
      if (!first) lo.end();
      brk(1, -lo.getDefaultIndentation()).print("}").end();
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
        brk(1).beginI(0);
        print(data);
      }
      if (!first) lo.end();
      brk(1, -lo.getDefaultIndentation()).print("]").end();
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
        brk(1).beginI(0);
        print(entry.getKey().type(), entry.getKey());
        lo.print(": ");
        print(entry.getValue());
      }
      if (!first) lo.end();
      brk(1, -lo.getDefaultIndentation()).print(")").end();
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
