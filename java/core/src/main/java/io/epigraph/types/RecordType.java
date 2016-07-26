/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class RecordType extends DatumType {

  public RecordType(QualifiedTypeName name, List<RecordType> immediateSupertypes, boolean polymorphic) {
    super(name, immediateSupertypes, polymorphic);
  }

  @Override
  public QualifiedTypeName name() {
    return (QualifiedTypeName) super.name();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<RecordType> immediateSupertypes() {
    return (List<RecordType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<RecordType> supertypes() {
    return (Collection<RecordType>) super.supertypes();
  }

  public abstract List<Field> immediateFields();

  @Nullable
  private Collection<? extends Field> fields = null;

  public Collection<? extends Field> fields() {
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


  public static class Field {

    public final String name;

    public final Type type;

    public Field(String name, Type type) {
      this.name = name;
      this.type = type;
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
