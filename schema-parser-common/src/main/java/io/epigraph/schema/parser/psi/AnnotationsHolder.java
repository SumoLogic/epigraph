package io.epigraph.schema.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface AnnotationsHolder extends PsiElement {
  @NotNull
  List<SchemaAnnotation> getAnnotationsList();
}
