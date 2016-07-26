/* Created by yegor on 7/22/16. */

package io.epigraph.names;

public class AnonListTypeName implements TypeName {

  public final TypeName elementTypeName;

  public final boolean polymorphic;

  public AnonListTypeName(boolean polymorphic, TypeName elementTypeName) {
    this.elementTypeName = elementTypeName;
    this.polymorphic = polymorphic;
  }

  public static AnonListTypeName of(boolean polymorphic, TypeName elementTypeName) {
    return new AnonListTypeName(polymorphic, elementTypeName); // TODO cache?
  }

  @Override
  public String toString() { // TODO cache?
    return (polymorphic ? "polymorphic list" : "list") + "[" + elementTypeName + "]";
  }

}
