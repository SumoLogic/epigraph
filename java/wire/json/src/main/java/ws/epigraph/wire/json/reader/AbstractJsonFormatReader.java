/*
 * Copyright 2016 Sumo Logic
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.*;
import ws.epigraph.types.Type.Tag;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.json.JsonFormat;
import ws.epigraph.wire.json.JsonFormatCommon;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ws.epigraph.types.RecordType.Field;
import static ws.epigraph.wire.json.JsonFormatCommon.*;


/**
 * Abstract projection-driven JSON data reader
 * <p/>
 * Format grammar (some quotes omitted):<p/>
 * <p>
 * <code> <pre>
 * DATA ::= POLYDATA | MONODATA                                        // triggered by projection (polymorphic tails presence)
 * POLYDATA ::= '{' "type" ':' TYPE, "data" ':' MONODATA '}'
 * TYPE ::= '"' ( STRING '.' )* STRING '"'                             // enquoted dot-separated type FQN string
 * MONODATA ::= MULTIDATA | VALUE                                      // triggered by projection (parenthesized flag)
 * MULTIDATA ::= '{' (( "tag" ':' VALUE ',' )* "tag" ':' VALUE )? '}'  // 0 or more comma-separated entries
 * VALUE ::= ERROR | DATUM | 'null'
 * ERROR ::= '{' "ERROR": INTEGER ',' "message": STRING '}'
 * DATUM ::= DATUM_WITH_META | DATUM_NO_META                           // depending on meta-projection presence
 * DATUM_WITH_META ::= '{' "meta" ':' DATUM_NO_META ',' "data" ':' DATUM_NO_META '}'
 * DATUM_NO_META ::= RECORD | MAP | LIST | PRIMITIVE | ENUM
 * RECORD ::= { (( "field" ':' DATA ',' )* "field ':' DATA )* '}'      // 0 or more comma-separated entries
 * MAP ::= '[' (( MAP_ENTRY ',' )* MAP_ENTRY )? ]                      // 0 or more comma-separated entries
 * MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
 * LIST ::= '[' (( DATA ',' )* DATA )? ']'                             // 0 or more comma-separated entries
 * PRIMITIVE ::= STRING | INTEGER | LONG | DOUBLE | BOOLEAN
 * ENUM ::= STRING // ? TODO
 * </pre></code>
 *
 * @author Yegor Borovikov
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class AbstractJsonFormatReader<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?, ?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>
    //,PMP extends GenPrimitiveModelProjection<PMP, ?>
    >
    implements FormatReader<VP, MP, IOException> {

  private final @NotNull JsonParser in;

  protected AbstractJsonFormatReader(@NotNull JsonParser jsonParser) { this.in = jsonParser; }

  @Override
  public @Nullable Data readData(@NotNull VP projection) throws IOException {
    Data data = readData(Collections.singletonList(projection));
    stepOver(null, "EOF");
    return data;
  }

  // DATA ::= POLYDATA or MONODATA
  private @Nullable Data readData(
      @NotNull List<? extends VP> projections // non-empty, polymorphic tails respected
  ) throws IOException {

    if (in.nextToken() == null) return null;
    return finishReadingData(projections);
  }

  private @NotNull Data finishReadingData(
      @NotNull List<? extends VP> projections // non-empty, polymorphic tails respected
  ) throws IOException {
    assert !projections.isEmpty();

    JsonToken token = in.currentToken();
    boolean readPoly = isPolymorphic(projections); // at least one projection has poly tail

    final Type type;
    if (readPoly) { // { "type": "list[epigraph.String]", "data": MONODATA }
      ensure(token, JsonToken.START_OBJECT);
      type = readType(projections);
      stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD); // "data"
      nextNonEof(); // position parser on first MONODATA token
    } else {
      type = (Type) projections.get(projections.size() - 1).type(); // effectiveType; // mostSpecificType(projections);
      // current token is first MONODATA token
    }

    // read MONODATA
    List<? extends VP> flattened = flatten(new ArrayList<>(), projections, type);

    String monoTagName = type.kind() == TypeKind.UNION ? JsonFormatCommon.monoTag(flattened) : DatumType.MONO_TAG_NAME;
    final Data data;
    if (monoTagName == null) { // MULTIDATA ::= { "tag": VALUE, ... }
      data = finishReadingMultiData(type, flattened);
    } else { // VALUE ::= ERROR or DATUM or null
      Tag tag = type.tagsMap().get(monoTagName);
      assert tag != null : "invalid tag";
      Collection<? extends MP> tagModelProjections =
          tagModelProjections(tag, flattened, () -> new ArrayList<>(projections.size()));
      assert tagModelProjections != null : "missing mono tag";
      final Data.Builder builder = type.createDataBuilder();
      builder._raw().setValue(tag, finishReadingValue(tag, tagModelProjections));
      data = builder;
    }

    if (readPoly) stepOver(JsonToken.END_OBJECT); // TODO verify it's not already consumed (by invoked code)
    return data;
  }

  // MULTIDATA ::= { "tag": VALUE, ... }
  private @NotNull Data finishReadingMultiData(
      @NotNull Type effectiveType,
      @NotNull List<? extends VP> projections // non-empty, polymorphic tails ignored
  ) throws IOException {

    assert !projections.isEmpty();

    JsonToken token = in.currentToken();
    Data.Builder data = effectiveType.createDataBuilder();
    ensure(token, JsonToken.START_OBJECT);

    while ((token = in.nextToken()) == JsonToken.FIELD_NAME) {
      String tagName = in.getCurrentName();
      Tag tag = effectiveType.tagsMap().get(tagName);
      if (tag == null)
        throw error("Unknown tag '" + tagName + "' in type '" + effectiveType.name().toString() + "'");

      Collection<? extends MP> tagModelProjections =
          tagModelProjections(tag, projections, () -> new ArrayList<>(projections.size()));
      if (tagModelProjections == null) { // the tag was not requested in projection
        throw error("Unexpected tag '" + tagName + "'");
      } else {
        Val value = readValue(tag, tagModelProjections);
        data._raw().setValue(tag, value);
      }
    }
    ensure(token, JsonToken.END_OBJECT);

    for (final VP projection : projections) {
      for (final TP tagProjectionEntry : projection.tagProjections().values()) {
        if (tagRequired(tagProjectionEntry) && data._raw().getValue((Type.Tag) tagProjectionEntry.tag()) == null)
          throw error("Missing data for required tag '" + tagProjectionEntry.tag().name() + "'");
      }
    }

    return data;
  }

  protected boolean tagRequired(@NotNull TP tagProjection) { return false; }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val readValue(
      @NotNull Tag tag,
      @NotNull Collection<? extends MP> tagModelProjections // non-empty
  ) throws IOException {
    nextNonEof(); // read first token
    return finishReadingValue(tag, tagModelProjections);
  }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val finishReadingValue(
      @NotNull Tag tag,
      @NotNull Collection<? extends MP> tagModelProjections // non-empty
  ) throws IOException {

    JsonToken token = in.currentToken();
    assert !tagModelProjections.isEmpty();

    DatumType type = tag.type;
    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = in.nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;
    // datum
    final @NotNull Datum datum = finishReadingDatum(firstFieldName, tagModelProjections, type);
    return datum.asValue();
  }

  protected @Nullable MP getMetaProjection(@NotNull MP projection) { return null; }

  // DATUM ::= DATUM_WITH_META | DATUM_NO_META                           // depending on meta-projection presence
  // DATUM_WITH_META ::= '{' "meta" ':' DATUM_NO_META ',' "data" ':' DATUM_NO_META '}'
  @SuppressWarnings("unchecked")
  private @Nullable Datum finishReadingDatum(
      @Nullable String fieldName,
      final @NotNull Collection<? extends MP> modelProjections,
      final DatumType type) throws IOException {

    Collection<? extends MP> metaProjections = modelProjections.stream()
        .map(this::getMetaProjection)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayList::new));

    final Datum datum;

    if (metaProjections.isEmpty()) {
      datum = finishReadingDatumNoMeta(fieldName, modelProjections, type);
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
          if (token == JsonToken.FIELD_NAME) fieldName = in.getCurrentName();
          else throw expected("field name or '}'");
        } else if (Objects.equals(fieldName, JsonFormat.DATUM_VALUE_FIELD)) {
          if (_datum != null) throw error("Field '" + JsonFormat.DATUM_VALUE_FIELD + "' must only be specified once");
          _datum = readDatumNoMeta(modelProjections, type);
          @NotNull JsonToken token = nextNonEof();
          if (token == JsonToken.END_OBJECT) break;
          if (token == JsonToken.FIELD_NAME) fieldName = in.getCurrentName();
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

  private @Nullable Datum.Builder readDatumNoMeta(
      @NotNull Collection<? extends MP> modelProjections,
      @NotNull DatumType type) throws IOException {

    @NotNull JsonToken token = nextNonEof();
    @Nullable String firstFieldName = token == JsonToken.START_OBJECT ? in.nextFieldName() : null;
    return finishReadingDatumNoMeta(firstFieldName, modelProjections, type);
  }

  @SuppressWarnings("unchecked")
  private @NotNull Datum.Builder finishReadingDatumNoMeta(
      final @Nullable String firstFieldName,
      final @NotNull Collection<? extends MP> tagModelProjections,
      final DatumType type) throws IOException {

    final @NotNull Datum.Builder datum;
    switch (type.kind()) {
      case RECORD:
        datum = finishReadingRecord(
            firstFieldName,
            (RecordType) type,
            (Collection<? extends RMP>) tagModelProjections
        );
        break;
      case MAP:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingMap(
            (MapType) type,
            (Collection<? extends MMP>) tagModelProjections
        );
        break;
      case LIST:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingList(
            (ListType) type,
            (Collection<? extends LMP>) tagModelProjections
        );
        break;
      case PRIMITIVE:
        if (firstFieldName != null) throw expected("primitive value");
        datum = finishReadingPrimitive(
            (PrimitiveType<?>) type
            // (Collection<? extends PMP>) tagModelProjections
        );
        break;
//    case ENUM: // TODO once enums are supported
//      datum = readEnum(
//        token,
//        (EnumType) type,
//        (Collection<? extends ReqOutputEnumModelProjection>) tagModelProjections
//      );
//      break;
      case UNION: // this one is 500 - there should be no such model projections
      default:
        throw new UnsupportedOperationException(type.kind().name());
    }
    return datum;
  }

  private @NotNull ErrorValue finishReadingError() throws IOException { // `: 404, "message": "blah" }`
    stepOver(JsonToken.VALUE_NUMBER_INT, "integer value");
    int errorCode;
    try { errorCode = in.getIntValue(); } catch (JsonParseException ignored) { throw expected("integer error code"); }
    stepOver(JsonFormat.ERROR_MESSAGE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String message = in.getText();
    // TODO read custom error properties here (if we decide to support these)
    stepOver(JsonToken.END_OBJECT);
    return new ErrorValue(errorCode, message, null);
  }

  // `}` or `: DATA, "field": DATA, ... }`
  private @NotNull RecordDatum.Builder finishReadingRecord(
      @Nullable String fieldName,
      @NotNull RecordType type,
      @NotNull Collection<? extends RMP> projections // non-empty
  ) throws IOException {

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
        List<? extends VP> varProjections =
            fieldVarProjections(projections, field, () -> new ArrayList<>(projections.size()));
        if (varProjections == null) throw error("Unexpected field '" + fieldName + "'");
        Data fieldData = readData(varProjections);
        datum._raw().setData(field, fieldData);

        JsonToken token = nextNonEof();
        if (token == JsonToken.END_OBJECT) break;
        if (token == JsonToken.FIELD_NAME) fieldName = in.getCurrentName();
        else throw expected("field name or '}'");
      }
    }

    for (RMP projection : projections)
      for (final Map.Entry<String, FPE> entry : projection.fieldProjections().entrySet())
        if (fieldRequired(entry.getValue()) && datum._raw().getData((Field) entry.getValue().field()) == null)
          throw error("Required field '" + entry.getKey() + "' is missing");

    return datum;
  }

  protected boolean fieldRequired(@NotNull FPE fieldEntry) { return false; }

  // `]` or ` MAP_ENTRY , MAP ENTRY ... ]`
  // MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
  private @NotNull MapDatum.Builder finishReadingMap(
      @NotNull MapType type,
      @NotNull Collection<? extends MMP> projections // non-empty
  ) throws IOException {

    JsonToken token = in.currentToken();
    ensure(token, JsonToken.START_ARRAY);

    final @NotNull DatumType keyType = type.keyType();
    final MapDatum.@NotNull Builder datum = type.createBuilder();
    final @Nullable Set<Datum> expectedKeys = getExpectedKeys(projections);
    final List<VP> itemProjections = projections.stream().map(MMP::itemsProjection).collect(Collectors.toList());

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      if (token == JsonToken.START_OBJECT) {
        stepOver(JsonFormat.MAP_ENTRY_KEY_FIELD);
        final @Nullable Datum keyValue = readDatum(keyType);
        if (keyValue == null) throw error("Null map keys are not allowed");
        if (expectedKeys != null && !expectedKeys.contains(keyValue))
          throw error("Key was not requested: '" + keyValue + "'"); // todo pretty print
        //noinspection SuspiciousMethodCalls
        if (datum._raw().elements().containsKey(keyValue))
          throw error("Key specified twice: '" + keyValue + "'"); // todo pretty print

        stepOver(JsonFormat.MAP_ENTRY_VALUE_FIELD);

        final @NotNull Data value = readData(itemProjections); // FIXME comment why this is not null?
        datum._raw().elements().put(keyValue.toImmutable(), value);
        stepOver(JsonToken.END_OBJECT);
      } else throw expected("'{' or ']");
    }

    return datum;
  }

  protected abstract @Nullable Set<Datum> getExpectedKeys(@NotNull Collection<? extends MMP> projections);

  private @NotNull ListDatum.Builder finishReadingList(
      @NotNull ListType type,
      @NotNull Collection<? extends LMP> projections // non-empty
  ) throws IOException {

    JsonToken token = in.currentToken();
    ensure(token, JsonToken.START_ARRAY);

    final ListDatum.@NotNull Builder datum = type.createBuilder();
    final List<VP> itemProjections = projections.stream().map(LMP::itemsProjection).collect(Collectors.toList());

    final Collection<@NotNull Data> elements = datum._raw().elements();
    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      final @NotNull Data value = finishReadingData(itemProjections);
      elements.add(value);
    }

    return datum;
  }

  @SuppressWarnings("unchecked")
  private @NotNull PrimitiveDatum.Builder<?> finishReadingPrimitive(@NotNull PrimitiveType<?> type) throws IOException {

    JsonToken token = in.currentToken();
    final Object nativeValue;

    if (type instanceof StringType) { // TODO introduce PrimitiveType.primitiveKind(): PrimitiveType.Kind; use switch
      ensure(token, JsonToken.VALUE_STRING);
      nativeValue = in.getValueAsString();
    } else if (type instanceof BooleanType) {
      ensure(token, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE);
      nativeValue = in.getValueAsBoolean();
    } else if (type instanceof DoubleType) {
      ensure(token, JsonToken.VALUE_NUMBER_FLOAT); // FIXME VALUE_NUMBER_INT is ok here, too (add test)
      nativeValue = in.getValueAsDouble();
    } else if (type instanceof LongType) {
      ensure(token, JsonToken.VALUE_NUMBER_INT);
      nativeValue = in.getValueAsLong();
    } else if (type instanceof IntegerType) {
      ensure(token, JsonToken.VALUE_NUMBER_INT);
      nativeValue = in.getValueAsInt();
    } else throw error("Unknown primitive type: '" + type.name() + "' (" + type.getClass().getName() + ")");

    //noinspection rawtypes
    return ((PrimitiveType) type).createBuilder(nativeValue);
  }

  private @NotNull Type readType(
      @NotNull Collection<? extends VP> projections // polymorphic tails respected
  ) throws IOException {
    stepOver(JsonFormat.POLYMORPHIC_TYPE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String typeName = in.getText();
    Type type = resolveType(projections, typeName);
    if (type == null)
      throw error("Invalid type '" + typeName + "'");
    return type;
  }

  @Contract("null, _ -> null")
  private @Nullable Type resolveType(
      @Nullable Collection<? extends VP> projections, // polymorphic tails respected
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

  @Override
  public @Nullable Data readData(@NotNull DataType dataType) throws IOException {
    JsonToken token = nextNonEof();

    if (token == JsonToken.VALUE_NULL) return null;

    final @NotNull Type type = dataType.type;
    final Data.@NotNull Builder data = type.createDataBuilder();

    if (type.kind() == TypeKind.UNION) {
      ensure(token, JsonToken.START_OBJECT);

      while ((token = in.nextToken()) == JsonToken.FIELD_NAME) {
        String tagName = in.getCurrentName();
        Tag tag = type.tagsMap().get(tagName);
        if (tag == null)
          throw error("Unknown tag '" + tagName + "' in type '" + type.name().toString() + "'");

        Val value = finishReadingValue(tag);
        data._raw().setValue(tag, value);
      }
      ensure(token, JsonToken.END_OBJECT);
    } else {
      DatumType datumType = (DatumType) type;
      final @NotNull Tag selfTag = datumType.self;

      @NotNull Val val = finishReadingValue(selfTag);
      data._raw().setValue(selfTag, val);
    }

    return data;
  }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val finishReadingValue(@NotNull Tag tag) throws IOException {

    DatumType type = tag.type;
    @NotNull JsonToken token = nextNonEof();
    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = in.nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;
    // datum
    final @NotNull Datum datum = finishReadingDatum(token, firstFieldName, type);
    return datum.asValue();
  }

  @Override
  public @Nullable Datum readDatum(@NotNull MP projection) throws IOException {
    String firstFieldName = nextNonEof() == JsonToken.START_OBJECT ? in.nextFieldName() : null;
    return finishReadingDatum(firstFieldName, Collections.singleton(projection), (DatumType) projection.model());
  }

  @Override
  public @Nullable Datum readDatum(@NotNull DatumType type) throws IOException {
    @NotNull JsonToken token = nextNonEof();
    @Nullable String firstFieldName = token == JsonToken.START_OBJECT ? in.nextFieldName() : null;
    return finishReadingDatum(token, firstFieldName, type);
  }

  @Override
  public @NotNull Val readValue(@NotNull DatumType type) throws IOException {
    final @NotNull JsonToken token = nextNonEof();
    // null?
    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
    // error?
    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT) { // can be a record or an error
      firstFieldName = in.nextFieldName(); // advances to next token (field name or end object - in valid cases)
      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
    } else firstFieldName = null;

    // datum
    final @NotNull Datum datum = finishReadingDatum(token, firstFieldName, type);
    return datum.asValue();
  }

  private @NotNull Datum finishReadingDatum(
      final JsonToken token,
      final String firstFieldName,
      final @NotNull DatumType type) throws IOException {

    final @NotNull Datum datum;
    switch (type.kind()) {
      case RECORD:
        datum = finishReadingRecord(
            token,
            firstFieldName,
            (RecordType) type
        );
        break;
      case MAP:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingMap(
            token,
            (MapType) type
        );
        break;
      case LIST:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingList(
            token,
            (ListType) type
        );
        break;
      case PRIMITIVE:
        if (firstFieldName != null) throw expected("primitive value");
        datum = finishReadingPrimitive(
            (PrimitiveType<?>) type
        );
        break;
//    case ENUM: // TODO once enums are supported
//      datum = readEnum(
//        token,
//        (EnumType) type,
//      );
//      break;
      case UNION: // this one is 500 - there should be no such model projections
      default:
        throw new UnsupportedOperationException(type.kind().name());
    }
    return datum;
  }

  // `}` or `: DATA, "field": DATA, ... }`
  private @NotNull RecordDatum finishReadingRecord(
      @NotNull JsonToken token,
      @Nullable String fieldName,
      @NotNull RecordType type
  ) throws IOException {

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
        if (token == JsonToken.FIELD_NAME) fieldName = in.getCurrentName();
        else throw expected("field name or '}'");
      }
    }
    return datum;
  }

  // `]` or ` MAP_ENTRY , MAP ENTRY ... ]`
  // MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
  private @NotNull MapDatum finishReadingMap(@NotNull JsonToken token, @NotNull MapType type) throws IOException {

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

  private @NotNull ListDatum finishReadingList(@NotNull JsonToken token, @NotNull ListType type) throws IOException {

    ensure(token, JsonToken.START_ARRAY);

    final ListDatum.@NotNull Builder datum = type.createBuilder();

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      final @Nullable Data value = readData(type.elementType());
      if (value == null) throw error("Null list values are not allowed"); // or try to construct null datum?
      datum._raw().elements().add(value);
    }

    return datum;
  }

  @Override
  public @NotNull ErrorValue readError() throws IOException {
    stepOver(JsonToken.START_OBJECT);
    return finishReadingError();
  }

  /**
   * Returns `true` iff at least one of projections has polymorphic tails.
   */
  private boolean isPolymorphic(Collection<? extends VP> projections) {
    return projections.stream().anyMatch(vp -> vp.polymorphicTails() != null);
  }

  private @NotNull JsonToken nextNonEof() throws IOException {return checkEof(in.nextToken());}

  @Contract("null -> fail")
  private @NotNull JsonToken checkEof(@Nullable JsonToken token) throws IllegalArgumentException {
    if (token == null) throw new IllegalArgumentException("Unexpected EOF at " + in.getTokenLocation());
    return token;
  }

  private void ensure(@Nullable JsonToken actual, @NotNull JsonToken... expected) throws IOException {
    // FIXME premature error message construction + .toString() is bad here (enum constant names)
    // TODO remove this method and pass hand-written expectedText to `ensure(JsonToken, String, JsonToken...)`
    String expectedString =
        Arrays.stream(expected).map(token -> "'" + token.toString() + "'").collect(Collectors.joining(", "));
    ensure(actual, expectedString, expected);
  }

  private void ensure(
      @Nullable JsonToken actual,
      @NotNull String expectedText,
      @NotNull JsonToken... expected)
      throws IOException, IllegalArgumentException {

    for (JsonToken e : expected) if (e == actual) return;
    throw expected(expectedText);
  }

  private void ensureCurr(@Nullable JsonToken expected, @NotNull String expectedText) throws IOException {
    ensure(in.currentToken(), expectedText, expected);
  }

  private void stepOver(@NotNull JsonToken expected) throws IOException { // FIXME use `stepOver(JsonToken, String)`
    stepOver(expected, "'" + expected.toString() + "'");
  }

  private void stepOver(@Nullable JsonToken expected, @NotNull String expectedText) throws IOException {
    ensure(in.nextToken(), expectedText, expected);
  }

  private void stepOver(@NotNull String fieldName) throws IOException {
    if (!fieldName.equals(in.nextFieldName())) throw expected('"' + fieldName + "\" field");
  }

  private IllegalArgumentException expected(@NotNull String expected) throws IOException {
    return error("Expected " + expected + " but got " + str(in.getText()));
  }

  protected IllegalArgumentException error(@NotNull String message) {
    return new IllegalArgumentException(message + " at " + in.getCurrentLocation());
  }

  @Contract(pure = true)
  private static @NotNull String str(@Nullable String text) { return text == null ? "EOF" : '\'' + text + '\''; }
}
