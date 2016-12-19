// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.EdlVarTypeDefStub;

public interface EdlVarTypeDef extends EdlTypeDef, StubBasedPsiElement<EdlVarTypeDefStub> {

  @Nullable
  EdlExtendsDecl getExtendsDecl();

  @Nullable
  EdlQid getQid();

  @Nullable
  EdlSupplementsDecl getSupplementsDecl();

  @Nullable
  EdlVarTypeBody getVarTypeBody();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getVartype();

  @NotNull
  List<EdlTypeDef> supplemented();

}
