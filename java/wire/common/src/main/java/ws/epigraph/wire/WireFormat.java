/* Created by yegor on 10/8/16. */

package ws.epigraph.wire;

import org.jetbrains.annotations.NotNull;

public interface WireFormat {

  @NotNull String name();

  @NotNull String mimeType();

}
