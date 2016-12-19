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

package ws.epigraph.ideaplugin.schema.highlighting;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.schema.parser.psi.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaOpOutputProjectionsAnnotator extends SchemaAnnotatorBase {
  @Override
  public void annotate(
      @NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {

    element.accept(new SchemaVisitor() {

      @Override
      public void visitOpOutputSingleTagProjection(@NotNull final SchemaOpOutputSingleTagProjection tp) {
        final SchemaTagName tagName = tp.getTagName();
        if (tagName != null) {
          setHighlighting(tagName, holder, SchemaSyntaxHighlighter.VAR_MEMBER);
          // todo validate tag reference
        }
      }

      @Override
      public void visitOpOutputMultiTagProjectionItem(@NotNull final SchemaOpOutputMultiTagProjectionItem tpe) {
        final SchemaTagName tagName = tpe.getTagName();
        setHighlighting(tagName, holder, SchemaSyntaxHighlighter.VAR_MEMBER);
        // todo validate tag reference
      }

      @Override
      public void visitOpOutputFieldProjectionEntry(@NotNull final SchemaOpOutputFieldProjectionEntry fpe) {
        final SchemaQid qid = fpe.getQid();
        setHighlighting(qid, holder, SchemaSyntaxHighlighter.FIELD);

        // todo validate field reference
      }
    });
  }
}
