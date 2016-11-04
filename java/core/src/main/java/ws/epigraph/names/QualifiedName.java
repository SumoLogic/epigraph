/* Created by yegor on 7/22/16. */

package ws.epigraph.names;

import ws.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class QualifiedName {

  private final @Nullable NamespaceName namespaceName;

  private final @NotNull String localName;

  protected QualifiedName(@Nullable NamespaceName namespaceName, @NotNull String localName) {
    this.namespaceName = namespaceName;
    this.localName = localName;
  }

  @NotNull
  public Qn toFqn() {
    if (namespaceName == null) return new Qn(localName);
    else return namespaceName.toFqn().append(localName);
  }

  /** Returns canonical string representation for this qualified name. */
  @Override
  public @NotNull String toString() {
    return namespaceName == null ? localName : namespaceName.toString() + '.' + localName;
  }

}
