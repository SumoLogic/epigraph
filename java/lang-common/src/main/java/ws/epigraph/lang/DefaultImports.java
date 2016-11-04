package ws.epigraph.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DefaultImports {
  public static Qn[] DEFAULT_IMPORTS = new Qn[]{
      new Qn("epigraph", "String"),
      new Qn("epigraph", "Integer"),
      new Qn("epigraph", "Long"),
      new Qn("epigraph", "Double"),
      new Qn("epigraph", "Boolean"),
  };

  public static List<Qn> DEFAULT_IMPORTS_LIST = Collections.unmodifiableList(Arrays.asList(DEFAULT_IMPORTS));
}
