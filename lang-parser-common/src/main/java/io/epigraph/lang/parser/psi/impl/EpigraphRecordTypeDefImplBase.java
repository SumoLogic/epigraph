package io.epigraph.lang.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import io.epigraph.lang.parser.psi.EpigraphRecordTypeDef;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import io.epigraph.lang.parser.psi.TypeKind;
import io.epigraph.lang.parser.psi.stubs.SchemaRecordTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class EpigraphRecordTypeDefImplBase extends EpigraphTypeDefImplBase<SchemaRecordTypeDefStub, EpigraphRecordTypeDef> implements EpigraphTypeDef {
  public EpigraphRecordTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  EpigraphRecordTypeDefImplBase(@NotNull SchemaRecordTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.RECORD;
  }

}
