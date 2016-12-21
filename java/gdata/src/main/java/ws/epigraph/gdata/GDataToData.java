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

package ws.epigraph.gdata;

import ws.epigraph.data.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Transforms raw schema-less {@code GData} instances to typed {@code Data}
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class GDataToData {
  // todo attach location info to GData instances and propagate it to ProcessingExceptions

  private GDataToData() {}

  public static Data transform(
      @NotNull Type type,
      @NotNull GDataValue gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {

    if (gdata instanceof GData) {
      GData gData = (GData) gdata;

      @Nullable TypeRef typeRef = gData.typeRef();
      if (typeRef != null) type = resolveType(typeRef, Type.class, resolver, gdata.location());

      @NotNull Data.Builder builder = type.createDataBuilder();

      for (Map.Entry<String, GDatum> entry : gData.tags().entrySet()) {
        Type.Tag tag = type.tagsMap().get(entry.getKey());
        if (tag == null) throw new ProcessingException(
            String.format("Unknown tag '%s' in type '%s'", entry.getKey(), typeRef),
            gdata.location()
        );

        @NotNull DatumType tagType = tag.type;
        @NotNull Val tagValue = transform(tagType, entry.getValue(), resolver);

        builder._raw().setValue(tag, tagValue);
      }

      return builder;
    } else if (gdata instanceof GDatum) {
      GDatum gDatum = (GDatum) gdata;

      if (type instanceof DatumType) {
        DatumType datumType = (DatumType) type;

        Val value = transform(datumType, gDatum, resolver);

        @NotNull Data.Builder builder = type.createDataBuilder();
        builder._raw().setValue(datumType.self, value);

        return builder;
      } else throw new ProcessingException(
          String.format("Can't create vartype '%s' from datum '%s'", type.name(), gdata),
          gdata.location()
      );
    } else throw new IllegalArgumentException(gdata.getClass().getName());
  }

  public static @NotNull Val transform(
      @NotNull DatumType type,
      @NotNull GDatum gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable Datum datum = transformDatum(type, gdata, resolver);
    if (datum == null) return type.createValue(null);
    else return toVal(datum);
  }

  public static @Nullable Datum transformDatum(
      @NotNull DatumType type,
      @NotNull GDatum gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {

    if (gdata instanceof GNullDatum) {
      return null;
    } else if (gdata instanceof GPrimitiveDatum) {
      if (!(type instanceof PrimitiveType<?>))
        throw new ProcessingException(
            String.format("Can't transform primitive value '%s' into '%s'", gdata, type.name()),
            gdata.location()
        );

      return transform((PrimitiveType<?>) type, (GPrimitiveDatum) gdata, resolver);
    } else if (gdata instanceof GRecordDatum) {
      if (!(type instanceof RecordType))
        throw new ProcessingException(
            String.format("Can't transform record value '%s' into '%s'", gdata, type.name()),
            gdata.location()
        );

      return transform((RecordType) type, (GRecordDatum) gdata, resolver);
    } else if (gdata instanceof GMapDatum) {
      if (!(type instanceof MapType))
        throw new ProcessingException(
            String.format("Can't transform map value '%s' into '%s'", gdata, type.name()),
            gdata.location()
        );

      return transform((MapType) type, (GMapDatum) gdata, resolver);
    } else if (gdata instanceof GListDatum) {
      if (!(type instanceof ListType))
        throw new ProcessingException(
            String.format("Can't transform list value '%s' into '%s'", gdata, type.name()),
            gdata.location()
        );

      return transform((ListType) type, (GListDatum) gdata, resolver);
    } else {
      // TODO Enum
      throw new ProcessingException(
          String.format("Don't know how to handle '%s'", type.getClass().getName()),
          gdata.location()
      );
    }
  }


  public static @NotNull RecordDatum transform(
      @NotNull RecordType type,
      @NotNull GRecordDatum gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable TypeRef typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(typeRef, RecordType.class, resolver, gdata.location());

    @NotNull RecordDatum.Builder builder = type.createBuilder();

    for (Map.Entry<String, GDataValue> entry : gdata.fields().entrySet()) {
      String fieldName = entry.getKey();
      RecordType.Field field = type.fieldsMap().get(fieldName);

      if (field == null)
        throw new ProcessingException(
            String.format("Can't find field '%s' in type '%s'", fieldName, type.name()),
            gdata.location()
        );

      Data fieldData = transform(field.dataType.type, entry.getValue(), resolver);

      builder._raw().setData(field, fieldData);
    }

    return builder;
  }

  public static MapDatum transform(
      @NotNull MapType type,
      @NotNull GMapDatum gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable TypeRef typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(typeRef, MapType.class, resolver, gdata.location());

    @NotNull DatumType keyType = type.keyType();
    @NotNull Type valueType = type.valueType().type;
    @NotNull MapDatum.Builder builder = type.createBuilder();

    for (Map.Entry<GDatum, GDataValue> entry : gdata.entries().entrySet()) {
      @Nullable Datum key = transformDatum(keyType, entry.getKey(), resolver);
      if (key == null)
        throw new ProcessingException("'" + type.name() + "': Null keys in maps not allowed", gdata.location());

      Data value = transform(valueType, entry.getValue(), resolver);

      builder._raw().elements().put(key.toImmutable(), value);
    }

    return builder;
  }

  public static ListDatum transform(
      @NotNull ListType type,
      @NotNull GListDatum gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable TypeRef typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(typeRef, ListType.class, resolver, gdata.location());

    @NotNull Type elementType = type.elementType().type;
    @NotNull ListDatum.Builder builder = type.createBuilder();

    for (GDataValue gitem : gdata.values()) {
      Data item = transform(elementType, gitem, resolver);
      builder._raw().elements().add(item);
    }

    return builder;
  }

  // todo enum

  @SuppressWarnings("unchecked")
  public static PrimitiveDatum<?> transform(
      @NotNull PrimitiveType<?> type,
      @NotNull GPrimitiveDatum gdata,
      @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable TypeRef typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(typeRef, PrimitiveType.class, resolver, gdata.location());

    // TODO need to carefully coerce types here

    @NotNull Object n = gdata.value();
    if (type instanceof LongType) {
      if (n instanceof Number)
        n = ((Number) n).longValue();
    } else if (type instanceof IntegerType) {
      if (n instanceof Number)
        n = ((Number) n).intValue();
    } else if (type instanceof StringType) {
      n = n.toString();
    }

    try {
      return ((PrimitiveType<Object>) type).createBuilder(n);
    } catch (ClassCastException ignored) {
      throw new ProcessingException("Can't convert '" + n + "' to '" + type.name() + "'", gdata.location());
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Type> T resolveType(
      @NotNull TypeRef ref,
      @NotNull Class<T> expectedClass,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location) throws ProcessingException {
    @Nullable Type type = ref.resolve(resolver);
    if (type == null) throw new ProcessingException("Can't resolve type '" + ref + "'", location);

    if (expectedClass.isAssignableFrom(type.getClass()))
      return (T) type;
    else
      throw new ProcessingException(String.format(
          "Reference '%s' resolved to wrong type kind '%s', expected it to be '%s'",
          ref,
          type.getClass().getName(),
          expectedClass.getName()
      ), location);
  }

//  public static Val.Imm transform(@NotNull DatumType type,
//                                  @NotNull GNullDatum gdata,
//                                  @NotNull TypesResolver resolver) throws ProcessingException {
//    @Nullable TypeRef typeRef = gdata.typeRef();
//    if (typeRef != null) type = resolveType(typeRef, RecordType.class, resolver, gdata.location());
//    return type.createValue(null);
//  }

  private static @NotNull Val.Imm toVal(@NotNull Datum datum) {
    return datum.toImmutable().asValue();
  }

  public static class ProcessingException extends Exception {
    private final @NotNull TextLocation location;

    ProcessingException(String message, @NotNull TextLocation location) {
      super(message);
      this.location = location;
    }

    public @NotNull TextLocation location() {
      return location;
    }
  }
}
