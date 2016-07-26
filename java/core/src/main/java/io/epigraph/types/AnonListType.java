/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.AnonListTypeName;

public class AnonListType extends ListType {

  public AnonListType(boolean polymorphic, Type elementType) {
    super(AnonListTypeName.of(polymorphic, elementType.name()), polymorphic, elementType);
  }

  @Override
  public AnonListTypeName name() {
    return (AnonListTypeName) super.name();
  }

}
