/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.builders.RecordDatumBuilder;
import io.epigraph.data.mutable.MutRecordDatum;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class RecordType extends DatumType {

  private @Nullable Collection<? extends Field> fields = null;

  private @Nullable Map<String, ? extends Field> fieldsMap = null;

  public RecordType(QualifiedTypeName name, List<RecordType> immediateSupertypes, boolean polymorphic) {
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

  public final @NotNull Collection<? extends Field> fields() {
    // TODO produce better ordering of the fields (i.e. supertypes first, in the order of supertypes and their fields declaration)
    if (fields == null) { // TODO move initialization to constructor (if possible?)
      LinkedList<Field> acc = new LinkedList<>(immediateFields());
      for (RecordType st : supertypes()) {
        st.fields().stream().filter(sf -> // TODO check that fields overridden (subtyped) are marked as abstract
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

  public abstract @NotNull RecordDatumBuilder builder(); // TODO createBuilder()?

  public abstract @NotNull MutRecordDatum mutable(); // TODO createMutable()?

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


  public static class Field {

    public final String name;

    public final Type type;

    public final boolean isAbstract;

    public Field(String name, Type type, boolean isAbstract) { // TODO capture overridden super-fields?
      this.name = name;
      this.type = type;
      this.isAbstract = isAbstract;
    }

  }


//  public static class Dynamic extends RecordType { // TODO this should be a type builder
//
//    private final List<Field> immediateFields = new ArrayList<>();
//
//    public Dynamic(
//        QualifiedTypeName name,
//        List<RecordType> immediateSupertypes,
//        boolean polymorphic
//    ) {
//      super(name, immediateSupertypes, polymorphic);
//    }
//
//    @Override
//    public List<Field> immediateFields() {
//      return immediateFields;
//    }
//
//  }

}
