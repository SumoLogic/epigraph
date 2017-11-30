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

package ws.epigraph.wire.json.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.ListDatum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.PrimitiveDatum;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.gen.GenFieldProjection;
import ws.epigraph.projections.gen.GenFieldProjectionEntry;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenProjectionsComparator;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenEntityProjection;
import ws.epigraph.refs.QnTypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.wire.AbstractFormatReader;
import ws.epigraph.wire.FormatException;
import ws.epigraph.wire.json.JsonFormat;
import ws.epigraph.wire.json.JsonFormatCommon;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static ws.epigraph.wire.json.JsonFormatCommon.*;

/* JSON wire format gammar: // can be visualized (railroad diagram) here: http://www.bottlecaps.de/rr/ui

Data ::= BackRef | PolyData | MonoData // PolyData triggered by projection (polymorphic tails presence)
  BackRef ::= '{' '"REC"' ':' integer '}' // integer = how many var datas up the stack to skip to get the same instance
  PolyData ::= '{' '"TYPE"' ':' string ',' '"DATA"' ':' MonoData '}'
  MonoData ::= MultiValue | Value // MultiValue triggered by projection (parenthesized flag)
    MultiValue ::= '{' ( tag ':' Value ( ',' tag ':' Value )* )? '}' // 0 or more comma-separated entries
      tag ::= string
    Value ::= PolyValue | MonoValue // PolyValue triggered by projection (polymorphic tails presence)
      PolyValue ::= '{' '"TYPE"' ':' string ',' '"DATA"' ':' MonoValue '}'
      MonoValue ::= Error | Datum | 'null'
        Error ::= '{' "ERROR" ':' integer ',' "message" ':' string '}' // TODO make it more like exception
        Datum ::= DatumWithMeta | DatumNoMeta // depending on meta-projection presence
          DatumWithMeta ::= '{' '"META"' ':' DatumNoMeta ',' '"DATA"' ':' DatumNoMeta '}'
          DatumNoMeta ::= Record | Map | List | Primitive | Enum
            Record ::= '{' ( field ':' Data ( ',' field ':' Data )* )? '}' // 0 or more comma-separated entries
                field ::= string
            Map ::= '[' ( ( MapEntry ',' )* MapEntry )? ']' // 0 or more comma-separated entries
              MapEntry ::= '{' '"K"' ':' DatumNoMeta ',' '"V"' ':' Data '}'
            List ::= '[' ( ( Data ',' )* Data )? ']' // 0 or more comma-separated entries
            Primitive ::= string | number | 'true' | 'false'
            Enum ::= string // ? TODO

*/

