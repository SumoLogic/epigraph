/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;

import java.util.List;

public abstract class NamedListType extends ListType {

  public NamedListType(
      QualifiedTypeName name,
      List<NamedListType> immediateNamedSupertypes,
      boolean polymorphic,
      Type elementType
  ) {
    super(name, immediateNamedSupertypes, polymorphic, elementType);
  }

}
