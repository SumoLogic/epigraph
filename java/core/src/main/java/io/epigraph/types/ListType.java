/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.ListDatum;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ListType extends DatumType {

  public final @NotNull DataType elementType; // TODO rename to elementDataType

  protected ListType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends ListType> immediateSupertypes,
      @NotNull DataType elementDataType
  ) {
    super(name, immediateSupertypes);
    this.elementType = elementDataType;
  }

//  protected ListType(@NotNull AnonListTypeName name, @NotNull DataType elementDataType) {
//    this(
//        name,
//        elementDataType.type.immediateSupertypes().stream().map(Type::listOf).collect(Collectors.toList()),
//        elementDataType
//    );
//  }
//
//  protected ListType(
//      @NotNull QualifiedTypeName name,
//      @NotNull List<@NotNull NamedListType> immediateNamedSupertypes,
//      @NotNull DataType elementDataType
//  ) {
//    this(name, addAnonSupertypes(immediateNamedSupertypes, elementDataType.type), elementDataType);
//  }
//
//  private static @NotNull List<@NotNull ListType> addAnonSupertypes(
//      @NotNull List<@NotNull ? extends NamedListType> namedSupertypes,
//      @NotNull Type elementType
//  ) {
//    Stream<? extends Type> missingElementSupertypes = elementType.immediateSupertypes().stream().filter(est ->
//        namedSupertypes.stream().anyMatch(nst -> !nst.elementType.type.doesExtend(est))
//    );
//    return Stream.concat(
//        namedSupertypes.stream(),
//        missingElementSupertypes.map(Type::listOf)
//    ).collect(Collectors.toList());
//  }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.LIST; }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends ListType> immediateSupertypes() {
    return (List<? extends ListType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends ListType> supertypes() {
    return (Collection<? extends ListType>) super.supertypes();
  }

  public abstract @NotNull ListDatum.Mut createBuilder();

}
