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

package ws.epigraph.ideaplugin.edl.highlighting;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.edl.parser.psi.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlOpOutputProjectionsAnnotator extends EdlAnnotatorBase {
  @Override
  public void annotate(
      @NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {

    element.accept(new EdlVisitor() {

      @Override
      public void visitOpOutputSingleTagProjection(@NotNull final EdlOpOutputSingleTagProjection tp) {
        final EdlTagName tagName = tp.getTagName();
        if (tagName != null) {
          setHighlighting(tagName, holder, EdlSyntaxHighlighter.VAR_MEMBER);
          // todo validate tag reference
        }
      }

      @Override
      public void visitOpOutputMultiTagProjectionItem(@NotNull final EdlOpOutputMultiTagProjectionItem tpe) {
        final EdlTagName tagName = tpe.getTagName();
        setHighlighting(tagName, holder, EdlSyntaxHighlighter.VAR_MEMBER);
        // todo validate tag reference
      }

      @Override
      public void visitOpOutputFieldProjectionEntry(@NotNull final EdlOpOutputFieldProjectionEntry fpe) {
        final EdlQid qid = fpe.getQid();
        setHighlighting(qid, holder, EdlSyntaxHighlighter.FIELD);

        // todo validate field reference
      }
    });
  }
}
