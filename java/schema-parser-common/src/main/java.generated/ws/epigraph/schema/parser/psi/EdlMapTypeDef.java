// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.schema.parser.psi.stubs.EdlMapTypeDefStub;

public interface EdlMapTypeDef extends EdlTypeDef, StubBasedPsiElement<EdlMapTypeDefStub> {

  @NotNull
  EdlAnonMap getAnonMap();

  @Nullable
  EdlExtendsDecl getExtendsDecl();

  @Nullable
  EdlMapTypeBody getMapTypeBody();

  @Nullable
  EdlMetaDecl getMetaDecl();

  @Nullable
  EdlQid getQid();

  @Nullable
  EdlSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

}
