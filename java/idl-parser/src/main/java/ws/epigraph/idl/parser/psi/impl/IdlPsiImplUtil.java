/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
