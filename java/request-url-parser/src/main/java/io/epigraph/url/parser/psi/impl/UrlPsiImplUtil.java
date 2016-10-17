package io.epigraph.url.parser.psi.impl;

import io.epigraph.lang.NamingConventions;
import io.epigraph.lang.Qn;
import io.epigraph.url.parser.psi.UrlQid;
import io.epigraph.url.parser.psi.UrlQn;
import io.epigraph.url.parser.psi.UrlQnSegment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlPsiImplUtil {
  @Contract(pure = true)
  @NotNull
  public static String getName(UrlQid qid) {
    return qid.getId().getText();
  }

  @Contract(pure = true)
  @NotNull
  public static String getCanonicalName(UrlQid qid) {
    String name = getName(qid);
    return NamingConventions.unquote(name);
  }

  // qn --------------------------------------------
  @NotNull
  public static Qn getQn(@NotNull UrlQn e) {
    List<UrlQnSegment> fqnSegmentList = e.getQnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (UrlQnSegment segment : fqnSegmentList) {
      segments[idx++] = getCanonicalName(segment.getQid());
    }

    return new Qn(segments);
  }
}
