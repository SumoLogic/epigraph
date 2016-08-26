// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphRecordTypeDefStub;

public interface EpigraphRecordTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphRecordTypeDefStub> {

  @Nullable
  EpigraphExtendsDecl getExtendsDecl();

  @Nullable
  EpigraphMetaDecl getMetaDecl();

  @Nullable
  EpigraphQid getQid();

  @Nullable
  EpigraphRecordTypeBody getRecordTypeBody();

  @Nullable
  EpigraphSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getRecord();

  @NotNull
  List<EpigraphTypeDef> supplemented();

}
