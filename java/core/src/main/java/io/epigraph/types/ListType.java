/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.datum.ListDatum;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ListType extends DatumType {

  public final @NotNull Type elementType;

  private ListType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ListType> immediateSupertypes,
      boolean polymorphic,
      @NotNull Type elementType
  ) {
    super(name, immediateSupertypes, polymorphic);
    this.elementType = elementType;
  }

  protected ListType(@NotNull AnonListTypeName name, boolean polymorphic, @NotNull Type elementType) {
    this(
        name,
        elementType.immediateSupertypes().stream().map(Type::listOf).collect(Collectors.toList()),
        polymorphic,
        elementType
    );
  }

  protected ListType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull NamedListType> immediateNamedSupertypes,
      boolean polymorphic,
      @NotNull Type elementType
  ) {
    this(name, addAnonSupertypes(immediateNamedSupertypes, elementType), polymorphic, elementType);
  }

  private static @NotNull List<ListType> addAnonSupertypes(List<NamedListType> namedSupertypes, Type elementType) {
    Stream<? extends Type> missingElementSupertypes = elementType.immediateSupertypes().stream().filter(est ->
        namedSupertypes.stream().anyMatch(nst -> !nst.elementType.doesExtend(est))
    );
    return Stream.concat(
        namedSupertypes.stream(),
        missingElementSupertypes.map(Type::listOf)
    ).collect(Collectors.toList());
  }

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

  public abstract @NotNull ListDatum.Mut createMutableDatum();

}
