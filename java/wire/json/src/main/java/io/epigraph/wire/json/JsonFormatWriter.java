/* Created by yegor on 10/8/16. */

package io.epigraph.wire.json;

import io.epigraph.data.*;
import io.epigraph.errors.ErrorValue;
import io.epigraph.projections.req.output.*;
import io.epigraph.types.DatumType;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import io.epigraph.types.Type.Tag;
import io.epigraph.types.TypeKind;
import io.epigraph.wire.FormatWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;

public class JsonFormatWriter implements FormatWriter<IOException> {

  private final @NotNull Writer out;

  public JsonFormatWriter(@NotNull Writer out) { this.out = out; }

  @Override
  public void writeData(@NotNull ReqOutputVarProjection projection, @Nullable Data data) throws IOException {
    if (data == null) {
      out.write("null");
    } else {
      Type type = data.type();
      projection.type().checkAssignable(type); // TODO assert instead?
      writeData(projection.polymorphicTails() != null, varProjections(projection, type), data);
    }
  }

  private void writeData(
      boolean renderPoly,
      @NotNull Deque<? extends ReqOutputVarProjection> projections, // non-empty, polymorphic tails ignored
      @NotNull Data data
  ) throws IOException {
    Type type = projections.peekLast().type(); // use deepest match type from here on
    // TODO check all projections (not just the ones that matched actual data type)?
    boolean renderMulti = type.kind() == TypeKind.UNION && needMultiRendering(projections);
    if (renderPoly) {
      out.write("{\"type\":\"");
      out.write(type.name().toString()); // TODO use (potentially short) type name used in request projection?
      out.write("\",\"data\":");
    }
    if (renderMulti) out.write('{');
    boolean comma = false;
    for (Tag tag : type.tags()) {
      Deque<ReqOutputModelProjection> modelProjections = new ArrayDeque<>(projections.size());
      for (ReqOutputVarProjection vp : projections) {
        ReqOutputTagProjection tagProjection = vp.tagProjection(tag.name());
        if (tagProjection != null) modelProjections.add(tagProjection.projection());
      }
      if (!modelProjections.isEmpty()) { // if this tag was mentioned in at least one projection
        if (renderMulti) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(tag.name());
          out.write("\":");
        }
        writeValue(modelProjections, data._raw().getValue(tag));
      }
    } // TODO if we're not rendering multi and zero tags were requested (projection error) - render error instead
    if (renderMulti) out.write('}');
    if (renderPoly) out.write('}');
  }

  private static boolean needMultiRendering(@NotNull Collection<? extends ReqOutputVarProjection> projections) {
    String tagName = null;
    for (ReqOutputVarProjection vp : projections) {
      if (vp.parenthesized()) return true;
      for (String vpTagName : vp.tagProjections().keySet()) {
        if (tagName == null) tagName = vpTagName;
        else if (!tagName.equals(vpTagName)) return true;
      }
    }
    return tagName == null; // false if there was exactly one tag and not parenthesized projections
  }

  private void writeValue(@NotNull Deque<? extends ReqOutputModelProjection> projections, @Nullable Val value)
      throws IOException {
    if (value == null) {
      out.write("null");
    } else {
      ErrorValue error = value.getError();
      if (error == null) {
        writeDatum(projections, value.getDatum());
      } else {
        writeError(error);
      }
    }
  }

  @Override
  public void writeDatum(@Nullable ReqOutputModelProjection projection, @Nullable Datum datum) throws IOException {
    ArrayDeque<ReqOutputModelProjection> projections = new ArrayDeque<>(1);
    projections.add(projection);
    writeDatum(projections, datum);
  }

  private void writeDatum(@NotNull Deque<? extends ReqOutputModelProjection> projections, @Nullable Datum datum)
      throws IOException {
    if (datum == null) {
      out.write("null");
    } else {
      DatumType model = projections.peekLast().model();
      switch (model.kind()) {
        case RECORD:
          writeRecord((Deque<? extends ReqOutputRecordModelProjection>) projections, (RecordDatum) datum);
          break;
        case MAP:
          writeMap((Deque<? extends ReqOutputMapModelProjection>) projections, (MapDatum) datum);
          break;
        case LIST:
          writeList((Deque<? extends ReqOutputListModelProjection>) projections, (ListDatum) datum);
          break;
        case PRIMITIVE:
          writePrimitive((Deque<? extends ReqOutputPrimitiveModelProjection>) projections, (PrimitiveDatum) datum);
          break;
        case ENUM:
//            writeEnum((Deque<? extends ReqOutputEnumModelProjection>) modelProjections, (EnumDatum) datum);
//            break;
        case UNION:
        default:
          throw new UnsupportedOperationException(model.kind().name());
      }
    }

  }

  public void writeError(@NotNull ErrorValue error) throws IOException {
    out.write("{\"ERROR\":");
    out.write(error.statusCode().toString());
    out.write(",\"message\":");
    writeString(error.message());
    out.write('}');
  }

  private void writeRecord(
      @NotNull Deque<? extends ReqOutputRecordModelProjection> projections, // non-empty
      @NotNull RecordDatum datum
  ) throws IOException {
    out.write('{');
    RecordType type = projections.peekLast().model();
    boolean comma = false;
    for (Field field : type.fields()) {
      Deque<ReqOutputVarProjection> varProjections = new ArrayDeque<>(projections.size());
      for (ReqOutputRecordModelProjection mp : projections) {
        ReqOutputFieldProjection fieldProjection = mp.fieldProjection(field.name());
        if (fieldProjection != null) varProjections.add(fieldProjection.projection());
      }
      if (!varProjections.isEmpty()) { // if this field was mentioned in at least one projection
        Data fieldData = datum._raw().getData(field);
        if (fieldData != null) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(field.name());
          out.write("\":");
          writeData(
              varProjections.stream().anyMatch(vp -> vp.polymorphicTails() != null),
              flatten(varProjections, fieldData.type()),
              fieldData
          );
        }
      }
    }
    out.write('}');
  }

  private void writeMap(
      @NotNull Deque<? extends ReqOutputMapModelProjection> projections, // non-empty
      @NotNull MapDatum datum
  ) throws IOException {
    out.write("[");
    List<? extends ReqOutputKeyProjection> keyProjections = keyProjections(projections);
    Deque<? extends ReqOutputVarProjection> valueProjections = subProjections(
        projections,
        ReqOutputMapModelProjection::itemsProjection
    );
    boolean polymorphicValue = valueProjections.stream().anyMatch(vp -> vp.polymorphicTails() != null);
    Map<Type, Deque<? extends ReqOutputVarProjection>> polymorphicCache = polymorphicValue ? new HashMap<>() : null;
    if (keyProjections == null) writeMapEntries(
        valueProjections,
        polymorphicCache,
        datum._raw().elements().entrySet(),
        Map.Entry::getKey,
        Map.Entry::getValue
    );
    else writeMapEntries(
        valueProjections,
        polymorphicCache,
        keyProjections,
        ReqOutputKeyProjection::value,
        kp -> datum._raw().elements().get(kp.value()) // Datum.equals() contract says this is ok.
    );
    out.write("]");
  }

  /** Builds a superset of all key projections. `null` is treated as wildcard and yields wildcard result immediately. */
  private static @Nullable List<? extends ReqOutputKeyProjection> keyProjections(
      @NotNull Deque<? extends ReqOutputMapModelProjection> projections // non-empty
  ) {
    switch (projections.size()) {
      case 0:
        throw new IllegalArgumentException("No projections");
      case 1:
        return projections.peek().keys();
      default:
        List<ReqOutputKeyProjection> keys = null;
        for (ReqOutputMapModelProjection projection : projections) {
          List<? extends ReqOutputKeyProjection> projectionKeys = projection.keys();
          if (projectionKeys == null) return null;
          if (keys == null) keys = new ArrayList<>(projectionKeys);
          else keys.addAll(projectionKeys);
        }
        return keys;
    }
  }

  private static <P> @NotNull Deque<? extends ReqOutputVarProjection> subProjections(
      @NotNull Deque<? extends P> projections, // non-empty
      @NotNull Function<P, ReqOutputVarProjection> varFunc
  ) {
    ArrayDeque<ReqOutputVarProjection> subProjections = new ArrayDeque<>(projections.size());
    switch (projections.size()) {
      case 0:
        throw new IllegalArgumentException("No projections");
      case 1:
        subProjections.add(varFunc.apply(projections.peek()));
        break;
      default:
        for (P projection : projections) subProjections.add(varFunc.apply(projection));
    }
    return subProjections;
  }

  private <E> void writeMapEntries(
      @NotNull Deque<? extends ReqOutputVarProjection> valueProjections,
      @Nullable Map<Type, Deque<? extends ReqOutputVarProjection>> polymorphicCache,
      @NotNull Iterable<E> entries,
      @NotNull Function<E, @NotNull Datum> keyFunc,
      @NotNull Function<E, @Nullable Data> valueFunc
  ) throws IOException {
    boolean comma = false;
    for (E entry : entries) {
      @NotNull Datum key = keyFunc.apply(entry);
      @Nullable Data valueData = valueFunc.apply(entry);
      if (valueData != null) {
        if (comma) out.write(',');
        else comma = true;
        out.write("{\"key\":");
        writeDatum(key);
        out.write(",\"value\":");
        Deque<? extends ReqOutputVarProjection> flatValueProjections = polymorphicCache == null
            ? valueProjections
            : polymorphicCache.computeIfAbsent(valueData.type(), t -> flatten(valueProjections, t));
        writeData(polymorphicCache != null, flatValueProjections, valueData);
        out.write('}');
      }
    }
  }

  private void writeList(@NotNull Deque<? extends ReqOutputListModelProjection> projections, @NotNull ListDatum datum)
      throws IOException {
    out.write('[');
    Deque<? extends ReqOutputVarProjection> elementProjections = subProjections(
        projections,
        ReqOutputListModelProjection::itemsProjection
    );
    boolean polymorphicValue = elementProjections.stream().anyMatch(vp -> vp.polymorphicTails() != null);
    Map<Type, Deque<? extends ReqOutputVarProjection>> polymorphicCache = polymorphicValue ? new HashMap<>() : null;
    boolean comma = false;
    for (Data element : datum._raw().elements()) {
      if (comma) out.write(',');
      else comma = true;
      Deque<? extends ReqOutputVarProjection> flatElementProjections = polymorphicCache == null
          ? elementProjections
          : polymorphicCache.computeIfAbsent(element.type(), t -> flatten(elementProjections, t));
      writeData(polymorphicCache != null, flatElementProjections, element);
    }
    out.write(']');
  }

  private static @NotNull Deque<? extends ReqOutputVarProjection> flatten(
      @NotNull Collection<? extends ReqOutputVarProjection> projections,
      @NotNull Type varType
  ) { // TODO more careful ordering of projections might be needed to ensure last one is the most precise in complex cases
    ArrayDeque<ReqOutputVarProjection> acc = new ArrayDeque<>();
    for (ReqOutputVarProjection projection : projections) append(acc, projection, varType);
    return acc;
  }

  private void writePrimitive(
      @NotNull Deque<? extends ReqOutputPrimitiveModelProjection> projections,
      @NotNull PrimitiveDatum datum
  ) throws IOException { writePrimitive(datum); }

  private static @NotNull Deque<? extends ReqOutputVarProjection> varProjections(
      @NotNull ReqOutputVarProjection projection,
      @NotNull Type varType
  ) { return append(new ArrayDeque<>(projection.polymorphicDepth() + 1), projection, varType); }

  private static <C extends Collection<ReqOutputVarProjection>> C append(
      @NotNull C acc,
      @NotNull ReqOutputVarProjection varProjection,
      @NotNull Type varType
  ) {
    acc.add(varProjection);
    Iterable<ReqOutputVarProjection> tails = varProjection.polymorphicTails();
    if (tails != null) for (ReqOutputVarProjection tail : tails) {
      if (tail.type().isAssignableFrom(varType)) return append(acc, tail, varType);
    }
    return acc;
  }

  @Override
  public void writeData(@Nullable Data data) throws IOException {
    if (data == null) {
      out.write("null");
    } else {
      Type type = data.type();
      boolean renderMulti = type.kind() == TypeKind.UNION;
      if (renderMulti) {
        out.write('{');
        boolean comma = false;
        for (Tag tag : type.tags()) {
          Val value = data._raw().getValue(tag);
          if (value != null) {
            if (comma) out.write(',');
            else comma = true;
            out.write('"');
            out.write(tag.name());
            out.write("\":");
            writeValue(value);
          }
        }
        out.write('}');
      } else {
        Tag tag = ((DatumType) type).self; // TODO use instanceof instead of kind?
        Val value = data._raw().getValue(tag);
        if (value == null) value = tag.type.createValue(new ErrorValue(500, "No value", null));
        writeValue(value);
      }
    }
  }

  public void writeValue(@NotNull Val value) throws IOException {
    ErrorValue error = value.getError();
    if (error == null) {
      writeDatum(value.getDatum());
    } else {
      writeError(error);
    }
  }


  @Override
  public void writeDatum(@Nullable Datum datum) throws IOException {
    if (datum == null) {
      out.write("null");
    } else {
      DatumType model = datum.type();
      switch (model.kind()) {
        case RECORD:
          writeRecord((RecordDatum) datum);
          break;
        case MAP:
          writeMap((MapDatum) datum);
          break;
        case LIST:
          writeList((ListDatum) datum);
          break;
        case PRIMITIVE:
          writePrimitive((PrimitiveDatum) datum);
          break;
        case ENUM:
//        writeEnum((EnumDatum) datum);
//        break;
        case UNION:
        default:
          throw new UnsupportedOperationException(model.kind().name());
      }
    }
  }

  private void writeRecord(@NotNull RecordDatum datum) throws IOException {
    out.write('{');
    boolean comma = false;
    for (Field field : datum.type().fields()) {
      Data fieldData = datum._raw().getData(field);
      if (fieldData != null) {
        if (comma) out.write(',');
        else comma = true;
        out.write('"');
        out.write(field.name());
        out.write("\":");
        writeData(fieldData);
      }
    }
    out.write('}');
  }

  private void writeMap(@NotNull MapDatum datum) throws IOException {
    out.write("[");
    boolean comma = false;
    for (Map.Entry<Datum.Imm, @NotNull ? extends Data> entry : datum._raw().elements().entrySet()) {
      if (comma) out.write(',');
      else comma = true;
      out.write("{\"key\":");
      writeDatum(entry.getKey());
      out.write(",\"value\":");
      writeData(entry.getValue());
      out.write('}');
    }
    out.write("]");
  }

  private void writeList(@NotNull ListDatum datum) throws IOException {
    out.write('[');
    boolean comma = false;
    for (Data elementData : datum._raw().elements()) {
      if (comma) out.write(',');
      else comma = true;
      writeData(elementData);
    }
    out.write(']');
  }

  private void writePrimitive(@NotNull PrimitiveDatum datum) throws IOException {
    if (datum instanceof StringDatum) {
      writeString(((StringDatum) datum).getVal());
    } else {
      // FIXME treat double values (NaN, infinity) properly https://tools.ietf.org/html/rfc7159#section-6
      out.write(datum.getVal().toString());
    }
  }

  private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

  /** See https://tools.ietf.org/html/rfc7159#section-7. */
  private void writeString(@Nullable String s) throws IOException {
    if (s == null) {
      out.write("null");
    } else {
      out.write('"');
      int length = s.length(), from = 0;
      char c;
      String escape = null;
      for (int i = 0; i < length; ++i) {
        c = s.charAt(i);
        switch (c) {
          case '\b':
            escape = "\\b";
            break;
          case '\t':
            escape = "\\t";
            break;
          case '\n':
            escape = "\\n";
            break;
          case '\f':
            escape = "\\f";
            break;
          case '\r':
            escape = "\\r";
            break;
          case '"':
            escape = "\\\"";
            break;
          case '\\':
            escape = "\\\\";
            break;
          default:
            if (c < 0x20) escape = "\\u00" + HEX_DIGITS[c >> 4] + HEX_DIGITS[c & 0x0f];
        }
        if (escape != null) {
          int len = i - from;
          if (len != 0) out.write(s, from, len);
          out.write(escape);
          escape = null;
          from = i + 1;
        }
      }
      int len = length - from;
      if (len != 0) out.write(s, from, len);
      out.write('"');
    }
  }

}
