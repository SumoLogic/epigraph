/* Created by yegor on 7/22/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;

public interface TypeName {

  /** Must return canonical string representation for this type name. */
  @Override
  @NotNull String toString();

  // TODO equals/hashCode for the hierarchy

}
