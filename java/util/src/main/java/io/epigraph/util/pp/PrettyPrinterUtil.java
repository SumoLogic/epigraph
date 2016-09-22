package io.epigraph.util.pp;

import de.uka.ilkd.pp.DataLayouter;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PrettyPrinterUtil {
  public static <E extends Exception> void printWithBrkIfNonEmpty(DataLayouter<E> l, Object obj)
      throws E {

    @NotNull String s = DataPrettyPrinter.prettyPrint(obj);
    if (!s.isEmpty()) l.brk();
    l.print(obj);
  }
}
