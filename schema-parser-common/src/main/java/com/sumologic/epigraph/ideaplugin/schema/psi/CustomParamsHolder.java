package com.sumologic.epigraph.ideaplugin.schema.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface CustomParamsHolder extends PsiElement {
  @NotNull
  List<SchemaCustomParam> getCustomParamList();
}
