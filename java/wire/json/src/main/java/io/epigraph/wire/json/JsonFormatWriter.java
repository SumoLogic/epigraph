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
import java.util.Collection;
import java.util.Deque;

public class JsonFormatWriter implements FormatWriter<IOException> {

  private final @NotNull Writer out;

  public JsonFormatWriter(@NotNull Writer out) { this.out = out; }

  @Override
  public void write(@NotNull ReqOutputVarProjection projection, @Nullable Data data) throws IOException {
    if (data == null) {
      out.write("null");
    } else {
      Type type = data.type();
      projection.type().checkAssignable(type); // TODO assert instead?
      write(projection.polymorphicTails() != null, varProjections(projection, type), data);
    }
  }

  private void write(
      boolean renderPoly,
      @NotNull Deque<? extends ReqOutputVarProjection> projections, // non-empty, polymorphic tails ignored
      @Nullable Data data
  )
      throws IOException {
    if (data == null) {
      out.write("null"); // TODO may get away without this check
    } else {
      Type type = projections.peekLast().type(); // use deepest match type from here on
      // TODO check all projections (not just the ones that matched actual data type)?
      boolean renderMulti = type.kind() == TypeKind.UNION && needMultiRendering(projections);
      if (renderPoly) {
        out.write("{\"type\":\"");
        out.write(type.name().toString()); // TODO use (potentially short) type name used in request projection
        out.write("\",\"data\":");
      }
      if (renderMulti) out.write('{');
      boolean comma = false;
      for (Tag tag : type.tags()) {
        Deque<ReqOutputModelProjection> modelProjections = new ArrayDeque<>(projections.size());
        for (ReqOutputVarProjection vp : projections) {
          Tag vpTag = vp.type().tagsMap().get(tag.name());
          if (vpTag != null) {
            ReqOutputTagProjection tagProjection = vp.tagProjection(vpTag);
            if (tagProjection != null) modelProjections.add(tagProjection.projection());
          }
        }
        if (!modelProjections.isEmpty()) { // if this tag was mentioned in at least one projection
          if (renderMulti) {
            if (comma) out.write(',');
            else comma = true;
            out.write('"');
            out.write(tag.name());
            out.write("\":");
          }
          write(modelProjections, data._raw().getValue(tag));
        }
      }
      if (renderMulti) out.write('}');
      if (renderPoly) out.write('}');
    }
  }

  private static boolean needMultiRendering(@NotNull Collection<? extends ReqOutputVarProjection> projections) {
    String tagName = null;
    for (ReqOutputVarProjection vp : projections) {
      if (vp.parenthesized()) return true;
      for (Tag tag : vp.tagProjections().keySet()) {
        if (tagName == null) tagName = tag.name();
        else if (!tagName.equals(tag.name())) return true;
      }
    }
    return false;
  }

  private void write(@NotNull Deque<? extends ReqOutputModelProjection> projections, @Nullable Val value)
      throws IOException {
    if (value == null) {
      out.write("null");
    } else {
      ErrorValue error = value.getError();
      if (error == null) {
        Datum datum = value.getDatum();
        if (datum == null) {
          out.write("null");
        } else {
          DatumType model = projections.peekLast().model();
          switch (model.kind()) {
            case RECORD:
              write((Deque<? extends ReqOutputRecordModelProjection>) projections, (RecordDatum) datum);
              break;
            case MAP:
              write((Deque<? extends ReqOutputMapModelProjection>) projections, (MapDatum) datum);
              break;
            case LIST:
              write((Deque<? extends ReqOutputListModelProjection>) projections, (ListDatum) datum);
              break;
            case PRIMITIVE:
              write((Deque<? extends ReqOutputPrimitiveModelProjection>) projections, (PrimitiveDatum) datum);
              break;
            case ENUM:
//            write((Deque<? extends ReqOutputEnumModelProjection>) modelProjections, (EnumDatum) datum);
//            break;
            case UNION:
            default:
              throw new UnsupportedOperationException(model.kind().name());
          }
        }
      } else {
        write(error);
      }
    }
  }

  private void write(ErrorValue error) throws IOException {
//    out.write("{\"ERROR\":");
//    out.write(error.statusCode().toString());
//    out.write(",\"message\":");
//    write(error.message());
//    out.write('}');
    out.write('['); // rendering error as `[int, string]` array so it can't be confused with map datum
    out.write(error.statusCode().toString());
    out.write(',');
    write(error.message());
    out.write(']');
  }

  private void write(@NotNull Deque<? extends ReqOutputRecordModelProjection> projections, @NotNull RecordDatum datum)
      throws IOException {
    out.write('{');
    RecordType type = projections.peekLast().model();
    boolean comma = false;
    for (Field field : type.fields()) {
      Deque<ReqOutputVarProjection> varProjections = new ArrayDeque<>(projections.size());
      for (ReqOutputRecordModelProjection mp : projections) {
        Field mpField = mp.model().fieldsMap().get(field.name());
        if (mpField != null) {
          ReqOutputFieldProjection fieldProjection = mp.fieldProjection(mpField);
          if (fieldProjection != null) varProjections.add(fieldProjection.projection());
        }
      }
      if (!varProjections.isEmpty()) { // if this field was mentioned in at least one projection
        Data fieldData = datum._raw().getData(field);
        if (fieldData != null) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(field.name());
          out.write("\":");
          write(
              varProjections.stream().anyMatch(vp -> vp.polymorphicTails() != null),
              flatten(varProjections, fieldData.type()),
              fieldData
          );
        }
      }
    }
    out.write('}');
  }

  private void write(@NotNull Deque<? extends ReqOutputMapModelProjection> projections, @NotNull MapDatum datum)
      throws IOException {
    out.write("{");


    out.write("}");
  }

  private void write(@NotNull Deque<? extends ReqOutputListModelProjection> projections, @NotNull ListDatum datum)
      throws IOException {
    out.write("[ ]"); // FIXME
  }

  private void write(
      @NotNull Deque<? extends ReqOutputPrimitiveModelProjection> projections,
      @NotNull PrimitiveDatum datum
  ) throws IOException {
    if (datum instanceof StringDatum) {
      write(((StringDatum) datum).getVal());
    } else {
      // FIXME treat double values properly https://tools.ietf.org/html/rfc7159#section-6
      out.write(datum.getVal().toString());
    }
  }

  @Override
  public <M extends DatumType> void write(@Nullable ReqOutputModelProjection<M> projection, @Nullable Datum datum)
      throws IOException {

  }

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

  private static @NotNull Deque<? extends ReqOutputVarProjection> flatten(
      @NotNull Collection<? extends ReqOutputVarProjection> projections,
      @NotNull Type varType
  ) {
    ArrayDeque<ReqOutputVarProjection> acc = new ArrayDeque<>();
    for (ReqOutputVarProjection projection : projections) append(acc, projection, varType);
    return acc;
  }

  private void write(@Nullable String s) throws IOException {
    if (s == null) {
      out.write("null");
    } else {
      out.write('"');
      out.write(s); // FIXME apply proper json string escaping https://tools.ietf.org/html/rfc7159#section-7
      out.write('"');
    }
  }

}
