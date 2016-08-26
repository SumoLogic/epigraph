package com.sumologic.epigraph.ideaplugin.schema.features.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import io.epigraph.lang.parser.psi.SchemaFieldDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class RenameSchemaFieldProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return element instanceof SchemaFieldDecl;
  }

  @Nullable
  @Override
  public PsiElement substituteElementToRename(PsiElement element, @Nullable Editor editor) {
    return SchemaRenameUtil.chooseSuper((PsiNamedElement) element);
  }

  //  interface A0 {
//    void foo();
//  }
//
//  interface A extends A0 {
//    void foo();
//  }
//
//  interface A2 {
//    void foo();
//  }
//
//  interface B extends A,A2 {
//    void foo();
//  }
}
