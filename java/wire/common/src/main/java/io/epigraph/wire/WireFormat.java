/* Created by yegor on 10/8/16. */

package io.epigraph.wire;

import org.jetbrains.annotations.NotNull;

public interface WireFormat {

  @NotNull String name();

  @NotNull String mimeType();

}
