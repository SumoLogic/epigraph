/* Created by yegor on 7/22/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface StringIterable {

  @NotNull Iterator<String> toStrings();

}
