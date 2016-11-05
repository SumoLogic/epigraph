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

package ws.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.editorActions.moveUpDown.LineMover;
import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UnfairTextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.schema.parser.psi.SchemaFile;
import ws.epigraph.ideaplugin.schema.psi.SchemaPsiUtil;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaLineMover extends LineMover {
  @Override
  public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
    if (!(file instanceof SchemaFile) || !super.checkAvailable(editor, file, info, down)) return false;

    Pair<PsiElement, PsiElement> movedElementRange = getElementRange(editor, file, info.toMove);
    if (movedElementRange == null) return false;

    final Document document = editor.getDocument();

    PsiNamedElement moved = PsiTreeUtil.getParentOfType(movedElementRange.getFirst(), PsiNamedElement.class, false);
    if (moved == null || moved instanceof PsiFile) return false;

    if (moved != PsiTreeUtil.getParentOfType(movedElementRange.getSecond(), PsiNamedElement.class, false))
      return false;

    PsiElement movedParent = moved.getParent();

    LineRange toMoveOld = info.toMove;
    info.toMove = new LineRange(moved);

    Class<? extends PsiNamedElement> movedClass = moved instanceof SchemaTypeDef ? SchemaTypeDef.class : moved.getClass();

    if (down) {
      int delta = info.toMove.endLine - toMoveOld.endLine;
      if (delta > 0)
        info.toMove2 = new LineRange(info.toMove2.startLine + delta, Math.min(info.toMove2.endLine + delta, document.getLineCount() - 1));

      final TextRange moveDestinationRange = new UnfairTextRange(
          document.getLineStartOffset(info.toMove2.startLine),
          document.getLineEndOffset(info.toMove2.endLine - 1)
      );

      PsiElement updatedElement = file.findElementAt(moveDestinationRange.getEndOffset());
      if (updatedElement instanceof PsiWhiteSpace) updatedElement = PsiTreeUtil.prevLeaf(updatedElement);


      if (updatedElement != null) {
        @SuppressWarnings("unchecked")
        final PsiElement target = SchemaPsiUtil.getElementOrParentOfType(updatedElement, movedClass, PsiComment.class);

        if (target != null) {
          if (target.getParent() != movedParent) {
//            info.prohibitMove(); // disallows moving members between types
            return true;
          }

          final int offset = target.getTextRange().getStartOffset();
          updatedMovedIntoEnd(document, info, offset);

          if (moved != target)
            info.toMove2 = new LineRange(target);
        }
      }
    } else {
      int delta = toMoveOld.startLine - info.toMove.startLine;
      if (delta > 0)
        info.toMove2 = new LineRange(info.toMove2.startLine - delta, info.toMove2.endLine - delta);

      final TextRange moveDestinationRange = new UnfairTextRange(
          document.getLineStartOffset(info.toMove2.startLine),
          document.getLineEndOffset(info.toMove2.endLine - 1)
      );
      PsiElement updatedElement = file.findElementAt(moveDestinationRange.getStartOffset());
      if (updatedElement instanceof PsiWhiteSpace) updatedElement = PsiTreeUtil.nextLeaf(updatedElement);

      if (updatedElement != null) {
        @SuppressWarnings("unchecked")
        final PsiElement target = SchemaPsiUtil.getElementOrParentOfType(updatedElement, movedClass, PsiComment.class);

        if (target == null) {
          if (moved instanceof SchemaTypeDef)
            info.prohibitMove();
          return true;
        }
        if (target == moved) return true;
//        if (source.getParent() != movedParent) return false;

        info.toMove2 = new LineRange(target);
      }
    }

    return true;
  }

  private void updatedMovedIntoEnd(final Document document, @NotNull final MoveInfo info, final int offset) {
    if (offset + 1 < document.getTextLength()) {
      final int line = document.getLineNumber(offset + 1);
      final LineRange toMove2 = info.toMove2;
      if (toMove2 == null) return;
      info.toMove2 = new LineRange(toMove2.startLine, Math.min(Math.max(line, toMove2.endLine), document.getLineCount() - 1));
    }
  }
}
