package com.sumologic.epigraph.ideaplugin.schema.features.linepainter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDefaultTagLinePainter extends EditorLinePainter {
  @Override
  public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
    List<LineExtensionInfo> res = ContainerUtil.newSmartList();

    PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
    final Document doc = FileDocumentManager.getInstance().getDocument(file);
    if (doc != null) {
      PsiFile psiFile = psiDocumentManager.getPsiFile(doc);
      if (psiFile != null) {
        int offset = doc.getLineStartOffset(lineNumber);
        FileViewProvider viewProvider = psiFile.getViewProvider();

        PsiElement element = viewProvider.findElementAt(offset);
        while (element != null) {
          int startOffset = element.getTextRange().getStartOffset();
          if (startOffset >= doc.getTextLength()) break;

          int startOffsetLine = doc.getLineNumber(startOffset);

          if (startOffsetLine < lineNumber) {
            element = PsiTreeUtil.nextVisibleLeaf(element);
            continue;
          }

          if (startOffsetLine > lineNumber) break;

          try {
            element = addExtensions(res, element);
          } catch (IndexNotReadyException e) { element = null; }
        }
      }
    }

    return res;
  }

  @Nullable
  private PsiElement addExtensions(@NotNull List<LineExtensionInfo> res, @NotNull PsiElement element) {
    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(element, SchemaValueTypeRef.class);
    if (valueTypeRef == null) return PsiTreeUtil.nextVisibleLeaf(element);

    if (valueTypeRef.getDefaultOverride() == null) {
      SchemaVarTagDecl defaultTag = TypeMembers.getEffectiveDefault(valueTypeRef);
      if (defaultTag != null) {
        String defaultTagName = defaultTag.getQid().getText();
        String defaultTagTypeName = getDefaultTagTypeName(defaultTag);
        String typeAux = defaultTagTypeName == null ? "" : " (" + defaultTagTypeName + ")";
        String extension = " default " + defaultTagName + typeAux;
        res.add(new LineExtensionInfo(extension, getNormalAttributes()));
      }
    }

    return PsiTreeUtil.nextVisibleLeaf(valueTypeRef);
  }

  public static TextAttributes getNormalAttributes() {
    TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.INLINED_VALUES);
    if (attributes == null || attributes.getForegroundColor() == null) {
      return new TextAttributes(new JBColor(() -> isDarkEditor() ? new Color(0x3d8065) : Gray._135), null, null, null, Font.ITALIC);
    }
    return attributes;
  }

  private static boolean isDarkEditor() {
    Color bg = EditorColorsManager.getInstance().getGlobalScheme().getDefaultBackground();
    return ColorUtil.isDark(bg);
  }

  @Nullable
  private String getDefaultTagTypeName(@NotNull SchemaVarTagDecl defaultTag) {
    SchemaTypeRef defaultTagTypeRef = defaultTag.getTypeRef();
    String defaultTagTypeName = defaultTagTypeRef == null ? null : defaultTagTypeRef.getText();
    if (defaultTagTypeRef instanceof SchemaFqnTypeRef) {
      SchemaFqnTypeRef defaultTagFqnTypeRef = (SchemaFqnTypeRef) defaultTagTypeRef;
      SchemaTypeDef typeDef = defaultTagFqnTypeRef.resolve();
      if (typeDef != null) defaultTagTypeName = SchemaPresentationUtil.getName(typeDef, true);
    }
    return defaultTagTypeName;
  }
}
