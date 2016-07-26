/* Created by yegor on 7/22/16. */

package io.epigraph.names;

import org.jetbrains.annotations.Nullable;

public class QualifiedTypeName extends QualifiedName implements TypeName {

  public QualifiedTypeName(@Nullable NamespaceName namespaceName, String localName) {
    super(namespaceName, localName);
  }

}
