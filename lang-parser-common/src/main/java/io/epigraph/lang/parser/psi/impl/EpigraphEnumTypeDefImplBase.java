package io.epigraph.lang.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import io.epigraph.lang.parser.psi.EpigraphEnumTypeDef;
import io.epigraph.lang.parser.psi.TypeKind;
import io.epigraph.lang.parser.psi.stubs.EpigraphEnumTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class EpigraphEnumTypeDefImplBase extends EpigraphTypeDefImplBase<EpigraphEnumTypeDefStub, EpigraphEnumTypeDef> implements EpigraphTypeDef {
  EpigraphEnumTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  EpigraphEnumTypeDefImplBase(@NotNull EpigraphEnumTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.ENUM;
  }
}
