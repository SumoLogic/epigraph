package com.sumologic.epigraph.ideaplugin.schema.features.linepainter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.xdebugger.ui.DebuggerColors;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDefaultTagLinePainter extends EditorLinePainter {
  public static final Key<DefaultTagsInfo> DEFAULT_TAGS_INFO_KEY = Key.create("default.tags.info");
  public static final Key<Integer> ELEMENT_LINE = Key.create("default.tags.element.line");

  public static void clearForElement(@NotNull PsiElement element) {
    getDefaultTagsInfo(element.getProject()).clearForElement(element);
  }

  public static void add(@NotNull PsiElement element, @NotNull String text) {
    getDefaultTagsInfo(element.getProject()).add(element, text);
  }

  private static DefaultTagsInfo getDefaultTagsInfo(@NotNull Project project) {
    DefaultTagsInfo data = project.getUserData(DEFAULT_TAGS_INFO_KEY);
    if (data == null) {

      final DefaultTagsInfo newData = new DefaultTagsInfo();
      project.putUserData(DEFAULT_TAGS_INFO_KEY, newData);
      data = newData;

      project.getMessageBus().connect(project).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER,
          new FileEditorManagerAdapter() {
            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
              newData.clearForFile(file);
            }
          });
    }

    return data;
  }

  @Override
  public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
//    final Document doc = FileDocumentManager.getInstance().getDocument(file);
//    if (doc == null) return null;

    DefaultTagsInfo data = project.getUserData(DEFAULT_TAGS_INFO_KEY);
    if (data == null) return null;

    List<Pair<PsiElement, String>> pairs = data.get(file, lineNumber);
    if (pairs == null || pairs.isEmpty()) return null;

//    List<String> strings = Arrays.asList(new String[]{"     Line " + lineNumber});

    // todo customize attrs depending on light/dark color scheme, see XDebuggerEditorLinePainter

    return pairs.stream()
        .map(s -> new LineExtensionInfo(s.second, getNormalAttributes()))
        .collect(Collectors.toList());
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

  public static class DefaultTagsInfo {
    private Map<VirtualFile, Map<Integer, List<Pair<PsiElement, String>>>> data = new THashMap<>();

    @Nullable
    public List<Pair<PsiElement, String>> get(@NotNull VirtualFile file, int line) {
      Map<Integer, List<Pair<PsiElement, String>>> listMap = data.get(file);
      return listMap == null ? null : listMap.get(line);
    }

    public void add(@NotNull PsiElement element, @NotNull String text) {
      PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(element.getProject());
      Document document = psiDocumentManager.getDocument(element.getContainingFile());
      if (document != null) {
        int line = document.getLineNumber(element.getTextOffset());

        VirtualFile file = element.getContainingFile().getVirtualFile();
        Map<Integer, List<Pair<PsiElement, String>>> listMap = data.get(file);
        if (listMap == null) {
          listMap = new THashMap<>();
          data.put(file, listMap);
        }

        List<Pair<PsiElement, String>> strings = listMap.get(line);
        if (strings == null) {
          strings = new ArrayList<>();
          listMap.put(line, strings);
        }

        strings.add(new Pair<>(element, text));
        element.putUserData(ELEMENT_LINE, line);
      }
    }

    public void clearForFile(@NotNull VirtualFile file) {
      data.remove(file);
    }

    public void clearForElement(@NotNull PsiElement element) {
      VirtualFile file = element.getContainingFile().getVirtualFile();
      Integer line = element.getUserData(ELEMENT_LINE);
      if (line != null) {
        Map<Integer, List<Pair<PsiElement, String>>> listMap = data.get(file);
        if (listMap != null) {
          List<Pair<PsiElement, String>> pairs = listMap.get(line);
          if (pairs != null) {
            Iterator<Pair<PsiElement, String>> iterator = pairs.iterator();
            while (iterator.hasNext()) {
              Pair<PsiElement, String> pair = iterator.next();
              if (pair.getFirst().equals(element)) iterator.remove();
            }
            if (pairs.isEmpty())
              listMap.remove(line);
          }
        }
      }
    }
  }
}
