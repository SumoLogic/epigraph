package ws.epigraph.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import ws.epigraph.schema.parser.psi.SchemaRecordTypeDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import ws.epigraph.schema.parser.psi.TypeKind;
import ws.epigraph.schema.parser.psi.stubs.SchemaRecordTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract public class SchemaRecordTypeDefImplBase extends SchemaTypeDefImplBase<SchemaRecordTypeDefStub, SchemaRecordTypeDef> implements SchemaTypeDef {
  public SchemaRecordTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaRecordTypeDefImplBase(@NotNull SchemaRecordTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.RECORD;
  }

}
