/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

  public abstract @NotNull RecordDatum.Mut createBuilder();


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

  // TODO public abstract @NotNull ImmRecordDatum immutable(@NotNull RecordDatum)?

  // TODO above and below need to be introduced in raw and static types separately?

  //  @Override
  //  public abstract @NotNull MutRecordDatum mutable();

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

    public final boolean isAbstract;

    public Field(
        @NotNull String name,
        @NotNull DataType dataType,
        boolean isAbstract
    ) { // TODO capture overridden super-fields?
      this.name = name;
      this.dataType = dataType;
      this.type = dataType.type;
      this.isAbstract = isAbstract;
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
    public int hashCode() {
      return Objects.hash(name, dataType);
    }
  }


  // TODO .Raw (final type builder?)


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends RecordDatum.Imm.Static,
      MyMutDatum extends RecordDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends RecordType
      implements DatumType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData> {

    private final @NotNull Function<RecordDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends RecordType.Static> immediateSupertypes,
        @NotNull Function<RecordDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder() {
      return mutDatumConstructor.apply(new RecordDatum.Mut.Raw(this));
    }

    @Override
    public final @NotNull MyMutVal createValueBuilder() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createDataBuilder() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
