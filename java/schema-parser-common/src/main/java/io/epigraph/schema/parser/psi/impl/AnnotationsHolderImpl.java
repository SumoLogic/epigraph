package io.epigraph.schema.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import io.epigraph.schema.parser.psi.AnnotationsHolder;
import io.epigraph.schema.parser.psi.SchemaAnnotation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class AnnotationsHolderImpl extends ASTWrapperPsiElement implements AnnotationsHolder {
  public AnnotationsHolderImpl(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public List<SchemaAnnotation> getAnnotationList() {
    throw new RuntimeException("Should never happen: " + getClass().getName());
  }
}
