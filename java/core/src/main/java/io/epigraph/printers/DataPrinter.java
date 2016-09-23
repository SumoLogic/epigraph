/* Created by yegor on 7/24/16. */

package io.epigraph.printers;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.WriterBackend;
import io.epigraph.data.*;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.DatumType;
import io.epigraph.types.RecordType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;
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
              lo.beginIInd(0).print("").print(tag.name).print(" ");
              print(value);
            }
          }
          if (comma) lo.end();
          /*if (!single)*/
          lo.brk(1, -lo.getDefaultIndentation()).print(">").end();
          break;
        default:
          print(data._raw().getValue(((DatumType) data.type()).self));
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
      lo.print("!").print(error.toString()); // TODO?
    }
  }

  public void print(@Nullable Datum datum) throws Exc {
    if (datum == null) {
      lo.print("null");
    } else {
      if (datum instanceof PrimitiveDatum) print((PrimitiveDatum) datum);
      else if (datum instanceof RecordDatum) print((RecordDatum) datum);
      else if (datum instanceof ListDatum) print((ListDatum) datum);
      else if (datum instanceof MapDatum) print((MapDatum) datum);
        //else if (datum instanceof EnumDatum) print((EnumDatum) datum);
      else if (datum instanceof Datum) lo.print(datum.type().name().toString());
      else throw new UnsupportedOperationException(datum.type().name().toString());
    }
  }

  public void print(@NotNull PrimitiveDatum datum) throws Exc {
    if (datum instanceof StringDatum) lo.print("\"").print(datum.getVal().toString()).print("\"");
    else lo.print(datum.getVal().toString());
  }

  public void print(@NotNull RecordDatum datum) throws Exc {
    if (enterSelf(datum, "...", "")) try {
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
    } finally { leaveSelf(datum); }
  }

  public void print(@NotNull ListDatum datum) throws Exc {
    if (enterSelf(datum, "...", "")) try {
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
    } finally { leaveSelf(datum); }
  }

  public void print(@NotNull MapDatum datum) throws Exc {
    if (enterSelf(datum, "...", "")) try {
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
    } finally { leaveSelf(datum); }
  }

  private boolean enterSelf(@NotNull Datum datum, @NotNull String left, @NotNull String right) throws Exc {
    Integer selfId = visiting.get(datum);
    if (selfId == null) {
      visiting.put(datum, UNASSIGNED_ID);
      return true;
    } else {
      if (selfId == UNASSIGNED_ID) {
        selfId = ++lastId;
        visiting.put(datum, selfId);
      }
      lo.print(left).print("@").print(selfId.toString()).print(right);
      return false;
    }
  }

  private void leaveSelf(@NotNull Datum datum) throws Exc {
    Integer selfId = visiting.remove(datum);
    if (selfId != null && selfId != UNASSIGNED_ID) lo.print("@").print(selfId.toString());
  }

}
