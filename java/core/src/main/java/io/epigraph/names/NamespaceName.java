/* Created by yegor on 7/22/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class NamespaceName extends QualifiedName {

  public NamespaceName(@Nullable NamespaceName namespaceName, String localName) {
    super(namespaceName, localName);
  }

  public static @Nullable NamespaceName from(@NotNull String... segments) {
    switch (segments.length) {
      case 0:
        return null;
      default:
        return new NamespaceName(from(Arrays.copyOf(segments, segments.length - 1)), segments[segments.length - 1]);
    }
  }

}
