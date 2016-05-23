package com.sumologic.epigraph.ideaplugin.schema.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaTypeDef extends PsiNameIdentifierOwner {
  PsiElement getId();

  @Nullable
  String getName();

  @Nullable
  PsiElement setName(@NotNull String name);

  @Nullable
  PsiElement getNameIdentifier();

  int getTextOffset();

  @Nullable
  PsiElement getAbstract() ;

  @Nullable
  PsiElement getPolymorphic() ;

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @NotNull
  TypeKind getKind();

  Icon getIcon(int flags);

  @NotNull
  List<SchemaTypeDef> parents();

  @Override
  void delete() throws IncorrectOperationException;
}
