package io.epigraph.util.pp;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DataPrettyPrinter {
  private static final int DEFAULT_LINE_WIDTH = 120;
  private static final int DEFAULT_INDENTATION = 2;

  @NotNull
  public static String prettyPrint(Object o) {
    return prettyPrint(o, DEFAULT_LINE_WIDTH, DEFAULT_INDENTATION);
  }

  @NotNull
  public static String prettyPrint(Object o, int lineWith, int indentation) {
//    StringBackend backend = new StringBackend(lineWith);
//    DataLayouter<NoExceptions> layouter = new DataLayouter<>(backend, indentation);

    MarkingStringBackend backend = new MarkingStringBackend(lineWith);
    MarkingDataLayouter layouter = new MarkingDataLayouter(backend, indentation);
    layouter.beginI(0);
    layouter.print(o);
    layouter.end();
    layouter.close();
    return backend.getString();
  }
}
