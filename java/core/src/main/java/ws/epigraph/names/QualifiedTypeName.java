/* Created by yegor on 7/22/16. */

package ws.epigraph.names;

import ws.epigraph.data.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class QualifiedTypeName extends QualifiedName implements TypeName, Immutable {

  private @Nullable String toString = null;

  public QualifiedTypeName(@Nullable NamespaceName namespaceName, @NotNull String localName) {
    super(namespaceName, localName);
  }

  public QualifiedTypeName(@NotNull String localName, @NotNull String... namespaceNames) {
    this(NamespaceName.from(namespaceNames), localName);
  }

  @Override
  public @NotNull String toString() {
    if (toString == null) toString = super.toString();
    return toString;
  }

}
