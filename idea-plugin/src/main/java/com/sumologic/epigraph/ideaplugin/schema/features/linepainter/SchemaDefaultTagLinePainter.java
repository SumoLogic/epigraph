package com.sumologic.epigraph.ideaplugin.schema.features.linepainter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDefaultTagLinePainter extends EditorLinePainter {
  public static final Key<DefaultTagsInfo> DEFAULT_TAGS_INFO_KEY = Key.create("default.tags.info");

  @NotNull
  public static DefaultTagsInfo getOrCreate(@NotNull Project project) {
    DefaultTagsInfo data = project.getUserData(DEFAULT_TAGS_INFO_KEY);
    if (data == null) {
      data = new DefaultTagsInfo();
      project.putUserData(DEFAULT_TAGS_INFO_KEY, data);
    }
    return data;
  }

  public static void clearForFile(@NotNull Project project, @NotNull VirtualFile file) {
    DefaultTagsInfo data = project.getUserData(DEFAULT_TAGS_INFO_KEY);
    if (data != null) {
      data.clear(file);
    }
  }

  public static void clearForElement(@NotNull PsiElement element) {
    Project project = element.getProject();
    DefaultTagsInfo data = project.getUserData(DEFAULT_TAGS_INFO_KEY);
    if (data != null) {
      PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
      Document document = psiDocumentManager.getDocument(element.getContainingFile());
      if (document != null) {
        int lineNumber = document.getLineNumber(element.getTextOffset());
        data.clear(element.getContainingFile().getVirtualFile(), lineNumber);
      }
    }
  }

  @Override
  public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
//    final Document doc = FileDocumentManager.getInstance().getDocument(file);
//    if (doc == null) return null;

    DefaultTagsInfo data = project.getUserData(DEFAULT_TAGS_INFO_KEY);
    if (data == null) return null;

    List<String> strings = data.get(file, lineNumber);
    if (strings == null || strings.isEmpty()) return null;

//    List<String> strings = Arrays.asList(new String[]{"     Line " + lineNumber});

    // todo customize attrs depending on light/dark color scheme, see XDebuggerEditorLinePainter

    return strings.stream()
        .map(s -> new LineExtensionInfo(s, Color.gray, null, null, Font.ITALIC))
        .collect(Collectors.toList());
  }

  public static class DefaultTagsInfo {
    private Map<VirtualFile, Map<Integer, List<String>>> data = new THashMap<>();

    @Nullable
    public List<String> get(@NotNull VirtualFile file, int line) {
      Map<Integer, List<String>> listMap = data.get(file);
      return listMap == null ? null : listMap.get(line);
    }

    public void add(@NotNull VirtualFile file, int line, @NotNull String text) {
      Map<Integer, List<String>> listMap = data.get(file);
      if (listMap == null) {
        listMap = new THashMap<>();
        data.put(file, listMap);
      }

      List<String> strings = listMap.get(line);
      if (strings == null) {
        strings = new ArrayList<>();
        listMap.put(line, strings);
      }

      strings.add(text);
    }

    public void clear(@NotNull VirtualFile file) {
      data.remove(file);
    }

    public void clear(@NotNull VirtualFile file, int line) {
      Map<Integer, List<String>> listMap = data.get(file);
      if (listMap != null) listMap.remove(line);
    }
  }
}
