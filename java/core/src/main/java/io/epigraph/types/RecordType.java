/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import com.sun.prism.impl.Disposer;
import io.epigraph.data.Data;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public abstract class RecordType extends DatumType {

  private @Nullable Collection<@NotNull ? extends Field> fields = null;

  private @Nullable Map<@NotNull String, @NotNull ? extends Field> fieldsMap = null;

  public RecordType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends RecordType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.RECORD; }

  @Override
  public @NotNull QualifiedTypeName name() { return (QualifiedTypeName) super.name(); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends RecordType> immediateSupertypes() {
    return (List<? extends RecordType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull RecordType> supertypes() { return (Collection<RecordType>) super.supertypes(); }

  public abstract @NotNull List<@NotNull ? extends Field> immediateFields(); // TODO could be protected but used by pretty-printer

  public abstract @NotNull RecordDatum.Builder createBuilder();


  public final @NotNull Collection<@NotNull ? extends Field> fields() {
    // TODO produce better ordering of the fields (i.e. supertypes first, in the order of supertypes and their fields declaration)
    if (fields == null) { // TODO move initialization to constructor (if possible?)
      LinkedList<Field> acc = new LinkedList<>(immediateFields());
      for (RecordType st : supertypes()) {
        st.fields().stream().filter(sf ->
            acc.stream().noneMatch(af -> af.name.equals(sf.name))
        ).forEachOrdered(acc::add);
      }
      fields = Unmodifiable.collection(new LinkedHashSet<>(acc));
      assert fields.size() == acc.size(); // assert there was no duplicates in the acc
    }
    return fields;
  }

  public final @NotNull Map<@NotNull String, @NotNull ? extends Field> fieldsMap() {
    if (fieldsMap == null) fieldsMap = Unmodifiable.map(fields(), f -> f.name, f -> f);
    return fieldsMap;
  }

  public @NotNull Field assertReadable(@NotNull Field field) throws IllegalArgumentException {
    Field knownField = fieldsMap().get(field.name);
    // TODO use asserts?
    if (knownField == null) throw new IllegalArgumentException("Unknown field '" + field.name + "'");
    // FIXME check vs known's overridden fields instead?
    if (!field.type.isAssignableFrom(knownField.type)) throw new IllegalArgumentException(String.format(
        "Incompatible type for field '%s': expected '%s', actual '%s'",
        field.name,
        field.type.name(),
        knownField.type.name()
    ));
    return field;
  }

  public @NotNull Field assertWritable(@NotNull Field field) throws IllegalArgumentException {
    Field knownField = fieldsMap().get(field.name);
    // TODO use asserts?
    if (knownField == null) throw new IllegalArgumentException("Unknown field '" + field.name + "'");
    // FIXME check vs known's overridden fields instead?
    // FIXME this check is for reading, writing requires different rules
    if (!field.type.isAssignableFrom(knownField.type)) throw new IllegalArgumentException(String.format(
        "Incompatible type for field '%s': expected '%s', actual '%s'",
        field.name,
        field.type.name(),
        knownField.type.name()
    ));
    return field;
  }


  public static class Field { // TODO move out

    public final @NotNull String name;

    public final @NotNull DataType dataType;

    @Deprecated // use dataType().type()
    public final @NotNull Type type;

    public Field(
        @NotNull String name,
        @NotNull DataType dataType
    ) { // TODO capture overridden super-fields?
      this.name = name;
      this.dataType = dataType;
      this.type = dataType.type;
    }

    public @NotNull String name() { return name; }

    public @NotNull DataType dataType() { return dataType; }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Field field = (Field) o;
      return Objects.equals(name, field.name) &&
          Objects.equals(dataType, field.dataType);
    }

    @Override
    public int hashCode() { return Objects.hash(name, dataType); }

  }


  public static abstract class Raw extends RecordType implements DatumType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends RecordType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull RecordDatum.Builder createBuilder() { return new RecordDatum.Builder.Raw(this); }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends RecordDatum.Imm.Static,
      MyDatumBuilder extends RecordDatum.Builder.Static<MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends RecordType
      implements DatumType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<RecordDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends RecordType.Static> immediateSupertypes,
        @NotNull Function<RecordDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder() {
      return datumBuilderConstructor.apply(new RecordDatum.Builder.Raw(this));
    }

    @Override
    public final @NotNull MyImmVal createValue(@Nullable ErrorValue errorOrNull) {
      return immValConstructor.apply(Val.Imm.Raw.create(errorOrNull));
    }

    @Override
    public final @NotNull MyDataBuilder createDataBuilder() {
      return dataBuilderConstructor.apply(new Data.Builder.Raw(this));
    }

  }


}
