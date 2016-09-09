package io.epigraph.idl.parser.psi.impl;

import io.epigraph.idl.parser.psi.IdlFqn;
import io.epigraph.idl.parser.psi.IdlFqnSegment;
import io.epigraph.idl.parser.psi.IdlQid;
import io.epigraph.lang.Fqn;
import io.epigraph.lang.NamingConventions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlPsiImplUtil {
  // qid --------------------------------------------

//  @Contract(pure = true)
//  @NotNull
//  public static PsiElement setName(IdlQid qid, String name) {
//    PsiElement oldId = qid.getId();
//    PsiElement newId = IdlElementFactory.createId(qid.getProject(), name);
//    oldId.replace(newId);
//    return qid;
//  }

  @Contract(pure = true)
  @NotNull
  public static String getName(IdlQid qid) {
    return qid.getId().getText();
  }

  @Contract(pure = true)
  @NotNull
  public static String getCanonicalName(IdlQid qid) {
    String name = getName(qid);
    return NamingConventions.unquote(name);
  }

  // fqn --------------------------------------------
  @NotNull
  public static Fqn getFqn(@NotNull IdlFqn e) {
    List<IdlFqnSegment> fqnSegmentList = e.getFqnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (IdlFqnSegment segment : fqnSegmentList) {
      segments[idx++] = segment.getQid().getCanonicalName();
    }

    return new Fqn(segments);
  }
}
