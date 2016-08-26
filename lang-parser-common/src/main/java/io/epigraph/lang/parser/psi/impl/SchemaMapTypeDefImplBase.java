package io.epigraph.lang.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import io.epigraph.lang.parser.psi.SchemaMapTypeDef;
import io.epigraph.lang.parser.psi.SchemaTypeDef;
import io.epigraph.lang.parser.psi.TypeKind;
import io.epigraph.lang.parser.psi.stubs.SchemaMapTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaMapTypeDefImplBase extends SchemaTypeDefImplBase<SchemaMapTypeDefStub, SchemaMapTypeDef> implements SchemaTypeDef {
  SchemaMapTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaMapTypeDefImplBase(@NotNull SchemaMapTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.MAP;
  }
}