/**
 * Abstract projection-driven JSON data reader.
 *
 * @author Yegor Borovikov
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class AbstractJsonFormatReader<
    VP extends GenEntityProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<TP, /*MP*/?, ?, ?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>
    //,PMP extends GenPrimitiveModelProjection<PMP, ?>
    >
    extends AbstractFormatReader<VP, TP, MP, RMP, FPE, FP, MMP> {

  protected static final @NotNull JsonFactory JSON_FACTORY = new JsonFactory();

  private static final @NotNull JsonToken[] NO_JSON_TOKENS = new JsonToken[0];

  private final @NotNull JsonParser in;
  private final @NotNull TypesResolver typesResolver;
  private final @NotNull LinkedList<VisitedDataEntry> dataByLevel = new LinkedList<>();

  protected AbstractJsonFormatReader(@NotNull JsonParser jsonParser, final @NotNull TypesResolver typesResolver) {
    this.in = jsonParser;
    this.typesResolver = typesResolver;
  }

  public void reset() {
    dataByLevel.clear();
    resetParserStateRecording();
  }

  protected abstract GenProjectionsComparator<VP, TP, MP, RMP, MMP, LMP, ?, FPE, FP> projectionsComparator();

  @Override
  public @Nullable Data readData(@NotNull VP projection) throws IOException, JsonFormatException {
    Data data = readData((Type) projection.type(), Collections.singletonList(projection));
    ensureEOF();
    return data;
  }

  // DATA ::= RECDATA or POLYDATA or MONODATA
  private @Nullable Data readData(
      @NotNull Type typeBound,
      @NotNull List<VP> projections // non-empty, polymorphic tails respected
  ) throws IOException, JsonFormatException {

    if (nextToken() == null) return null;
    return finishReadingData(typeBound, projections);
  }

  private @NotNull Data finishReadingData(
      @NotNull Type typeBound,
      @NotNull List<VP> projections // non-empty, polymorphic tails respected
  ) throws IOException, JsonFormatException {
    assert !projections.isEmpty();
    boolean readPoly = projections.stream().anyMatch(vp -> vp.polymorphicTails() != null);

    JsonToken token = currentToken();

    // check for REC
    if (token == JsonToken.START_OBJECT) {

      // we need a simple 1-token lookahead here.
      startParserStateRecording();
      if (JsonFormat.REC_FIELD.equals(nextFieldName())) {
        stepOver(JsonToken.VALUE_NUMBER_INT, "recursion depth");
        int revStackDepth = currentValueAsInt();

        int idx = dataByLevel.size() - revStackDepth;

        if (idx < 0 || idx >= dataByLevel.size())
          throw error("Invalid recursion level " + revStackDepth);

        final VisitedDataEntry visitedDataEntry = dataByLevel.get(idx);
        if (visitedDataEntry == null)
          throw error("Can't find data by recursion level " + revStackDepth);

        // assuming that exactly the same, not just structurally same projection was used for writing
        // need full data validation in place if we want to accept structurally equal projections.

        // for instance {foo{foo{foo...}}} data can be serialized with this projection:
        // $rec=(foo $rec)
        // and deserialized with
        // $rec=(foo(foo $rec))
        if (!visitedDataEntry.matches(projections))
          throw error("Projection doesn't match recursive data");

        resetParserStateRecording();
        stepOver(JsonToken.END_OBJECT);

        return visitedDataEntry.builder;
      }
      replayParserState();
    }

    final Type type;
    if (readPoly) { // { "type": "list[epigraph.String]", "data": MONODATA }
      ensure(token, JsonToken.START_OBJECT);
      type = readType(projections);
      stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD); // "data"
      nextNonEof(); // position parser on first MONODATA token
    } else {
      Type projectionType =
          (Type) projections.get(projections.size() - 1).type(); // effectiveType; // mostSpecificType(projections);
      type = projectionType.isAssignableFrom(typeBound) ? typeBound : projectionType; // pick most specific
      // current token is first MONODATA token
    }

    // read MONODATA
    List<VP> flattened = flatten(new ArrayList<>(), projections, type);

    String monoTagName = type.kind() == TypeKind.ENTITY ? JsonFormatCommon.monoTag(flattened) : DatumType.MONO_TAG_NAME;
    final Data.Builder data = type.createDataBuilder();

    dataByLevel.add(new VisitedDataEntry(data, projections));

    if (monoTagName == null) { // MULTIDATA ::= { "tag": VALUE, ... }
      finishReadingMultiData(type, data, flattened);
    } else { // VALUE ::= ERROR or DATUM or null
      Tag tag = type.tagsMap().get(monoTagName);
      assert tag != null : "invalid tag";
      List<MP> tagModelProjections =
          tagModelProjections(tag, flattened, () -> new ArrayList<>(flattened.size()));
      assert tagModelProjections != null : "missing mono tag";
      data._raw().setValue(tag, finishReadingValue((DatumType) tag.type(), tagModelProjections));
    }

    if (readPoly) stepOver(JsonToken.END_OBJECT);

    dataByLevel.removeLast();
    return data;
  }

  // MULTIDATA ::= { "tag": VALUE, ... }
  private @NotNull Data.Builder finishReadingMultiData(
      @NotNull Type effectiveType,
      @NotNull Data.Builder data,
      @NotNull List<VP> projections // non-empty, polymorphic tails ignored
  ) throws IOException, JsonFormatException {

    assert !projections.isEmpty();

    JsonToken token = currentToken();
    ensure(token, JsonToken.START_OBJECT);

    while ((token = nextToken()) == JsonToken.FIELD_NAME) {
      String tagName = currentName();
      Tag tag = effectiveType.tagsMap().get(tagName);
      if (tag == null)
        throw error("Unknown tag '" + tagName + "' in type '" + effectiveType.name().toString() + "'");

      List<MP> tagModelProjections =
          tagModelProjections(tag, projections, () -> new ArrayList<>(projections.size()));
      if (tagModelProjections == null) { // the tag was not requested in projection
        throw error("Unexpected tag '" + tagName + "'");
      } else {
        Val value = readValue((DatumType) tag.type(), tagModelProjections);
        data._raw().setValue(tag, value);
      }
    }
    ensure(token, JsonToken.END_OBJECT);

    for (final VP projection : projections) {
      for (final TP tagProjectionEntry : projection.tagProjections().values()) {
        if (tagRequired(tagProjectionEntry)) {
          final Val value = data._raw().getValue((Tag) tagProjectionEntry.tag());
          if (value == null || value.getDatum() == null)
            throw error("Missing data for required tag '" + tagProjectionEntry.tag().name() + "'");
        }
      }
    }

    return data;
  }

  protected boolean tagRequired(@NotNull TP tagProjection) { return false; }

  // VALUE ::= POLYVALUE | MONOVALUE
  private @NotNull Val readValue(
      @NotNull DatumType typeBound,
      @NotNull List<MP> projections // non-empty
  ) throws IOException, JsonFormatException {
    nextNonEof();
    return finishReadingValue(typeBound, projections);
  }

  @Override
  public @NotNull Val readValue(final @NotNull MP projection, final int pathSteps) throws IOException, FormatException {
    return super.readValue(projection, pathSteps);
  }

  // VALUE ::= POLYVALUE | MONOVALUE
  private @NotNull Val finishReadingValue(
      @NotNull DatumType typeBound,
      @NotNull List<MP> projections // non-empty
  ) throws IOException, JsonFormatException {
    assert !projections.isEmpty();
    boolean readPoly = projections.stream().anyMatch(vp -> vp.polymorphicTails() != null);

    JsonToken token = currentToken();
    final DatumType type;
    if (readPoly) {
      ensure(token, JsonToken.START_OBJECT);
      type = readModelType(projections);
      stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD); // "data"
      nextNonEof(); // position parser on first MONODATA token
    } else {
      DatumType projectionType = (DatumType) projections.get(projections.size() - 1)
          .type(); // effectiveType; // mostSpecificType(projections);
      type = projectionType.isAssignableFrom(typeBound) ? typeBound : projectionType; // pick most specific
    }

    Val result = finishReadingMonoValue(type, projections);

    if (readPoly) stepOver(JsonToken.END_OBJECT);

    return result;
  }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val finishReadingMonoValue(
      @NotNull DatumType type,
      @NotNull Collection<MP> tagModelProjections // non-empty
  ) throws IOException, JsonFormatException {

    JsonToken token = currentToken();
    assert !tagModelProjections.isEmpty();

    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;
    // datum
    final @NotNull Datum datum = finishReadingDatum(type, firstFieldName, tagModelProjections);
    return datum.asValue();
  }

  protected @Nullable MP getMetaProjection(@NotNull MP projection) { return null; }

  // DATUM ::= DATUM_WITH_META | DATUM_NO_META                           // depending on meta-projection presence
  // DATUM_WITH_META ::= '{' "meta" ':' DATUM_NO_META ',' "data" ':' DATUM_NO_META '}'
  @SuppressWarnings("unchecked")
  private @NotNull Datum finishReadingDatum(
      final @NotNull DatumType type,
      @Nullable String fieldName,
      final @NotNull Collection<? extends MP> modelProjections) throws IOException, JsonFormatException {

    List<MP> flattened = flatten(new ArrayList<>(), modelProjections, type);

    Collection<MP> metaProjections = flattened.stream()
        .map(this::getMetaProjection)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayList::new));

    final Datum datum;

    if (metaProjections.isEmpty()) {
      datum = finishReadingDatumNoMeta(fieldName, flattened, type);
    } else {
      if (fieldName == null) throw expected(JsonToken.START_OBJECT.asString());
      final DatumType metaType = type.metaType();
      if (metaType == null)
        throw error(
            String.format("Meta-projections can't be specified, type '%s' doesn't have a meta-type", type.name())
        );

      Datum.Builder _datum = null;
      Datum _meta = null;

      while (true) {
        if (Objects.equals(fieldName, JsonFormat.DATUM_META_FIELD)) {
          if (_meta != null) throw error("Field '" + JsonFormat.DATUM_META_FIELD + "' must only be specified once");
          _meta = readDatumNoMeta(metaProjections, metaType);
          @NotNull JsonToken token = nextNonEof();
          if (token == JsonToken.END_OBJECT) break;
          if (token == JsonToken.FIELD_NAME) fieldName = currentName();
          else throw expected("field name or '}'");
        } else if (Objects.equals(fieldName, JsonFormat.DATUM_VALUE_FIELD)) {
          if (_datum != null) throw error("Field '" + JsonFormat.DATUM_VALUE_FIELD + "' must only be specified once");
          _datum = readDatumNoMeta(flattened, type);
          @NotNull JsonToken token = nextNonEof();
          if (token == JsonToken.END_OBJECT) break;
          if (token == JsonToken.FIELD_NAME) fieldName = currentName();
          else throw expected("field name or '}'");
        }
      }

      if (_meta != null) {
        if (_datum == null) throw error("meta-data can't be provided for a null data");
        else _datum._raw().setMeta(_meta);
      }

      datum = _datum;
    }

    return datum;
  }

  private @NotNull Datum.Builder readDatumNoMeta(
      @NotNull Collection<MP> modelProjections,
      @NotNull DatumType type) throws IOException, JsonFormatException {

    @NotNull JsonToken token = nextNonEof();
    @Nullable String firstFieldName = token == JsonToken.START_OBJECT ? nextFieldName() : null;
    return finishReadingDatumNoMeta(firstFieldName, modelProjections, type);
  }

  @SuppressWarnings("unchecked")
  private @NotNull Datum.Builder finishReadingDatumNoMeta(
      final @Nullable String firstFieldName,
      final @NotNull Collection<MP> tagModelProjections,
      final DatumType type) throws IOException, JsonFormatException {

    final @NotNull Datum.Builder datum;
    switch (type.kind()) {
      case RECORD:
        datum = finishReadingRecord(
            firstFieldName,
            (RecordType) type,
            (Collection<RMP>) tagModelProjections
        );
        break;
      case MAP:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingMap(
            (MapType) type,
            (Collection<MMP>) tagModelProjections
        );
        break;
      case LIST:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingList(
            (ListType) type,
            (Collection<LMP>) tagModelProjections
        );
        break;
      case PRIMITIVE:
        if (firstFieldName != null) throw expected("primitive value");
        datum = finishReadingPrimitive(
            (PrimitiveType<?>) type
            // (Collection<PMP>) tagModelProjections
        );
        break;
//    case ENUM: // TODO once enums are supported
//      datum = readEnum(
//        token,
//        (EnumType) type,
//        (Collection<ReqOutputEnumModelProjection>) tagModelProjections
//      );
//      break;
      case ENTITY: // this one is 500 - there should be no such model projections
      default:
        throw new UnsupportedOperationException(type.kind().name());
    }
    return datum;
  }

  private @NotNull ErrorValue finishReadingError()
      throws IOException, JsonFormatException { // `: 404, "message": "blah" }`
    stepOver(JsonToken.VALUE_NUMBER_INT, "integer value");
    int errorCode;
    try { errorCode = currentValueAsInt(); } catch (JsonParseException ignored) {
      throw expected("integer error code");
    }
    stepOver(JsonFormat.ERROR_MESSAGE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String message = currentText();
    // TODO read custom error properties here (if we decide to support these)
    stepOver(JsonToken.END_OBJECT);
    return new ErrorValue(errorCode, message, null);
  }

  // `}` or `: DATA, "field": DATA, ... }`
  private @NotNull RecordDatum.Builder finishReadingRecord(
      @Nullable String fieldName,
      @NotNull RecordType type,
      @NotNull Collection<RMP> projections // non-empty
  ) throws IOException, JsonFormatException {

    RecordDatum.Builder datum = type.createBuilder();
    if (fieldName == null) { // empty record?
      ensureCurr(JsonToken.END_OBJECT, "field name or '}'");
      return datum;
    } else {
      while (true) {
        Field field = type.fieldsMap().get(fieldName);
        if (field == null) throw error(
            "Unknown field '" + fieldName + "' in record type '" + type.name().toString() + "'"
        );
        List<VP> varProjections =
            fieldVarProjections(projections, field, () -> new ArrayList<>(projections.size()));
        if (varProjections == null) throw error("Unexpected field '" + fieldName + "'");
        Data fieldData = readData(field.dataType().type(), varProjections);
        datum._raw().setData(field, fieldData);

        JsonToken token = nextNonEof();
        if (token == JsonToken.END_OBJECT) break;
        if (token == JsonToken.FIELD_NAME) fieldName = currentName();
        else throw expected("field name or '}'");
      }
    }

    for (RMP projection : projections)
      for (final Map.Entry<String, FPE> entry : projection.fieldProjections().entrySet())
        if (fieldRequired(entry.getValue())) {
          final Data data = datum._raw().getData((Field) entry.getValue().field());
          if (data == null)
            throw error("Required field '" + entry.getKey() + "' is missing");
          else if (data.type().kind() != TypeKind.ENTITY) {
            final Val val = data._raw().tagValues().get(DatumType.MONO_TAG_NAME);
            if (val == null || val.getDatum() == null)
              throw error("Required field '" + entry.getKey() + "' is missing");
          }
        }

    return datum;
  }

  protected boolean fieldRequired(@NotNull FPE fieldEntry) { return false; }

  // `]` or ` MAP_ENTRY , MAP ENTRY ... ]`
  // MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
  private @NotNull MapDatum.Builder finishReadingMap(
      @NotNull MapType type,
      @NotNull Collection<MMP> projections // non-empty
  ) throws IOException, JsonFormatException {

    JsonToken token = currentToken();
    ensure(token, JsonToken.START_ARRAY);

    final @NotNull DatumType keyType = type.keyType();
    final @NotNull Type valueType = type.valueType().type();
    final MapDatum.@NotNull Builder datum = type.createBuilder();
    final @Nullable Set<Datum> expectedKeys = getExpectedKeys(projections);
    final List<? extends MP> keyProjections = getKeyProjections(projections);
    final List<VP> itemProjections = projections.stream().map(MMP::itemsProjection).collect(Collectors.toList());

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      if (token == JsonToken.START_OBJECT) {
        stepOver(JsonFormat.MAP_ENTRY_KEY_FIELD);
        final @Nullable Datum keyValue = keyProjections == null ? readDatum(keyType) : readDatum(keyProjections);
        if (keyValue == null) throw error("Null map keys are not allowed");
        if (expectedKeys != null && !expectedKeys.contains(keyValue))
          throw error("Key was not requested: '" + keyValue + "'"); // todo pretty print
        //noinspection SuspiciousMethodCalls
        if (datum._raw().elements().containsKey(keyValue))
          throw error("Key specified twice: '" + keyValue + "'"); // todo pretty print

        stepOver(JsonFormat.MAP_ENTRY_VALUE_FIELD);

        final @NotNull Data value = readData(valueType, itemProjections); // FIXME comment why this is not null?
        datum._raw().elements().put(keyValue.toImmutable(), value);
        stepOver(JsonToken.END_OBJECT);
      } else throw expected("'{' or ']");
    }

    return datum;
  }

  protected @Nullable List<? extends MP> getKeyProjections(@NotNull Collection<MMP> projections) { return null; }

  private @NotNull ListDatum.Builder finishReadingList(
      @NotNull ListType type,
      @NotNull Collection<LMP> projections // non-empty
  ) throws IOException, JsonFormatException {

    final @NotNull Type elementType = type.elementType().type();

    JsonToken token = currentToken();
    ensure(token, JsonToken.START_ARRAY);

    final ListDatum.@NotNull Builder datum = type.createBuilder();
    final List<VP> itemProjections = projections.stream().map(LMP::itemsProjection).collect(Collectors.toList());

    final Collection<@NotNull Data> elements = datum._raw().elements();
    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      final @NotNull Data value = finishReadingData(elementType, itemProjections);
      elements.add(value);
    }

    return datum;
  }

  @SuppressWarnings("unchecked")
  private @NotNull PrimitiveDatum.Builder<?> finishReadingPrimitive(@NotNull PrimitiveType<?> type)
      throws IOException, JsonFormatException {

    JsonToken token = currentToken();
    final Object nativeValue;

    if (type instanceof StringType) { // TODO introduce PrimitiveType.primitiveKind(): PrimitiveType.Kind; use switch
      ensure(token, JsonToken.VALUE_STRING);
      nativeValue = currentValueAsString();
    } else if (type instanceof BooleanType) {
      ensure(token, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE);
      nativeValue = currentValueAsBoolean();
    } else if (type instanceof DoubleType) {
      ensure(token, JsonToken.VALUE_NUMBER_FLOAT); // FIXME VALUE_NUMBER_INT is ok here, too (add test)
      nativeValue = currentValueAsDouble();
    } else if (type instanceof LongType) {
      ensure(token, JsonToken.VALUE_NUMBER_INT);
      nativeValue = currentValueAsLong();
    } else if (type instanceof IntegerType) {
      ensure(token, JsonToken.VALUE_NUMBER_INT);
      nativeValue = currentValueAsInt();
    } else throw error("Unknown primitive type: '" + type.name() + "' (" + type.getClass().getName() + ")");

    //noinspection rawtypes
    return ((PrimitiveType) type).createBuilder(nativeValue);
  }

  private @NotNull Type readType(
      @NotNull Collection<VP> projections // polymorphic tails respected
  ) throws IOException, JsonFormatException {
    stepOver(JsonFormat.POLYMORPHIC_TYPE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String typeName = currentText();
    Type type = resolveType(projections, typeName);
    if (type == null)
      throw error("Invalid type '" + typeName + "'");
    return type;
  }

  @Contract("null, _ -> null")
  private @Nullable Type resolveType(
      @Nullable Collection<VP> projections, // polymorphic tails respected
      @NotNull String typeName
  ) {
    if (projections == null) return null;
    for (VP vp : projections) {
      Type type = (Type) vp.type();
      if (typeName.equals(type.name().toString())) return type;
      type = resolveType(vp.polymorphicTails(), typeName); // dfs
      if (type != null) return type;
    }
    return null;
  }

  private @NotNull DatumType readModelType(
      @NotNull Collection<MP> projections // polymorphic tails respected
  ) throws IOException, JsonFormatException {
    stepOver(JsonFormat.POLYMORPHIC_TYPE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String typeName = currentText();
    DatumType type = resolveModelType(projections, typeName);
    if (type == null)
      throw error("Invalid type '" + typeName + "'");
    return type;
  }

  @SuppressWarnings("unchecked")
  @Contract("null, _ -> null")
  private @Nullable DatumType resolveModelType(
      @Nullable Collection<MP> projections, // polymorphic tails respected
      @NotNull String typeName
  ) {
    if (projections == null) return null;
    for (MP vp : projections) {
      DatumType type = (DatumType) vp.type();
      if (typeName.equals(type.name().toString())) return type;
      final List<MP> polymorphicTails = (List<MP>) vp.polymorphicTails();
      type = resolveModelType(polymorphicTails, typeName); // dfs
      if (type != null) return type;
    }
    return null;
  }

  @Override
  public @Nullable Data readData(@NotNull DataType valueType) throws IOException, JsonFormatException {
    if (nextToken() == null) return null;
    return finishReadingData(valueType);
  }

  private @Nullable Data finishReadingData(@NotNull DataType valueType) throws IOException, JsonFormatException {
    JsonToken token = currentToken();

    if (token == JsonToken.VALUE_NULL) return null;

    @NotNull Type type = valueType.type;
    final Data.Builder data;

    if (type.kind() == TypeKind.ENTITY) {
      ensure(token, JsonToken.START_OBJECT);

      token = nextNonEof();
      if (token == JsonToken.FIELD_NAME) {
        boolean polyData = currentName().equals(JsonFormat.POLYMORPHIC_TYPE_FIELD);

        if (polyData) {
          type = finishReadingType();
          stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD); // "data"
          stepOver(JsonToken.START_OBJECT);
          token = nextNonEof();
        }

        data = type.createDataBuilder();
        while (token == JsonToken.FIELD_NAME) {
          String tagName = currentName();
          Tag tag = type.tagsMap().get(tagName);
          if (tag == null)
            throw error("Unknown tag '" + tagName + "' in type '" + type.name().toString() + "'");

          nextNonEof();
          // current token = first value token
          Val value = finishReadingValue(tag);
          data._raw().setValue(tag, value);

          token = nextNonEof();
        }

        if (polyData) stepOver(JsonToken.END_OBJECT);
      } else
        data = type.createDataBuilder();

      ensure(token, JsonToken.END_OBJECT);
    } else {
      data = type.createDataBuilder();
      DatumType datumType = (DatumType) type;
      final @NotNull Tag selfTag = datumType.self();

      // current token = first value token
      @NotNull Val val = finishReadingValue(selfTag);
      data._raw().setValue(selfTag, val);
    }

    return data;
  }

  private @NotNull Type finishReadingType() throws IOException, JsonFormatException {
    stepOver(JsonToken.VALUE_STRING, "string value");
    String typeName = currentText();
    Type type = resolveType(typeName);
    if (type == null)
      throw error("Unknown type '" + typeName + "'");
    return type;
  }

  private @Nullable Type resolveType(@NotNull String typeName) {
    // we don't support anything but qualified type names here
    QnTypeRef typeRef = new QnTypeRef(Qn.fromDotSeparated(typeName));
    return (Type) typesResolver.resolve(typeRef);
  }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val finishReadingValue(@NotNull Tag tag) throws IOException, JsonFormatException {

    DatumType type = tag.type;
    @NotNull JsonToken token = currentToken();
    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;
    // datum
    final @NotNull Datum datum = finishReadingDatum(token, firstFieldName, type);
    return datum.asValue();
  }

  @Override
  protected @NotNull Val readValue(final @NotNull MP projection) throws IOException, FormatException {
    DatumType type = (DatumType) projection.type();
    final /*@NotNull*/ JsonToken token = nextNonEof();
    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final /*@Nullable*/ String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;

    // datum
    final /*@NotNull*/ Datum datum = finishReadingDatum(type, firstFieldName, Collections.singleton(projection));
    return datum.asValue();
  }

  protected @NotNull Datum readDatum(@NotNull List<? extends MP> projections) throws IOException, JsonFormatException {
    String firstFieldName = nextNonEof() == JsonToken.START_OBJECT ? nextFieldName() : null;
    return finishReadingDatum((DatumType) projections.get(0).type(), firstFieldName, projections);
  }

  @Override
  public @Nullable Datum readDatum(@NotNull DatumType valueType) throws IOException, JsonFormatException {
    /*@NotNull*/
    JsonToken token = nextNonEof();
    /*@Nullable*/
    String firstFieldName = token == JsonToken.START_OBJECT ? nextFieldName() : null;
    return finishReadingDatum(token, firstFieldName, valueType);
  }

  @Override
  public @NotNull Val readValue(@NotNull DatumType type) throws IOException, JsonFormatException {
    final /*@NotNull*/ JsonToken token = nextNonEof();
    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final /*@Nullable*/ String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;

    // datum
    final /*@NotNull*/ Datum datum = finishReadingDatum(token, firstFieldName, type);
    return datum.asValue();
  }

  private @NotNull Datum finishReadingDatum(
      final @NotNull JsonToken token,
      final @Nullable String firstFieldName,
      final @NotNull DatumType type) throws IOException, JsonFormatException {

    JsonToken actualToken = token;
    String actualFirstFieldName = firstFieldName;
    DatumType actualType = type;

    boolean polyData = JsonFormat.POLYMORPHIC_TYPE_FIELD.equals(firstFieldName);

    if (polyData) {
      TypeApi t = finishReadingType();
      if (t instanceof DatumType)
        actualType = (DatumType) t;
      else
        throw error("Expected to get a Datum type, got '" + t.name().toString() + "' of kind " + t.kind().toString());

      stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD); // "data"

      actualToken = nextNonEof();
      if (actualToken == JsonToken.START_OBJECT)
        actualFirstFieldName = nextFieldName();
      else
        actualFirstFieldName = null;
    }

    final @NotNull Datum datum;
    switch (actualType.kind()) {
      case RECORD:
        datum = finishReadingRecord(
            actualToken,
            actualFirstFieldName,
            (RecordType) actualType
        );
        break;
      case MAP:
        if (actualFirstFieldName != null) throw expected("'['");
        datum = finishReadingMap(
            actualToken,
            (MapType) actualType
        );
        break;
      case LIST:
        if (actualFirstFieldName != null) throw expected("'['");
        datum = finishReadingList(
            actualToken,
            (ListType) actualType
        );
        break;
      case PRIMITIVE:
        if (actualFirstFieldName != null) throw expected("primitive value");
        datum = finishReadingPrimitive(
            (PrimitiveType<?>) actualType
        );
        break;
//    case ENUM: // TODO once enums are supported
//      datum = readEnum(
//        token,
//        (EnumType) type,
//      );
//      break;
      case ENTITY: // this one is 500 - there should be no such model projections
      default:
        throw new UnsupportedOperationException(actualType.kind().name());
    }

    if (polyData) // was a typeful data
      stepOver(JsonToken.END_OBJECT);

    return datum;
  }

  // `}` or `: DATA, "field": DATA, ... }`
  private @NotNull RecordDatum finishReadingRecord(
      @NotNull JsonToken token,
      @Nullable String fieldName,
      @NotNull RecordType type
  ) throws IOException, JsonFormatException {

    ensure(token, JsonToken.START_OBJECT);
    RecordDatum.Builder datum = type.createBuilder();
    if (fieldName == null) { // empty record?
      ensureCurr(JsonToken.END_OBJECT, "field name or '}'");
      return datum;
    } else {
      while (true) {
        Field field = type.fieldsMap().get(fieldName);
        if (field == null) throw error(
            "Unknown field '" + fieldName + "' in record type '" + type.name().toString() + "'"
        );
        Data fieldData = readData(field.dataType());
        datum._raw().setData(field, fieldData);

        token = nextNonEof();
        if (token == JsonToken.END_OBJECT) break;
        if (token == JsonToken.FIELD_NAME) fieldName = currentName();
        else throw expected("field name or '}'");
      }
    }
    return datum;
  }

  // `]` or ` MAP_ENTRY , MAP ENTRY ... ]`
  // MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
  private @NotNull MapDatum finishReadingMap(@NotNull JsonToken token, @NotNull MapType type)
      throws IOException, JsonFormatException {

    ensure(token, JsonToken.START_ARRAY);

    final @NotNull DatumType keyType = type.keyType();
    final MapDatum.@NotNull Builder datum = type.createBuilder();

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      if (token == JsonToken.START_OBJECT) {
        stepOver(JsonFormat.MAP_ENTRY_KEY_FIELD);
        final @Nullable Datum keyValue = readDatum(keyType);
        if (keyValue == null) throw error("Null map keys are not allowed");
        //noinspection SuspiciousMethodCalls
        if (datum._raw().elements().containsKey(keyValue))
          throw error("Key specified twice: '" + keyValue + "'"); // todo pretty print

        stepOver(JsonFormat.MAP_ENTRY_VALUE_FIELD);

        final @Nullable Data value = readData(type.valueType());
        if (value == null)
          throw error("Null map values are not allowed"); // print keys for diagnostics? or try to construct null datum?
        datum._raw().elements().put(keyValue.toImmutable(), value);
      } else throw expected("'{' or ']");
    }

    return datum;
  }

  private @NotNull ListDatum finishReadingList(@NotNull JsonToken token, @NotNull ListType type)
      throws IOException, JsonFormatException {

    ensure(token, JsonToken.START_ARRAY);

    final ListDatum.@NotNull Builder datum = type.createBuilder();

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      final @Nullable Data value = finishReadingData(type.elementType());
      if (value == null) throw error("Null list values are not allowed"); // or try to construct null datum?
      datum._raw().elements().add(value);
    }

    return datum;
  }

  @Override
  public @NotNull ErrorValue readError() throws IOException, JsonFormatException {
    stepOver(JsonToken.START_OBJECT);
    return finishReadingError();
  }

  private @NotNull JsonToken nextNonEof() throws JsonFormatException {return checkEof(nextToken());}

  @Contract("null -> fail")
  private @NotNull JsonToken checkEof(@Nullable JsonToken token) throws IllegalArgumentException {
    if (token == null) throw new IllegalArgumentException("Unexpected EOF at " + currentLocation());
    return token;
  }

  private void ensure(@Nullable JsonToken actual, @NotNull JsonToken... expected)
      throws IOException, JsonFormatException {
    Callable<String> expectedStringCallable =
        () -> Arrays.stream(expected).map(token -> "'" + token.toString() + "'").collect(Collectors.joining(", "));
    ensure(actual, null, expectedStringCallable, expected);
  }

  private void ensureEOF() throws IOException, JsonFormatException {
    if (nextToken() != null)
      throw expected("EOF");
  }

  private void ensure(
      @Nullable JsonToken actual,
      @Nullable String expectedText,
      @Nullable Callable<String> expectedTextCallable,
      @NotNull JsonToken... expected)
      throws IOException, IllegalArgumentException, JsonFormatException {

    for (JsonToken e : expected)
      if (e == actual)
        return;

    if (expectedText == null) {
      assert expectedTextCallable != null;
      try {
        expectedText = expectedTextCallable.call();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    throw expected(expectedText);
  }

  private void ensureCurr(@Nullable JsonToken expected, @NotNull String expectedText)
      throws IOException, JsonFormatException {
    ensure(currentToken(), expectedText, null, expected == null ? NO_JSON_TOKENS : new JsonToken[]{expected});
  }


  private void stepOver(@NotNull JsonToken expected)
      throws IOException, JsonFormatException { // FIXME use `stepOver(JsonToken, String)`
    stepOver(expected, "'" + expected.toString() + "'");
  }

  private void stepOver(@NotNull JsonToken expected, @NotNull String expectedText)
      throws IOException, JsonFormatException {
    ensure(nextToken(), expectedText, null, expected);
  }

  private void stepOver(@NotNull String fieldName) throws IOException, JsonFormatException {
    if (!fieldName.equals(nextFieldName())) throw expected('"' + fieldName + "\" field");
  }

  private JsonFormatException expected(@NotNull String expected) throws IOException {
    return error("Expected " + expected + " but got " + str(currentText()));
  }

  @Override
  protected JsonFormatException error(@NotNull String message) {
    final JsonLocation location = currentLocation();
    return new JsonFormatException(
        String.format(
            "%s at line %s column %s",
            message,
            location.getLineNr(),
            location.getColumnNr()
        )
    );
  }

  @Contract(pure = true)
  private static @NotNull String str(@Nullable String text) { return text == null ? "EOF" : '\'' + text + '\''; }

  // parser interaction


  private JsonToken currentToken() {
    return replaying ? replay.peek().token : in.currentToken();
  }

  private String currentName() throws IOException {
    return replaying ? replay.peek().name : in.getCurrentName();
  }

  private String currentText() throws IOException {
    return replaying ? replay.peek().text : in.getText();
  }

  private int currentValueAsInt() throws IOException {
    return replaying ? replay.peek().intValue : in.getValueAsInt();
  }

  private long currentValueAsLong() throws IOException {
    return replaying ? replay.peek().longValue : in.getValueAsLong();
  }

  private double currentValueAsDouble() throws IOException {
    return replaying ? replay.peek().doubleValue : in.getValueAsDouble();
  }

  private boolean currentValueAsBoolean() throws IOException {
    return replaying ? replay.peek().booleanValue : in.getValueAsBoolean();
  }

  private String currentValueAsString() throws IOException {
    return replaying ? replay.peek().stringValue : in.getValueAsString();
  }

  private JsonLocation currentLocation() {
    return replaying ? replay.peek().location : in.getCurrentLocation();
  }

  // next

  private @Nullable JsonToken nextToken() throws JsonFormatException {
    try {
      if (replaying) {
        switch (replay.size()) {
          case 0:
            resetParserStateRecording();
            return in.nextToken();
          case 1:
            resetParserStateRecording();
            return in.currentToken();
          default:
            replay.pop();
            return currentToken();
        }
      } else if (replay != null) {
        replay.add(currentParserState());
        in.nextToken();
        return currentToken();
      } else
        return in.nextToken();
    } catch (IOException e) {
      throw new JsonFormatException(e.getMessage());
    }
  }

  private @Nullable String nextFieldName() throws IOException, JsonFormatException {
    nextToken();
    return currentToken() == JsonToken.FIELD_NAME ? currentName() : null;
  }

  // parser state management

  private Deque<JsonState> replay = null;
  private boolean replaying = false;

  private void startParserStateRecording() {
    resetParserStateRecording();
    replay = new ArrayDeque<>();
//    replay.push(currentParserState());
//    in.nextToken();
  }

  private void replayParserState() {
    assert replay != null;
    assert !replaying;
    replaying = true;
  }

  private void resetParserStateRecording() {
    replay = null;
    replaying = false;
  }

  private @NotNull JsonState currentParserState() throws IOException {
    return new JsonState(
        in.currentToken(),
        in.getText(),
        in.getCurrentName(),
        in.getValueAsInt(),
        in.getValueAsString(),
        in.getValueAsLong(),
        in.getValueAsDouble(),
        in.getValueAsBoolean(),
        in.getCurrentLocation()
    );
  }

  private static final class JsonState {
    final JsonToken token;
    final String text;
    final String name;

    final int intValue;
    final String stringValue;
    final long longValue;
    final double doubleValue;
    final boolean booleanValue;

    final JsonLocation location;

    JsonState(
        final JsonToken token,
        final String text,
        final String name,
        final int intValue,
        final String stringValue,
        final long longValue,
        final double doubleValue,
        final boolean booleanValue,
        final JsonLocation location) {

      this.token = token;
      this.text = text;
      this.name = name;
      this.intValue = intValue;
      this.stringValue = stringValue;
      this.longValue = longValue;
      this.doubleValue = doubleValue;
      this.booleanValue = booleanValue;
      this.location = location;
    }
  }

  private final class VisitedDataEntry {
    final Data.Builder builder;
    final Collection<VP> projections;

    private VisitedDataEntry(final Data.Builder builder, final Collection<VP> projections) {
      this.builder = builder;
      this.projections = projections;
    }

    boolean matches(Collection<VP> projections) {
      return projectionsComparator().projectionsEquals(projections, this.projections);
    }
  }

}
