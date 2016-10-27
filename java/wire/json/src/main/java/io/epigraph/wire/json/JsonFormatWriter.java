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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.epigraph.wire.json.JsonFormatCommon.*;

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
    Type type = projections.peekLast()
        .type(); // use deepest match type from here on // TODO use JsonFormatCommon.mostSpecificType()
    // TODO check all projections (not just the ones that matched actual data type)?
    boolean renderMulti = type.kind() == TypeKind.UNION && needMultiRendering(projections);
    if (renderPoly) {
      out.write("{\"" + JsonFormat.POLYMORPHIC_TYPE_FIELD + "\":\"");
      out.write(type.name().toString()); // TODO use (potentially short) type name used in request projection?
      out.write("\",\"" + JsonFormat.POLYMORPHIC_VALUE_FIELD + "\":");
    }
    if (renderMulti) out.write('{');
    boolean comma = false;
    for (Tag tag : type.tags()) {
      Deque<? extends ReqOutputModelProjection> tagModelProjections =
          tagModelProjections(tag, projections, () -> new ArrayDeque<>(projections.size()));
      if (tagModelProjections != null) { // if this tag was mentioned in at least one projection
        if (renderMulti) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(tag.name());
          out.write("\":");
        }
        writeValue(tagModelProjections, data._raw().getValue(tag));
      }
    } // TODO if we're not rendering multi and zero tags were requested (projection error) - render error instead
    if (renderMulti) out.write('}');
    if (renderPoly) out.write('}');
  }

  private void writeValue(@NotNull Deque<? extends ReqOutputModelProjection> projections, @Nullable Val value)
      throws IOException {
    if (value == null) { // TODO in case of null value we should probably render NO_VALUE error?
      out.write("null");
    } else {
      ErrorValue error = value.getError();
      if (error == null) writeDatum(projections, value.getDatum());
      else writeError(error);
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
    out.write("{\"" + JsonFormat.ERROR_CODE_FIELD + "\":");
    out.write(error.statusCode().toString());
    out.write(",\"" + JsonFormat.ERROR_MESSAGE_FIELD + "\":");
    writeString(error.message());
    out.write('}');
  }

  private void writeRecord(
      @NotNull Deque<? extends ReqOutputRecordModelProjection> projections, // non-empty
      @NotNull RecordDatum datum
  ) throws IOException {
    out.write('{');
    // TODO take type from announced type tag (same for other datum kinds)?
    RecordType type = projections.peekLast().model();
    boolean comma = false;
    for (Field field : type.fields()) {
      Deque<? extends ReqOutputVarProjection> varProjections =
          fieldVarProjections(projections, field, () -> new ArrayDeque<>(projections.size()));
      if (varProjections != null) { // if this field was mentioned in at least one projection
        Data fieldData = datum._raw().getData(field);
        if (fieldData != null) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(field.name());
          out.write("\":");
          writeData(
              varProjections.stream().anyMatch(vp -> vp.polymorphicTails() != null),
              flatten(new ArrayDeque<>(), varProjections, fieldData.type()),
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
    else writeMapEntries( // TODO check ReqOutputMapModelProjection::keysRequired() and throw(?) if a key is missing
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
    assert !projections.isEmpty() : "no projection(s)";
    ArrayDeque<ReqOutputVarProjection> subProjections = new ArrayDeque<>(projections.size());
    for (P projection : projections) subProjections.add(varFunc.apply(projection));
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
            : polymorphicCache.computeIfAbsent(valueData.type(), t -> flatten(new ArrayDeque<>(), valueProjections, t));
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
    // TODO extract following to separate method and re-use in maps, too?
    boolean polymorphicValue = elementProjections.stream().anyMatch(vp -> vp.polymorphicTails() != null);
    Map<Type, Deque<? extends ReqOutputVarProjection>> polymorphicCache = polymorphicValue ? new HashMap<>() : null;
    boolean comma = false;
    for (Data element : datum._raw().elements()) {
      if (comma) out.write(',');
      else comma = true;
      Deque<? extends ReqOutputVarProjection> flatElementProjections = polymorphicCache == null
          ? elementProjections
          : polymorphicCache.computeIfAbsent(element.type(), t -> flatten(new ArrayDeque<>(), elementProjections, t));
      writeData(polymorphicCache != null, flatElementProjections, element);
    }
    out.write(']');
  }

  private void writePrimitive(
      @NotNull Deque<? extends ReqOutputPrimitiveModelProjection> projections,
      @NotNull PrimitiveDatum datum
  ) throws IOException { writePrimitive(datum); }

  private static @NotNull Deque<? extends ReqOutputVarProjection> varProjections(
      @NotNull ReqOutputVarProjection projection,
      @NotNull Type varType
  ) { return append(new ArrayDeque<>(projection.polymorphicDepth() + 1), projection, varType); }

  // FIXME take explicit type for all projectionless writes below

  @Override
  public void writeData(@Nullable Data data) throws IOException {
    if (data == null) {
      out.write("null");
    } else {
      Type type = data.type();
      if (type.kind() == TypeKind.UNION) { // TODO use instanceof instead of kind?
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
        Val value = data._raw().getValue(((DatumType) type).self);
        if (value == null) writeError(NO_VALUE);
        else writeValue(value);
      }
    }
  }

  private static final ErrorValue NO_VALUE = new ErrorValue(500, "No value", null);

  public void writeValue(@NotNull Val value) throws IOException {
    ErrorValue error = value.getError();
    if (error == null) writeDatum(value.getDatum());
    else writeError(error);
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
      out.write("{\"" + JsonFormat.MAP_ENTRY_KEY_FIELD + "\":");
      writeDatum(entry.getKey());
      out.write(",\"" + JsonFormat.MAP_ENTRY_VALUE_FIELD + "\":");
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
    if (datum instanceof StringDatum) writeString(((StringDatum) datum).getVal());
    else if (datum instanceof DoubleDatum) writeDouble(((DoubleDatum) datum).getVal());
    else out.write(datum.getVal().toString());
  }

  /** See https://tools.ietf.org/html/rfc7159#section-6. */
  private void writeDouble(@NotNull Double d) throws IOException {
    if (d.isInfinite() || d.isNaN()) out.write("null"); // TODO render ErrorValue(500) instead?
    else out.write(d.toString()); // TODO more compact representation / better rfc compliance?
  }

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

  private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

}
