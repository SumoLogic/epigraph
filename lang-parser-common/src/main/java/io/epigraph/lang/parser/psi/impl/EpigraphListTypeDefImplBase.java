package io.epigraph.lang.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import io.epigraph.lang.parser.psi.EpigraphListTypeDef;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import io.epigraph.lang.parser.psi.TypeKind;
import io.epigraph.lang.parser.psi.stubs.EpigraphListTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class EpigraphListTypeDefImplBase extends EpigraphTypeDefImplBase<EpigraphListTypeDefStub, EpigraphListTypeDef> implements EpigraphTypeDef {
  EpigraphListTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  EpigraphListTypeDefImplBase(@NotNull EpigraphListTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.LIST;
  }
}
