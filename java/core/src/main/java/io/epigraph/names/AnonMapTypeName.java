/* Created by yegor on 7/22/16. */

package io.epigraph.names;

public class AnonMapTypeName implements TypeName {

  public final TypeName keyTypeName;

  public final TypeName valueTypeName;

  public AnonMapTypeName(TypeName keyTypeName, TypeName valueTypeName) {
    this.keyTypeName = keyTypeName;
    this.valueTypeName = valueTypeName;
  }

}
