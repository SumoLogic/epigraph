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

/* Created by yegor on 10/20/16. */

package ws.epigraph.wire.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.types.*;
import ws.epigraph.types.Type.Tag;
import ws.epigraph.wire.FormatReader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ws.epigraph.types.RecordType.Field;
import static ws.epigraph.wire.json.JsonFormatCommon.*;


/**
 * JSON data reader
 * <p/>
 * Format grammar (some quotes omitted):<p/>
 * <p>
 * <code> <pre>
 * DATA ::= POLYDATA | MONODATA                                          // triggered by projection (polymorphic tails presence)
 * POLYDATA ::= '{' "type" ':' TYPE, "data" ':' MONODATA '}'
 * TYPE ::= '"' ( STRING '.' )* STRING '"'                               // enquoted dot-separated type FQN string
 * MONODATA ::= MULTIDATA | VALUE                                        // triggered by projection (parenthesized flag)
 * MULTIDATA ::= '{' (( "tag" ':' VALUE ',' )* "tag" ':' VALUE )? '}'    // 0 or more comma-separated entries
 * VALUE ::= ERROR | DATUM | 'null'
 * ERROR ::= '{' "ERROR": INTEGER ',' "message": STRING '}'
 * DATUM ::= RECORD | MAP | LIST | PRIMITIVE | ENUM
 * RECORD ::= { (( "field" ':' DATA ',' )* "field ':' DATA )* '}'        // 0 or more comma-separated entries
 * MAP ::= '[' (( MAP_ENTRY ',' )* MAP_ENTRY )? ]                    // 0 or more comma-separated entries
 * MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
 * LIST ::= '[' (( DATA ',' )* DATA )? ']'                               // 0 or more comma-separated entries
 * PRIMITIVE ::= STRING | INTEGER | LONG | DOUBLE | BOOLEAN
 * ENUM ::= STRING // ? TODO
 * </pre></code>
 *
 * @author Yegor Borovikov
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class JsonFormatReader implements FormatReader<IOException> {

  private final @NotNull JsonParser in;

//  private final @NotNull Map<String, Type> cachedTypes = new HashMap<>();

  public JsonFormatReader(@NotNull JsonParser jsonParser) { this.in = jsonParser; }

  public @NotNull Data readData(@NotNull ReqOutputVarProjection projection) throws IOException {
//    Data data = readData(projection.type(), Collections.singletonList(projection));
    Data data = readData(Collections.singletonList(projection));
    stepOver(null, "EOF");
    return data;
  }

  // DATA ::= POLYDATA or MONODATA
  private @NotNull Data readData(
//      @NotNull Type effectiveType,
      @NotNull List<? extends ReqOutputVarProjection> projections // non-empty, polymorphic tails respected
  ) throws IOException {

    assert !projections.isEmpty();

    final Data data;
    JsonToken token = nextNonEof();
    boolean readPoly = isPolymorphic(projections); // at least one projection has poly tail

    final Type type;
    if (readPoly) { // { "type": "list[epigraph.String]", "data": MONODATA }
      ensure(token, JsonToken.START_OBJECT);
      type = readType(projections);
      stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD);
    } else type = projections.get(projections.size() - 1).type(); // effectiveType; // mostSpecificType(projections);

    List<? extends ReqOutputVarProjection> flattened = flatten(new ArrayList<>(), projections, type);

    String monoTagName = type.kind() == TypeKind.UNION ? monoTag(projections) : DatumType.MONO_TAG_NAME;
    if (monoTagName == null) { // MULTIDATA ::= { "tag": VALUE, ... }
      data = readMultiData(type, flatten(new ArrayList<>(), projections, type));
    } else { // VALUE ::= ERROR or DATUM or null
      Tag tag = type.tagsMap().get(monoTagName);
      assert tag != null : "invalid tag";
      Collection<? extends ReqOutputModelProjection> tagModelProjections =
          tagModelProjections(tag, flattened, () -> new ArrayList<>(projections.size()));
      assert tagModelProjections != null : "missing mono tag";
      data = type.createDataBuilder()._raw().setValue(tag, readValue(tag, tagModelProjections));
    }

    if (readPoly) stepOver(JsonToken.END_OBJECT); // TODO verify it's not already consumed (by invoked code)
    return data;
  }

  // MULTIDATA ::= { "tag": VALUE, ... }
  private @NotNull Data readMultiData(
      @NotNull Type effectiveType,
      @NotNull List<? extends ReqOutputVarProjection> projections // non-empty, polymorphic tails ignored
  ) throws IOException {

    assert !projections.isEmpty();

    Data.Builder data = effectiveType.createDataBuilder();
    stepOver(JsonToken.START_OBJECT);
    JsonToken token;
    while ((token = in.nextToken()) == JsonToken.FIELD_NAME) {
      String tagName = in.getCurrentName();
      Tag tag = effectiveType.tagsMap().get(tagName);
      if (tag == null)
        throw error("Unknown tag '" + tagName + "' in type '" + effectiveType.name().toString() + "'");

      Collection<? extends ReqOutputModelProjection> tagModelProjections =
          tagModelProjections(tag, projections, () -> new ArrayList<>(projections.size()));
      if (tagModelProjections == null) { // the tag was not requested in projection
        throw error("Unexpected tag '" + tagName + "'");
      } else {
        Val value = readValue(tag, tagModelProjections);
        data._raw().setValue(tag, value);
      }
    }
    ensure(token, JsonToken.END_OBJECT);

    for (final ReqOutputVarProjection projection : projections) {
      for (final ReqOutputTagProjectionEntry tagProjectionEntry : projection.tagProjections().values()) {
        if (tagProjectionEntry.projection().required() && data._raw().getValue(tagProjectionEntry.tag()) == null)
          throw error("Missing data for required tag '" + tagProjectionEntry.tag().name() + "'");
      }
    }

    return data;
  }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val readValue(
      @NotNull Tag tag,
      @NotNull Collection<? extends ReqOutputModelProjection> tagModelProjections // non-empty
  ) throws IOException {

    assert !tagModelProjections.isEmpty();

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
    final @NotNull Datum datum = finishReadingDatum(token, firstFieldName, tagModelProjections, type);
    return datum.asValue();
  }

  @SuppressWarnings("unchecked")
  @NotNull
  private Datum finishReadingDatum(
      final JsonToken token,
      final String firstFieldName,
      final @NotNull Collection<? extends ReqOutputModelProjection> tagModelProjections,
      final DatumType type) throws IOException {

    final @NotNull Datum datum;
    switch (type.kind()) {
      case RECORD:
        datum = finishReadingRecord(
            token,
            firstFieldName,
            (RecordType) type,
            (Collection<? extends ReqOutputRecordModelProjection>) tagModelProjections
        );
        break;
      case MAP:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingMap(
            token,
            (MapType) type,
            (Collection<? extends ReqOutputMapModelProjection>) tagModelProjections
        );
        break;
      case LIST:
        if (firstFieldName != null) throw expected("'['");
        datum = finishReadingList(
            token,
            (ListType) type,
            (Collection<? extends ReqOutputListModelProjection>) tagModelProjections
        );
        break;
      case PRIMITIVE:
        if (firstFieldName != null) throw expected("primitive value");
        datum = finishReadingPrimitive(
            token,
            (PrimitiveType) type
            // (Collection<? extends ReqOutputPrimitiveModelProjection>) tagModelProjections
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
    try { errorCode = in.getIntValue(); } catch (JsonParseException jpe) { throw expected("integer error code"); }
    stepOver(JsonFormat.ERROR_MESSAGE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String message = in.getText();
    // TODO read custom error properties here (if we decide to support these)
    stepOver(JsonToken.END_OBJECT);
    return new ErrorValue(errorCode, message, null);
  }

  // `}` or `: DATA, "field": DATA, ... }`
  private @NotNull RecordDatum finishReadingRecord(
      @NotNull JsonToken token,
      @Nullable String fieldName,
      @NotNull RecordType type,
      @NotNull Collection<? extends ReqOutputRecordModelProjection> projections // non-empty
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
        List<? extends ReqOutputVarProjection> varProjections =
            fieldVarProjections(projections, field, () -> new ArrayList<>(projections.size()));
        if (varProjections == null) throw error("Unexpected field '" + fieldName + "'");
        Data fieldData = readData(varProjections);
        datum._raw().setData(field, fieldData);

        token = nextNonEof();
        if (token == JsonToken.END_OBJECT) break;
        if (token == JsonToken.FIELD_NAME) fieldName = in.getCurrentName();
        else throw expected("field name or '}'");
      }
    }
    // todo ensure all required fields are present
    return datum;
  }


  // `]` or ` MAP_ENTRY , MAP ENTRY ... ]`
  // MAP_ENTRY ::= '{' "key" ':' DATUM ',' "value" ':' DATA '}'
  private @NotNull MapDatum finishReadingMap(
      @NotNull JsonToken token,
      @NotNull MapType type,
      @NotNull Collection<? extends ReqOutputMapModelProjection> projections // non-empty
  ) throws IOException {

    ensure(token, JsonToken.START_ARRAY);

    @NotNull final DatumType keyType = type.keyType();
    final MapDatum.@NotNull Builder datum = type.createBuilder();
    @Nullable final Set<Datum> expectedKeys = getExpectedKeys(projections);
    final List<ReqOutputVarProjection> itemProjections =
        projections.stream().map(ReqOutputMapModelProjection::itemsProjection).collect(Collectors.toList());

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      if (token == JsonToken.START_OBJECT) {
        stepOver(JsonFormat.MAP_ENTRY_KEY_FIELD);
        @Nullable final Datum keyValue = readDatum(keyType);
        if (keyValue == null) throw error("Null map keys are not allowed");
        if (expectedKeys != null && !expectedKeys.contains(keyValue))
          throw error("Key was not requested: '" + keyValue + "'"); // todo pretty print
        //noinspection SuspiciousMethodCalls
        if (datum._raw().elements().containsKey(keyValue))
          throw error("Key specified twice: '" + keyValue + "'"); // todo pretty print

        stepOver(JsonFormat.MAP_ENTRY_VALUE_FIELD);

        @NotNull final Data value = readData(itemProjections);
        datum._raw().elements().put(keyValue.toImmutable(), value);
      } else throw expected("'{' or ']");
    }

    return datum;
  }

  @Nullable
  private Set<Datum> getExpectedKeys(
      @NotNull Collection<? extends ReqOutputMapModelProjection> projections) {

    Set<Datum> expectedKeys = null;

    for (final ReqOutputMapModelProjection projection : projections) {
      @Nullable final List<ReqOutputKeyProjection> keyProjections = projection.keys();
      if (keyProjections == null) return null; // '*' : all keys allowed

      if (expectedKeys == null) expectedKeys = new HashSet<>();
      expectedKeys.addAll(keyProjections.stream().map(ReqOutputKeyProjection::value).collect(Collectors.toList()));
    }

    return expectedKeys;
  }

  private @NotNull ListDatum finishReadingList(
      @NotNull JsonToken token,
      @NotNull ListType type,
      @NotNull Collection<? extends ReqOutputListModelProjection> projections // non-empty
  ) throws IOException {

    ensure(token, JsonToken.START_ARRAY);

    final ListDatum.@NotNull Builder datum = type.createBuilder();
    final List<ReqOutputVarProjection> itemProjections =
        projections.stream().map(ReqOutputListModelProjection::itemsProjection).collect(Collectors.toList());

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      @NotNull final Data value = readData(itemProjections);
      datum._raw().elements().add(value);
    }

    return datum;
  }

  @SuppressWarnings("unchecked")
  private @NotNull PrimitiveDatum<?> finishReadingPrimitive(
      @NotNull JsonToken token,
      @NotNull PrimitiveType<?> type
  ) throws IOException {

    final Object nativeValue;

    if (type instanceof StringType) {
      ensure(token, JsonToken.VALUE_STRING);
      nativeValue = in.getValueAsString();
    } else if (type instanceof BooleanType) {
      ensure(token, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE);
      nativeValue = in.getValueAsBoolean();
    } else if (type instanceof DoubleType) {
      ensure(token, JsonToken.VALUE_NUMBER_FLOAT);
      nativeValue = in.getValueAsDouble();
    } else if (type instanceof LongType) {
      ensure(token, JsonToken.VALUE_NUMBER_INT);
      nativeValue = in.getValueAsLong();
    } else if (type instanceof IntegerType) {
      ensure(token, JsonToken.VALUE_NUMBER_INT);
      nativeValue = in.getValueAsInt();
    } else throw error("Unknown primitive type: '" + type.name() + "' (" + type.getClass().getName() + ")");

    return ((PrimitiveType) type).createBuilder(nativeValue);
  }

//  private @Nullable Data readMonoData(@NotNull ReqOutputVarProjection projection) throws IOException {
//    projection.type();
//    projection.
//    final Val value;
//    switch (in.nextToken()) {
//      case VALUE_NULL:
//        value = pronull;
//        break;
//      case START_OBJECT: // record or error
//        value = readRecordOrError(projection);
//
//      default:
//        throw new IllegalArgumentException("Expected null or object at " + in.getTokenLocation());
//    }
//  }

  private @NotNull Type readType(
      @NotNull Collection<? extends AbstractVarProjection<?, ?, ?>> projections // polymorphic tails ignored
  ) throws IOException {
    stepOver(JsonFormat.POLYMORPHIC_TYPE_FIELD);
    stepOver(JsonToken.VALUE_STRING, "string value");
    String typeName = in.getText();
    Type type = resolveType(projections, typeName);
    if (type == null)
      throw error("Invalid type '" + typeName + "'");
    return type;
  }

  private @Nullable Type resolveType(
      @NotNull Collection<? extends AbstractVarProjection<?, ?, ?>> projections, // polymorphic tails ignored
      @NotNull String typeName
  ) {
    /*
    // TODO get rid of the cache and search every time (to assert type is valid for specific projections)?
    // TODO or use local identity cache with projections collection as key?
    Type type = cachedTypes.get(typeName);
    if (type == null && (type = findType(projections, typeName)) != null) cachedTypes.put(typeName, type);
    return type;
    */

    return findType(projections, typeName); // KS: removed caching
  }

  private @Nullable Type findType(
      @NotNull Collection<? extends AbstractVarProjection<?, ?, ?>> projections, // polymorphic tails ignored
      @NotNull String typeName
  ) {
    for (AbstractVarProjection vp : projections) {
      Type type = vp.type();
      if (typeName.equals(type.name().toString())) return type;
    }
    return null;
//  return projections.stream().map(AbstractVarProjection::type).filter(vpt -> typeName.equals(vpt.name().toString())).findAny().orElse(null);
  }

