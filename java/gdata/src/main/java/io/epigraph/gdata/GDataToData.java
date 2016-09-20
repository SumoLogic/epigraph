package io.epigraph.gdata;

import io.epigraph.data.*;
import io.epigraph.lang.Fqn;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataToData {
  public static Data transform(@NotNull Type type,
                               @NotNull GDataValue gdata,
                               @NotNull TypesResolver resolver) throws ProcessingException {

    //todo gdata can be one of GDataVar or GDataVarValue, either create var or samovar

    if (gdata instanceof GData) {
      GData gData = (GData) gdata;

      @Nullable Fqn typeRef = gData.typeRef();
      if (typeRef != null) type = resolveType(resolver, typeRef, Type.class);

      @NotNull Data.Builder builder = type.createDataBuilder();

      for (Map.Entry<String, GDatum> entry : gData.tags().entrySet()) {
        Type.Tag tag = type.tagsMap().get(entry.getKey());
        if (tag == null) throw new ProcessingException(
            String.format("Unknown tag '%s' in type '%s'", entry.getKey(), typeRef)
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
          String.format("Can't create vartype '%s' from datum '%s'", type.name(), gdata)
      );
    } else throw new IllegalArgumentException(gdata.getClass().getName());
  }

  @NotNull
  public static Val transform(@NotNull DatumType type,
                              @NotNull GDatum gdata,
                              @NotNull TypesResolver resolver) throws ProcessingException {

    if (gdata instanceof GNullDatum) {
      return transform(type, (GNullDatum) gdata, resolver);
    } else if (gdata instanceof GPrimitiveDatum) {
      if (!(type instanceof PrimitiveType<?>))
        throw new ProcessingException(
            String.format("Can't transform primitive value '%s' into '%s'", gdata, type.name())
        );

      return toVal(transform((PrimitiveType<?>) type, (GPrimitiveDatum) gdata, resolver));
    } else if (gdata instanceof GRecordDatum) {
      if (!(type instanceof RecordType))
        throw new ProcessingException(
            String.format("Can't transform record value '%s' into '%s'", gdata, type.name())
        );

      return toVal(transform((RecordType) type, (GRecordDatum) gdata, resolver));
    } else if (gdata instanceof GListDatum) {
      if (!(type instanceof ListType))
        throw new ProcessingException(
            String.format("Can't transform list value '%s' into '%s'", gdata, type.name())
        );

      return toVal(transform((ListType) type, (GListDatum) gdata, resolver));
    } else {
      // TODO Map, Enum
      throw new ProcessingException(String.format("Don't know how to handle '%s'", type.getClass().getName()));
    }
  }


  @NotNull
  public static RecordDatum transform(@NotNull RecordType type,
                                      @NotNull GRecordDatum gdata,
                                      @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable Fqn typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(resolver, typeRef, RecordType.class);

    @NotNull RecordDatum.Builder builder = type.createBuilder();

    for (Map.Entry<String, GDataValue> entry : gdata.fields().entrySet()) {
      // todo
      String fieldName = entry.getKey();
      RecordType.Field field = type.fieldsMap().get(fieldName);

      if (field == null)
        throw new ProcessingException(String.format("Can't find field '%s' in type '%s'", fieldName, type.name()));

      Data fieldData = transform(field.dataType.type, entry.getValue(), resolver);

      // todo should take non-builder
//      builder._raw().setData(field, fieldData);
    }

    return builder;
  }

  // todo map

  public static ListDatum transform(@NotNull ListType type,
                                    @NotNull GListDatum gdata,
                                    @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable Fqn typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(resolver, typeRef, ListType.class);

    @NotNull Type elementType = type.elementType().type;
    @NotNull ListDatum.Builder builder = type.createBuilder();

    for (GDataValue gitem : gdata.values()) {

      Data item = transform(elementType, gitem, resolver);

      // todo shouldn't take builders..
//      builder._raw().elements().add(
//data
//      );
    }

    return builder;
  }

  // todo enum
  public static PrimitiveDatum<?> transform(@NotNull PrimitiveType<?> type,
                                            @NotNull GPrimitiveDatum gdata,
                                            @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable Fqn typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(resolver, typeRef, PrimitiveType.class);

    // TODO need to carefully coerce types here

    @NotNull Object n = gdata.value();
    if (type instanceof LongType) {
      if (n instanceof Number)
        n = ((Number) n).longValue();
    } else if (type instanceof IntegerType) {
      if (n instanceof Number)
        n = ((Number) n).intValue();
    }

    //noinspection unchecked
    return ((PrimitiveType<Object>) type).createBuilder(n);
  }

  @NotNull
  private static <T extends Type> T resolveType(@NotNull TypesResolver resolver,
                                                @NotNull Fqn ref,
                                                @NotNull Class<T> expectedClass) throws ProcessingException {
    @Nullable Type type = resolver.resolve(ref);
    if (type == null) throw new ProcessingException("Can't resolve type '" + ref + "'");

    if (expectedClass.isAssignableFrom(type.getClass()))
      //noinspection unchecked
      return (T) type;
    else
      throw new ProcessingException(String.format(
          "Reference '%s' resolved to wrong type kind '%s', expected it to be '%s'",
          ref,
          type.getClass().getName(),
          expectedClass.getName()
      ));
  }

  public static Val.Imm transform(@NotNull DatumType type,
                              @NotNull GNullDatum gdata,
                              @NotNull TypesResolver resolver) throws ProcessingException {
    @Nullable Fqn typeRef = gdata.typeRef();
    if (typeRef != null) type = resolveType(resolver, typeRef, RecordType.class);
    return type.createValue(null);
  }

  @NotNull
  private static Val.Imm toVal(@NotNull Datum datum) {
    return datum.toImmutable().asValue();
  }

  public static class ProcessingException extends Exception {
    ProcessingException(String message) {
      super(message);
    }
  }
}
