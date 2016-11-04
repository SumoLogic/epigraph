package ws.epigraph.gdata;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class GDataPrettyPrinter<E extends Exception> {
  @NotNull
  private final Layouter<E> l;

  public GDataPrettyPrinter(@NotNull Layouter<E> l) {this.l = l;}

  public void print(@NotNull GDataValue dataValue) throws E {
    if (dataValue instanceof GData)
      print((GData) dataValue);
    else if (dataValue instanceof GDatum)
      print((GDatum) dataValue);
    else throw new IllegalArgumentException("Unknown class: " + dataValue.getClass().getName());
  }

  public void print(@NotNull GData data) throws E {
    l.beginCInd();

    @Nullable TypeRef typeRef = data.typeRef();
    if (typeRef != null) l.print(typeRef.toString()).brk();

    l.print("<");

    boolean first = true;
    for (Map.Entry<String, GDatum> entry : data.tags().entrySet()) {
      if (first) first = false;
      else l.print(",");

      l.brk().beginIInd().print(entry.getKey()).print(":").brk();
      print(entry.getValue());
      l.end();
    }

    l.brk(1, -l.getDefaultIndentation()).print(">").end();
  }

  public void print(@NotNull GDatum datum) throws E {
    if (datum instanceof GRecordDatum) print((GRecordDatum) datum);
    else if (datum instanceof GMapDatum) print((GMapDatum) datum);
    else if (datum instanceof GListDatum) print((GListDatum) datum);
    else if (datum instanceof GPrimitiveDatum) print((GPrimitiveDatum) datum);
    else if (datum instanceof GNullDatum) print((GNullDatum) datum);
    else throw new UnsupportedOperationException("Unsupported datum class: " + datum.getClass().getName());
  }

  private void print(@NotNull GRecordDatum d) throws E {
    l.beginCInd();

    @Nullable TypeRef typeRef = d.typeRef();
    if (typeRef != null) l.print(typeRef.toString()).brk();

    l.print("{");

    boolean first = true;
    for (Map.Entry<String, GDataValue> entry : d.fields().entrySet()) {
      if (first) first = false;
      else l.print(",");

      l.brk().beginIInd().print(entry.getKey()).print(":").brk();
      print(entry.getValue());
      l.end();
    }

    l.brk(1, -l.getDefaultIndentation()).print("}").end();
  }

  private void print(@NotNull GMapDatum d) throws E {
    l.beginCInd();

    @Nullable TypeRef typeRef = d.typeRef();
    if (typeRef != null) l.print(typeRef.toString()).brk();

    l.print("(");

    boolean first = true;
    for (Map.Entry<GDatum, GDataValue> entry : d.entries().entrySet()) {
      if (first) first = false;
      else l.print(",");

      l.brk().beginIInd();
      print(entry.getKey());
      l.print(":").brk();
      print(entry.getValue());
      l.end();
    }

    l.brk(1, -l.getDefaultIndentation()).print(")").end();
  }

  private void print(@NotNull GListDatum d) throws E {
    l.beginCInd();

    @Nullable TypeRef typeRef = d.typeRef();
    if (typeRef != null) l.print(typeRef.toString()).brk();

    l.print("[");

    boolean first = true;
    for (GDataValue value : d.values()) {
      if (first) first = false;
      else l.print(",");

      l.brk().beginIInd();
      print(value);
      l.end();
    }

    l.brk(1, -l.getDefaultIndentation()).print("]").end();
  }

  private void print(@NotNull GPrimitiveDatum d) throws E {
    Object value = d.value();
    String valueString;

    if (value instanceof String) valueString = "\"" + value + '"';
    else valueString = value.toString();

    l.beginCInd();
    @Nullable TypeRef typeRef = d.typeRef();
    if (typeRef != null) l.print(typeRef.toString()).print("@");

    l.print(valueString).end();
  }

  private void print(@NotNull GNullDatum d) throws E {
    l.beginCInd();
    @Nullable TypeRef typeRef = d.typeRef();
    if (typeRef != null) l.print(typeRef.toString()).print("@");

    l.print("null").end();
  }
}
