package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.sumologic.epigraph.ideaplugin.schema.psi.CustomParamsHolder;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaCustomParam;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class CustomParamHolderImpl extends ASTWrapperPsiElement implements CustomParamsHolder {
  public CustomParamHolderImpl(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public List<SchemaCustomParam> getCustomParamList() {
    throw new RuntimeException("Should never happen");
  }
}
