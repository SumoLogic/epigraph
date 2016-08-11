/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public abstract class RecordType extends DatumType {

  private @Nullable Collection<? extends Field> fields = null;

  private @Nullable Map<String, ? extends Field> fieldsMap = null;

  public RecordType(QualifiedTypeName name, List<? extends RecordType> immediateSupertypes, boolean polymorphic) {
    super(name, immediateSupertypes, polymorphic);
  }

  @Override
  public @NotNull QualifiedTypeName name() { return (QualifiedTypeName) super.name(); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<? extends RecordType> immediateSupertypes() {
    return (List<? extends RecordType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<RecordType> supertypes() { return (Collection<RecordType>) super.supertypes(); }

  public abstract @NotNull List<@NotNull Field> immediateFields(); // TODO could be protected but used by pretty-printer

  public abstract @NotNull RecordDatum.Mut createMutableDatum();


  public final @NotNull Collection<? extends Field> fields() {
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

  public final @NotNull Map<String, ? extends Field> fieldsMap() {
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

    public final String name;

    public final Type type;

    public final boolean isAbstract;

    public Field(String name, Type type, boolean isAbstract) { // TODO capture overridden super-fields?
      this.name = name;
      this.type = type;
      this.isAbstract = isAbstract;
    }

    public String name() { return name; }

  }


  // TODO .Raw (final type builder?)


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends RecordDatum.Imm.Static,
      MyMutDatum extends RecordDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends RecordType implements DatumType.Static<
      RecordType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<RecordDatum.Mut.Raw, MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.Raw, MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.Raw, MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends RecordType> immediateSupertypes,
        boolean polymorphic,
        @NotNull Function<RecordDatum.Mut.Raw, MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.Raw, MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.Raw, MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes, polymorphic);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createMutableDatum() {
      return mutDatumConstructor.apply(new RecordDatum.Mut.Raw(this));
    }

    @Override
    public final @NotNull MyMutVal createMutableValue() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createMutableData() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