//  @Deprecated
//  private @NotNull Type readType(@NotNull ReqOutputVarProjection projection) throws IOException {
//    stepOver(JsonFormat.POLYMORPHIC_TYPE_FIELD);
//    stepOver(JsonToken.VALUE_STRING, "string value");
//    String typeName = in.getText();
//    Type type = resolveType(projection, typeName);
//    if (type == null)
//      throw new IllegalArgumentException("Invalid type '" + typeName + "' at " + in.getCurrentLocation());
//    return type;
//  }
//
//  @Deprecated
//  private @Nullable Type resolveType(@NotNull ReqOutputVarProjection projection, @NotNull String typeName) {
//    Type type = cachedTypes.get(typeName);
//    if (type == null && (type = findType(projection, typeName)) != null) cachedTypes.put(typeName, type);
//    return type;
//  }
//
//  @Deprecated
//  private @Nullable Type findType(@NotNull ReqOutputVarProjection projection, @NotNull String typeName) {
//    Type type = projection.type();
//    if (!typeName.equals(type.name().toString())) {
//      type = null;
//      Iterable<? extends ReqOutputVarProjection> tail = projection.polymorphicTails();
//      if (tail != null) for (ReqOutputVarProjection vp : tail) if ((type = findType(vp, typeName)) != null) break;
//    }
//    return type;
//  }

  public @Nullable Data readData(@NotNull DataType dataType) throws IOException {
    JsonToken token = nextNonEof();

    if (token == JsonToken.VALUE_NULL) return null;

    @NotNull final Type type = dataType.type;
    final Data.@NotNull Builder data = type.createDataBuilder();

    if (type.kind() == TypeKind.UNION) {
      ensure(token, JsonToken.START_OBJECT);

      while ((token = in.nextToken()) == JsonToken.FIELD_NAME) {
        String tagName = in.getCurrentName();
        Tag tag = type.tagsMap().get(tagName);
        if (tag == null)
          throw error("Unknown tag '" + tagName + "' in type '" + type.name().toString() + "'");

        Val value = readValue(tag);
        data._raw().setValue(tag, value);
      }
      ensure(token, JsonToken.END_OBJECT);
    } else {
      DatumType datumType = (DatumType) type;
      @NotNull final Tag selfTag = datumType.self;

      @NotNull Val val = readValue(selfTag);
      data._raw().setValue(selfTag, val);
    }

    return data;
  }

  // VALUE ::= ERROR or DATUM or null
  private @NotNull Val readValue( @NotNull Tag tag ) throws IOException {

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

  public @Nullable Datum readDatum(@NotNull ReqOutputModelProjection projection) throws IOException {
    @NotNull final JsonToken token = nextNonEof();

    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT)
      firstFieldName = in.nextFieldName();
    else firstFieldName = null;

    return finishReadingDatum(token, firstFieldName, Collections.singleton(projection), projection.model());
  }

  public @Nullable Datum readDatum(@NotNull DatumType type) throws IOException {
    @NotNull final JsonToken token = nextNonEof();

    final @Nullable String firstFieldName;
    if (token == JsonToken.START_OBJECT)
      firstFieldName = in.nextFieldName();
    else firstFieldName = null;

    return finishReadingDatum(token, firstFieldName, type);
  }

  public @NotNull Val readValue(@NotNull DatumType type) throws IOException {
    @NotNull final JsonToken token = nextNonEof();
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

  @NotNull
  private Datum finishReadingDatum(
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
            token,
            (PrimitiveType) type
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

    @NotNull final DatumType keyType = type.keyType();
    final MapDatum.@NotNull Builder datum = type.createBuilder();

    while (true) {
      token = nextNonEof();
      if (token == JsonToken.END_ARRAY) break;
      if (token == JsonToken.START_OBJECT) {
        stepOver(JsonFormat.MAP_ENTRY_KEY_FIELD);
        @Nullable final Datum keyValue = readDatum(keyType);
        if (keyValue == null) throw error("Null map keys are not allowed");
        //noinspection SuspiciousMethodCalls
        if (datum._raw().elements().containsKey(keyValue))
          throw error("Key specified twice: '" + keyValue + "'"); // todo pretty print

        stepOver(JsonFormat.MAP_ENTRY_VALUE_FIELD);

        @Nullable final Data value = readData(type.valueType());
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
      @Nullable final Data value = readData(type.elementType());
      if (value == null) throw error("Null list values are not allowed"); // or try to construct null datum?
      datum._raw().elements().add(value);
    }

    return datum;
  }

  public @NotNull ErrorValue readError() throws IOException {
    stepOver(JsonToken.START_OBJECT);
    return finishReadingError();
  }

  /**
   * Returns `true` iff at least one of projections has polymorphic tails.
   */
  private static boolean isPolymorphic(Collection<? extends AbstractVarProjection<?, ?, ?>> projections) {
    return projections.stream().anyMatch(vp -> vp.polymorphicTails() != null);
  }

//  private static Type mostSpecificType(Collection<? extends AbstractVarProjection<?, ?, ?>> projections) {
//    return projections.stream()
//        .map(AbstractVarProjection::type)
//        .max((o1, o2) -> {
//          if (o1.isAssignableFrom(o2)) return -1;
//          if (o2.isAssignableFrom(o1)) return 1;
//          return 0;
//        })
//        .orElseThrow(RuntimeException::new); // can't happen, projections should contain at least one element
//  }

//  private static <P> @NotNull Deque<? extends ReqOutputVarProjection> varProjections(
//      @NotNull Collection<? extends P> projections, // non-empty
//      @NotNull Function<P, ReqOutputVarProjection> varFunc
//  ) {
//    assert !projections.isEmpty() : "no projection(s)";
//    ArrayDeque<ReqOutputVarProjection> subProjections = new ArrayDeque<>(projections.size());
//    for (P projection : projections) subProjections.add(varFunc.apply(projection));
//    return subProjections;
//  }

  @NotNull
  private JsonToken nextNonEof() throws IOException {return checkEof(in.nextToken());}

  @Contract("null -> fail")
  private @NotNull JsonToken checkEof(@Nullable JsonToken token) throws IllegalArgumentException {
    if (token == null) throw new IllegalArgumentException("Unexpected EOF at " + in.getTokenLocation());
    return token;
  }

//  private @NotNull JsonToken next(@NotNull JsonToken expected, @NotNull String expectedText)
//      throws IOException, IllegalArgumentException {
//    JsonToken token = in.nextToken();
//    ensure(token, expectedText, expected);
//    return token;
//  }

  private void ensure(@Nullable JsonToken actual, @NotNull JsonToken... expected) throws IOException {
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

  private void stepOver(@NotNull JsonToken expected) throws IOException {
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

  private IllegalArgumentException error(@NotNull String message) {
    return new IllegalArgumentException(message + " at " + in.getCurrentLocation());
  }

  @Contract(pure = true)
  private static @NotNull String str(@Nullable String text) { return text == null ? "EOF" : '\'' + text + '\''; }
}
