/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class NamedListType extends ListType {

  public NamedListType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends NamedListType> immediateNamedSupertypes,
      @NotNull DataType elementType
  ) { super(name, immediateNamedSupertypes, elementType); }

  // TODO .Raw

  // TODO .Static

}
