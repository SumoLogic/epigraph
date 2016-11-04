///* Created by yegor on 10/20/16. */
//
//package ws.epigraph.wire.json;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonToken;
//import ws.epigraph.data.Data;
//import ws.epigraph.data.Datum;
//import ws.epigraph.data.RecordDatum;
//import ws.epigraph.data.Val;
//import ws.epigraph.errors.ErrorValue;
//import ws.epigraph.projections.abs.AbstractVarProjection;
//import ws.epigraph.projections.req.output.ReqOutputListModelProjection;
//import ws.epigraph.projections.req.output.ReqOutputMapModelProjection;
//import ws.epigraph.projections.req.output.ReqOutputModelProjection;
//import ws.epigraph.projections.req.output.ReqOutputPrimitiveModelProjection;
//import ws.epigraph.projections.req.output.ReqOutputRecordModelProjection;
//import ws.epigraph.projections.req.output.ReqOutputVarProjection;
//import ws.epigraph.types.*;
//import ws.epigraph.types.Type.Tag;
//import ws.epigraph.wire.FormatReader;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.function.Function;
//
//import static ws.epigraph.types.RecordType.Field;
//import static ws.epigraph.wire.json.JsonFormatCommon.*;
//
//// DATA ::= POLYDATA or MONODATA                          // triggered by projection (polymorphic tails presence)
//// POLYDATA ::= { "type" : "TYPE", "data": MONODATA }
//// MONODATA ::= MULTIDATA or VALUE                        // triggered by projection (parenthesized flag)
//// MULTIDATA ::= { "tag": VALUE, ... }                    // 0 or more
//// VALUE ::= ERROR or DATUM or null
//// ERROR ::= { "ERROR": integer, "message": string }
//// DATUM ::= RECORD or MAP or LIST or PRIMITIVE or ENUM
//// RECORD ::= { "field": DATA, ... }                      // 0 or more
//// MAP ::= [ { "key": DATUM, "value": DATA }, ... ]       // 0 or more
//// LIST ::= [ DATA, ... ]                                 // 0 or more
//// PRIMITIVE ::= string or integer or long or double or boolean
//// ENUM ::= string // ? TODO
//
//public class JsonFormatReader implements FormatReader<IOException> {
//
//  private final @NotNull JsonParser in;
//
//  private final @NotNull Map<String, Type> cachedTypes = new HashMap<>();
//
//  public JsonFormatReader(@NotNull JsonParser jsonParser) { this.in = jsonParser; }
//
//  public @NotNull Data readData(@NotNull ReqOutputVarProjection projection) throws IOException {
//    Data data = readData(Collections.singletonList(projection));
//    stepOver(null, "EOF");
//    return data;
//  }
//
//  private @NotNull Data readData( // DATA ::= POLYDATA or MONODATA
//      @NotNull List<? extends ReqOutputVarProjection> projections // non-empty, polymorphic tails respected
//  ) throws IOException {
//    final Data data;
//    JsonToken token = checkEof(in.nextToken());
//    boolean readPoly = polymorphic(projections);
//    List<? extends ReqOutputVarProjection> flattened = flatten(new ArrayList<>(), projections, type);
//
//    final Type type;
//    if (readPoly) { // { "type": "list[epigraph.String]", "data": MONODATA }
//      ensure(token, JsonToken.START_OBJECT, "'{'");
//      type = readType(projections); // TODO check the type is valid for request projection (see TODO in readType(...))
//      stepOver(JsonFormat.POLYMORPHIC_VALUE_FIELD);
//    } else type = mostSpecificType(projections);
//
//    String monoTagName = type.kind() == TypeKind.UNION ? monoTag(projections) : DatumType.MONO_TAG_NAME;
//    if (monoTagName == null) { // MULTIDATA ::= { "tag": VALUE, ... }
//      data = readMultiData(type, flatten(new ArrayList<>(), projections, type));
//    } else { // VALUE ::= ERROR or DATUM or null
//      Tag tag = type.tagsMap().get(monoTagName);
//      assert tag != null : "invalid tag";
//      Collection<? extends ReqOutputModelProjection> tagModelProjections =
//          tagModelProjections(tag, projections /*FIXME flatten*/, () -> new ArrayList<>(projections.size()));
//      assert tagModelProjections != null : "missing mono tag";
//      data = type.createDataBuilder()._raw().setValue(tag, readValue(tag, tagModelProjections));
//    }
//
//    if (readPoly) stepOver(JsonToken.END_OBJECT, "'}'"); // TODO verify it's not already consumed (by invoked code)
//    return data;
//  }
//
//
//  private @NotNull Data readMultiData( // MULTIDATA ::= { "tag": VALUE, ... }
//      @NotNull Type type,
//      @NotNull List<? extends ReqOutputVarProjection> projections // non-empty, polymorphic tails ignored
//  ) throws IOException {
//    Data.Builder data = type.createDataBuilder();
//    stepOver(JsonToken.START_OBJECT, "'{'");
//    JsonToken token;
//    while ((token = in.nextToken()) == JsonToken.FIELD_NAME) {
//      String tagName = in.getCurrentName();
//      Tag tag = type.tagsMap().get(tagName);
//      if (tag == null)
//        throw new IllegalArgumentException("Unknown tag '" + tagName + "' at " + in.getCurrentLocation());
//      Collection<? extends ReqOutputModelProjection> tagModelProjections =
//          tagModelProjections(tag, projections, () -> new ArrayList<>(projections.size()));
//      if (tagModelProjections == null) { // the tag was not requested in projection
//        throw new IllegalArgumentException("Unexpected tag '" + tagName + "' at " + in.getCurrentLocation());
//      } else {
//        Val value = readValue(tag, tagModelProjections);
//        data._raw().setValue(tag, value);
//      }
//    }
//    ensure(token, JsonToken.END_OBJECT, "'}'");
//    // TODO check all effectively required tags have been provided (nulls ok?)
//    // boolean tagRequired = tagModelProjections.stream().anyMatch(ReqOutputModelProjection::required);
//    // ...
//    return data;
//  }
//
//  private @NotNull Val readValue( // VALUE ::= ERROR or DATUM or null
//      @NotNull Tag tag,
//      @NotNull Collection<? extends ReqOutputModelProjection> tagModelProjections // non-empty
//  ) throws IOException {
//    DatumType type = tag.type;
//    @NotNull JsonToken token = checkEof(in.nextToken());
//    // null?
//    if (token == JsonToken.VALUE_NULL) return type.createValue(null);
//    // error?
//    final @Nullable String firstFieldName;
//    if (token == JsonToken.START_OBJECT) { // can be a record or an error
//      firstFieldName = in.nextFieldName(); // advances to next token (field name or end object - in valid cases)
//      if (JsonFormat.ERROR_CODE_FIELD.equals(firstFieldName)) return type.createValue(finishReadingError());
//    } else firstFieldName = null;
//    // datum
//    final @NotNull Datum datum;
//    switch (type.kind()) {
//      case RECORD:
//        datum = finishReadingRecord(
//            token,
//            firstFieldName,
//            (RecordType) type,
//            (Collection<? extends ReqOutputRecordModelProjection>) tagModelProjections
//        );
//        break;
//      case MAP:
//        datum = readMap(
//            token,
//            (MapType) type,
//            (Collection<? extends ReqOutputMapModelProjection>) tagModelProjections
//        );
//        break;
//      case LIST:
//        datum = readList(
//            token,
//            (ListType) type,
//            (Collection<? extends ReqOutputListModelProjection>) tagModelProjections
//        );
//        break;
//      case PRIMITIVE:
//        datum = readPrimitive(
//            token,
//            (PrimitiveType) type,
//            (Collection<? extends ReqOutputPrimitiveModelProjection>) tagModelProjections
//        );
//        break;
////    case ENUM: // TODO once enums are supported
////      datum = readEnum(
////        token,
////        (EnumType) type,
////        (Collection<? extends ReqOutputEnumModelProjection>) tagModelProjections
////      );
////      break;
//      case UNION: // this one is 500 - there should be no such model projections
//      default:
//        throw new UnsupportedOperationException(type.kind().name());
//    }
//    return datum.asValue();
//  }
//
//  private @NotNull ErrorValue finishReadingError() throws IOException { // `: 404, "message": "blah" }`
//    stepOver(JsonToken.VALUE_NUMBER_INT, "integer value");
//    int errorCode;
//    try { errorCode = in.getIntValue(); } catch (JsonParseException jpe) { throw expected("integer error code"); }
//    stepOver(JsonFormat.ERROR_MESSAGE_FIELD);
//    stepOver(JsonToken.VALUE_STRING, "string value");
//    String message = in.getText();
//    // TODO read custom error properties here (if we decide to support these)
//    stepOver(JsonToken.END_OBJECT, "'}'");
//    return new ErrorValue(errorCode, message, null);
//  }
//
//  private @NotNull RecordDatum finishReadingRecord( // `}` or `: DATA, "field": DATA, ... }`
//      @NotNull JsonToken token,
//      @Nullable String firstFieldName,
//      @NotNull RecordType type,
//      @NotNull Collection<? extends ReqOutputRecordModelProjection> projections // non-empty
//  ) throws IOException {
//    ensure(token, JsonToken.START_OBJECT, "'{'");
//    RecordDatum.Builder datum = type.createBuilder();
//    if (firstFieldName == null) { // empty record?
//      ensureCurr(JsonToken.END_OBJECT, "field name or '}'");
//      return datum;
//    } else {
//      Field field = type.fieldsMap().get(firstFieldName);
//      if (field == null) throw new IllegalArgumentException(
//          "Unknown field '" + firstFieldName + "' in record type '" + type.name().toString() + "'"
//      );
//      List<? extends ReqOutputVarProjection> varProjections =
//          fieldVarProjections(projections, field, () -> new ArrayList<>(projections.size()));
//      if (varProjections == null) throw new IllegalArgumentException("Unexpected field '" + firstFieldName + "'");
//      Data fieldData = readData(varProjections);
//      datum._raw().setData(field, fieldData);
//    }
//    // TODO remaining fields...
//    stepOver(JsonToken.END_OBJECT, "'}'");
//    return datum;
//  }
//
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
//
//  private @NotNull Type readType(
//      @NotNull Collection<? extends AbstractVarProjection> projections // polymorphic tails ignored
//  ) throws IOException {
//    stepOver(JsonFormat.POLYMORPHIC_TYPE_FIELD);
//    stepOver(JsonToken.VALUE_STRING, "string value");
//    String typeName = in.getText();
//    Type type = resolveType(projections, typeName);
//    if (type == null)
//      throw new IllegalArgumentException("Invalid type '" + typeName + "' at " + in.getCurrentLocation());
//    return type;
//  }
//
//  private @Nullable Type resolveType(
//      @NotNull Collection<? extends AbstractVarProjection> projections, // polymorphic tails ignored
//      @NotNull String typeName
//  ) {
//    // TODO get rid of the cache and search every time (to assert type is valid for specific projections)?
//    // TODO or use local identity cache with projections collection as key?
//    Type type = cachedTypes.get(typeName);
//    if (type == null && (type = findType(projections, typeName)) != null) cachedTypes.put(typeName, type);
//    return type;
//  }
//
//  private @Nullable Type findType(
//      @NotNull Collection<? extends AbstractVarProjection> projections, // polymorphic tails ignored
//      @NotNull String typeName
//  ) {
//    for (AbstractVarProjection vp : projections) {
//      Type type = vp.type();
//      if (typeName.equals(type.name().toString())) return type;
//    }
//    return null;
////  return projections.stream().map(AbstractVarProjection::type).filter(vpt -> typeName.equals(vpt.name().toString())).findAny().orElse(null);
//  }
//
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
//
//  public @Nullable Datum readDatum(@NotNull ReqOutputModelProjection projection) throws IOException {
//    return null; /* FIXME */
//  }
//
//  public @Nullable Data readData() throws IOException {
//    return null; /* FIXME */
//  }
//
//  public @Nullable Datum readDatum() throws IOException {
//    return null; /* FIXME */
//  }
//
//  public @NotNull Val readValue() throws IOException {
//    return null; /* FIXME */
//  }
//
//  public @NotNull ErrorValue readError() throws IOException {
//    return null; /* FIXME */
//  }
//
//  /** Returns `true` iff at least one of projections has polymorphic tails. */
//  private static boolean polymorphic(Collection<? extends AbstractVarProjection> projections) {
//    return projections.stream().anyMatch(vp -> vp.polymorphicTails() != null);
//  }
//
//  private static <P> @NotNull Deque<? extends ReqOutputVarProjection> varProjections(
//      @NotNull Collection<? extends P> projections, // non-empty
//      @NotNull Function<P, ReqOutputVarProjection> varFunc
//  ) {
//    assert !projections.isEmpty() : "no projection(s)";
//    ArrayDeque<ReqOutputVarProjection> subProjections = new ArrayDeque<>(projections.size());
//    for (P projection : projections) subProjections.add(varFunc.apply(projection));
//    return subProjections;
//  }
//
//
//  private @NotNull JsonToken checkEof(@Nullable JsonToken token) throws IllegalArgumentException {
//    if (token == null) throw new IllegalArgumentException("Unexpected EOF at " + in.getTokenLocation());
//    return token;
//  }
//
//  private @NotNull JsonToken next(@NotNull JsonToken expected, @NotNull String expectedText)
//      throws IOException, IllegalArgumentException {
//    JsonToken token = in.nextToken();
//    ensure(token, expected, expectedText);
//    return token;
//  }
//
//  private void ensure(@Nullable JsonToken actual, @Nullable JsonToken expected, @NotNull String expectedText)
//      throws IOException, IllegalArgumentException { if (actual != expected) throw expected(expectedText); }
//
//  private void ensureCurr(@Nullable JsonToken expected, @NotNull String expectedText) throws IOException {
//    ensure(in.currentToken(), expected, expectedText);
//  }
//
//  private void stepOver(@Nullable JsonToken expected, @NotNull String expectedText) throws IOException {
//    ensure(in.nextToken(), expected, expectedText);
//  }
//
//  private void stepOver(@NotNull String fieldName) throws IOException {
//    if (!fieldName.equals(in.nextFieldName())) throw expected('"' + fieldName + "\" field");
//  }
//
//  private IllegalArgumentException expected(@NotNull String expected) throws IllegalArgumentException, IOException {
//    return new IllegalArgumentException(
//        "Expected " + expected + " but got " + str(in.getText()) + " at " + in.getTokenLocation()
//    );
//  }
//
//  private static @NotNull String str(@Nullable String text) { return text == null ? "EOF" : '\'' + text + '\''; }
//
//}
