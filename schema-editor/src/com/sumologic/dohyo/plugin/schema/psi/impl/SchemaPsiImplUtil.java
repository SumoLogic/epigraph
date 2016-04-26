package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.psi.PsiElement;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqn;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqnTypeRef;
import com.sumologic.dohyo.plugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.S_ID;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtil {
  @NotNull
  public static List<PsiElement> getSegments(SchemaFqn e) {
    List<PsiElement> res = new ArrayList<>();
    for (PsiElement child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNode().getElementType() == S_ID) {
        res.add(child);
      }
    }
    return res;
  }

  @NotNull
  public static String getFqnString(SchemaFqn e) {
    StringBuilder res = new StringBuilder();

    for (PsiElement psiElement : getSegments(e)) {
      if (res.length() > 0) res.append('.');
      res.append(psiElement.getText().trim());
    }

    return res.toString();
  }

  public static String getName(SchemaTypeDef schemaTypeDef) {
    PsiElement id = schemaTypeDef.getId();
    return id == null ? null : id.getText();
  }

  public static PsiElement setName(SchemaTypeDef schemaTypeDef, String name) {
    PsiElement id = schemaTypeDef.getId();
    if (id == null) return null;
    else {
      PsiElement newId = SchemaElementFactory.createId(name);
      id.replace(newId);
      return id;
    }
  }

  public static String getName(SchemaFqnTypeRef fqnTypeRef) {
    // TODO return full dotted notation as name
    SchemaFqn fqn = fqnTypeRef.getFqn();
    List<PsiElement> segments = fqn.getSegments();
    return segments.isEmpty() ? null : segments.get(segments.size() - 1).getText();
  }

  public static PsiElement setName(SchemaFqnTypeRef fqnTypeRef, String name) {
    SchemaFqn oldFqn = fqnTypeRef.getFqn();
    SchemaFqn newFqn = SchemaElementFactory.createFqn(fqnTypeRef.getProject(), name);
    oldFqn.replace(newFqn);
    return oldFqn;
  }
}
