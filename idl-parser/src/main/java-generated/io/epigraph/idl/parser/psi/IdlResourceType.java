// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlResourceType extends PsiElement {

  @Nullable
  IdlDefaultOverride getDefaultOverride();

  @NotNull
  IdlFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getColon();

}