/* Created by yegor on 7/22/16. */

package io.epigraph.names;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QualifiedTypeName extends QualifiedName implements TypeName {

  public QualifiedTypeName(@Nullable NamespaceName namespaceName, @NotNull String localName) {
    super(namespaceName, localName);
  }

  public QualifiedTypeName(@NotNull String localName, @NotNull String... namespaceNames) {
    this(NamespaceName.from(namespaceNames), localName);
  }

}
