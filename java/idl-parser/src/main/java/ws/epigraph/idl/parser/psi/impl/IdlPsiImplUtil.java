package ws.epigraph.idl.parser.psi.impl;

import ws.epigraph.idl.parser.psi.IdlQid;
import ws.epigraph.idl.parser.psi.IdlQn;
import ws.epigraph.idl.parser.psi.IdlQnSegment;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.NamingConventions;
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

  // qn --------------------------------------------
  @NotNull
  public static Qn getQn(@NotNull IdlQn e) {
    List<IdlQnSegment> fqnSegmentList = e.getQnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (IdlQnSegment segment : fqnSegmentList) {
      segments[idx++] = getCanonicalName(segment.getQid());
    }

    return new Qn(segments);
  }
}
